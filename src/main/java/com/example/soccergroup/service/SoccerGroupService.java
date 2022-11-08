package com.example.soccergroup.service;

import com.example.soccergroup.controller.util.VerifyTokenResult;
import com.example.soccergroup.exception.JoinRequestApproveException;
import com.example.soccergroup.exception.JoinRequestCreationException;
import com.example.soccergroup.exception.ResourceNotFoundException;
import com.example.soccergroup.exception.UnAuthorizedActionException;
import com.example.soccergroup.repository.JoinRequestRepository;
import com.example.soccergroup.repository.MembersRepository;
import com.example.soccergroup.repository.SoccerGroupRepository;
import com.example.soccergroup.repository.entity.JoinRequest;
import com.example.soccergroup.repository.entity.SoccerGroup;
import com.example.soccergroup.service.dto.SoccerGroupServiceDto;
import com.example.soccergroup.util.feign.UserFeignClient;
import com.example.soccergroup.util.feign.dto.GetUserResponseDto;
import com.example.soccergroup.util.pictureUtil.PictureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SoccerGroupService {
    private static final String DEFAULT_REGION = "서울시 성동구";

    private final SoccerGroupRepository soccerGroupRepository;
    private final PictureUtil pictureUtil;
    private final UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;
    private final MembersRepository membersRepository;
    private final JoinRequestRepository joinRequestRepository;




    @Transactional("r2dbcTransactionManager")
    public Mono<SoccerGroup> saveSoccerGroup(FilePart filePart, VerifyTokenResult verifyTokenResultDto, String groupName, String groupDescription)
    {
        Long userId = verifyTokenResultDto.getUser_id();
        //upload Picture
        Mono<String> uploadResult = pictureUtil.uploadUserProfilePict(filePart, userId);

        //get File Path to save in RDS
        String path = pictureUtil.getFilePath(filePart, userId);
        log.info(path);

        Mono<SoccerGroup> soccerGroupMono =
                getUserResponseDtoMono(verifyTokenResultDto)
                        .flatMap(userResponseDto -> {
                            log.debug("done getting value from User service!");

                            log.debug(userResponseDto.toString());

                            GetUserResponseDto getUserResponseDto = userResponseDto;
                            String region = getUserResponseDto.getRegion();
                            SoccerGroup newSoccerGroup = SoccerGroup.builder()
                                    .groupRegion(region)
                                    .groupPoint(Long.valueOf(0))
                                    .groupDescription(groupDescription)
                                    .groupName(groupName)
                                    .ownerId(userId)
                                    .groupProfileImgUrl(path)
                                    .build();

                            return soccerGroupRepository.save(newSoccerGroup)
                                    .flatMap(soccerGroup -> {
                                        return membersRepository.saveMember(userId, soccerGroup.getGroupId())
                                                .then(Mono.just(soccerGroup));
                                    });
                        });

        return Flux.zip(uploadResult, soccerGroupMono).map(
                tup ->{
                    log.debug("both tasks are done!");
                    return tup.getT2();
                }
        ).next();
    }

    @Transactional("r2dbcTransactionManager")
    public Mono<Void> updateProfilePict(FilePart filePart, Long groupId, String oldPath)
    {

        Mono<String> publisherToComplete;
        if(oldPath == null || oldPath.length() < 20)
        {
            publisherToComplete = pictureUtil.uploadUserProfilePict(filePart, groupId);
        }
        else{
            publisherToComplete = pictureUtil.deletePicture(oldPath).flatMap(
                    deleteObjectResponse -> {
                        return pictureUtil.uploadUserProfilePict(filePart, groupId);
                    }
            );
        }

        return publisherToComplete.flatMap(path -> {
            return soccerGroupRepository.updateProfileImgPath(path, groupId);
        });
    }

    @Transactional("r2dbcTransactionManager")
    public Mono<Void> updateGroupDescription(String groupDescription, Long groupId)
    {
        return soccerGroupRepository.updateGroupDescription(groupDescription, groupId);
    }

    public Mono<SoccerGroup> getSoccerGroupById(Long groupId)
    {
        return soccerGroupRepository.findById(groupId);
    }

    public Flux<SoccerGroupServiceDto> recommendSoccerGroups(VerifyTokenResult verifyTokenResult, Pageable pageable)
    {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();

        return Flux.from(getUserResponseDtoMono(verifyTokenResult))
                .flatMap(getUserResponseDto -> {
                    String delimiter = " ";
                    String[] tempArr = getUserResponseDto.getRegion().split(delimiter);
                    String region = DEFAULT_REGION;


                    if(tempArr.length >=2)
                    {
                        region = tempArr[0] + delimiter +tempArr[1];
                    }

                    log.info("region to find: " + region);
                    return soccerGroupRepository.recommendSoccerGroup(region, offset, limit)
                            .flatMap(soccerGroup -> {

                                return membersRepository.getMembers(soccerGroup.getGroupId())
                                        .collectList()
                                        .map(members ->{
                                            return new SoccerGroupServiceDto(soccerGroup, members);
                                        });
                            });

                });

    }

    public Flux<SoccerGroupServiceDto> searchSoccerGroups(String keyword, Pageable pageable)
    {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();
        return soccerGroupRepository.findByGroupNameContains(keyword, offset, limit)
                .flatMap(soccerGroup -> {

                    return membersRepository.getMembers(soccerGroup.getGroupId())
                            .collectList()
                            .map(members ->{
                                return new SoccerGroupServiceDto(soccerGroup, members);
                            });
                });
    }

    public Mono<Tuple2<SoccerGroup, List<Long>>> getSoccerGroupDetail(Long groupId)
    {
        Mono<SoccerGroup> soccerGroupMono = soccerGroupRepository.findById(groupId);
        Mono<List<Long>> members = membersRepository.getMembers(groupId).collectList();

        return Mono.zip(soccerGroupMono, members);
    }

    @Transactional("r2dbcTransactionManager")
    public Mono<JoinRequest> createJoinRequest(Long groupId,
                                               Long userId)
    {
        JoinRequest joinRequest = JoinRequest.builder()
                .requestTime(LocalDateTime.now())
                .groupId(groupId)
                .userId(userId)
                .build();

        return checkIfUserInGroup(userId, groupId).flatMap(
                result -> {
                    if(result){
                        throw new JoinRequestCreationException("the user is already in group");
                    }
                    return joinRequestRepository.save(joinRequest);
                }
        );
    }

    public Mono<Boolean> checkIfJoinRequestExists(Long groupId, Long userId)
    {
        return joinRequestRepository.existsByUserIdAndGroupId(userId, groupId);
    }

    public Flux<JoinRequest> getJoinRequestByGroupId(Long groupId,Pageable pageable)
    {
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("requestTime").descending());
        return joinRequestRepository.findByGroupId(groupId,sortPageable );
    }


    @Transactional("r2dbcTransactionManager")
    public Mono<Void> approveJoinRequest(Long requestId, Long groupId, Long userId)
    {
        return joinRequestRepository.findById(requestId)
                .flatMap(joinRequest -> {
                    if(joinRequest.getUserId().equals(userId) && joinRequest.getGroupId().equals(groupId))
                    {
                      return joinRequestRepository.delete(joinRequest);
                    }

                    throw new JoinRequestApproveException("userId and groupId is not matching!");
                })
                .then(membersRepository.saveMember(userId, groupId));
    }

    @Transactional("r2dbcTransactionManager")
    public Mono<Void> deleteJoinRequest(Long requestId, Long userId)
    {
        return joinRequestRepository.findById(requestId)
                .switchIfEmpty(Mono.defer(()->{
                    throw new ResourceNotFoundException("join request does not exists!");
                }))
                .flatMap(joinRequest -> {
                    return checkIfUserIsOwner(userId, joinRequest.getRequestId())
                            .map(re ->{

                                //check if user is allowed to delete request

                                if(!re){
                                    throw new UnAuthorizedActionException("the user is not allowed to delete join request");
                                }
                                return joinRequest;
                            });
                })
                .flatMap(joinRequest -> {
                    //delete request
                   return joinRequestRepository.delete(joinRequest);
                });
    }


    public Flux<JoinRequest> getJoinRequestByUserId(Long userId, Pageable pageable)
    {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(), Sort.by("requestTime").descending());

        return joinRequestRepository.findByUserId(userId, sortedPageable);
    }

    public Flux<SoccerGroupServiceDto> getSoccerGroupsByUserId(Long userId, Pageable pageable)
    {
        return soccerGroupRepository.findByUserId(userId,
                pageable.getPageNumber() * pageable.getPageSize(),
                pageable.getPageSize())
                .flatMap(soccerGroup -> {

                    return membersRepository.getMembers(soccerGroup.getGroupId())
                            .collectList()
                            .map(members ->{
                                return new SoccerGroupServiceDto(soccerGroup, members);
                            });
                });
    }


    //--------------------------- private methods -----------------------------------------------------------------

    private Mono<Boolean> checkIfUserInGroup(Long userId , Long groupId)
    {
        return membersRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    private Mono<Boolean> checkIfUserIsOwner(Long userId, Long groupId)
    {
        return soccerGroupRepository.existsByOwnerIdAndGroupId(userId, groupId);
    }

    @SneakyThrows
    private Mono<GetUserResponseDto> getUserResponseDtoMono(VerifyTokenResult verifyTokenResult)
    {
        return userFeignClient.getUser(objectMapper.writeValueAsString(verifyTokenResult));
    }




}
