package com.example.soccergroup.controller;

import com.example.soccergroup.controller.dto.GetSoccerGroupDetailResponseDto;
import com.example.soccergroup.controller.dto.SearchSoccerGroupRequestDto;
import com.example.soccergroup.controller.dto.UpdateGroupDescriptionRequestDto;
import com.example.soccergroup.controller.dto.SoccerGroupsDto.SoccerGroupsResponseDto;
import com.example.soccergroup.controller.dto.requestDto.RequestsResponseDto;
import com.example.soccergroup.controller.util.ObjectConverter;
import com.example.soccergroup.controller.util.VerifyTokenResult;
import com.example.soccergroup.exception.JoinRequestCreationException;
import com.example.soccergroup.exception.SoccerGroupNotFoundException;
import com.example.soccergroup.repository.entity.SoccerGroup;
import com.example.soccergroup.service.SoccerGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.net.URI;
import java.util.List;

import static com.example.soccergroup.config.GlobalStaticVariable.AUTH_CREDENTIALS;

@RestController
@RequiredArgsConstructor
@RequestMapping("soccer-group")
public class SoccerGroupController {

    private final SoccerGroupService soccerGroupService;

    @GetMapping
    Mono<ResponseEntity<SoccerGroupsResponseDto>> recommendSoccerGroups(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);
        Pageable pageable = PageRequest.of(page, size);

        return soccerGroupService
                .recommendSoccerGroups(verifyTokenResult, pageable)
                .collectList()
                .map(soccerGroups -> {
                    return ResponseEntity.ok(new SoccerGroupsResponseDto(soccerGroups));
                });
    }

    @PostMapping("get-my-soccer-group")
    Mono<ResponseEntity<SoccerGroupsResponseDto>> getMySoccerGroups(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    )
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);
        Pageable pageable = PageRequest.of(page, size);

        return soccerGroupService
                .getSoccerGroupsByUserId(verifyTokenResult.getUser_id(), pageable)
                .collectList()
                .map(soccerGroups -> {
                    return ResponseEntity.ok(new SoccerGroupsResponseDto(soccerGroups));
                });
    }

    @GetMapping("{groupId}")
    Mono<ResponseEntity<GetSoccerGroupDetailResponseDto>> getSoccerGroupDetail(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @PathVariable Long groupId)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);

        return soccerGroupService
                .getSoccerGroupDetail(groupId)
                .switchIfEmpty(Mono.defer(() ->{
                    throw new SoccerGroupNotFoundException("soccer group with id " + String.valueOf(groupId) + " not found!");
                }))
                .map(tup ->{
                    SoccerGroup soccerGroup = tup.getT1();
                    List<Long> members = tup.getT2();
                    return ResponseEntity.ok(
                            new GetSoccerGroupDetailResponseDto(soccerGroup, members));
                });
    }

    @PostMapping("search")
    Mono<ResponseEntity<SoccerGroupsResponseDto>> searchSoccerGroups(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @Valid @RequestBody SearchSoccerGroupRequestDto searchSoccerGroupRequestDto,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);
        Pageable pageable = PageRequest.of(page, size);

        return soccerGroupService
                .searchSoccerGroups(searchSoccerGroupRequestDto.getKeyword(), pageable)
                .collectList()
                .map(soccerGroups -> {
                    return ResponseEntity.ok(new SoccerGroupsResponseDto(soccerGroups));
                });
    }


    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    Mono<ResponseEntity> createSoccerGroup(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @NotBlank(message="picture should not be blank") @RequestPart("picture") FilePart picture,
            @NotBlank(message="group name should not be blank") @Size(min = 3, max=40) @RequestPart("name") String groupName,
            @NotBlank(message="group description should not be blank") @RequestPart("description") String groupDescription)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);

        return soccerGroupService.saveSoccerGroup(picture, verifyTokenResult, groupName, groupDescription).map(
                soccerGroup -> {
                    return ResponseEntity.created(URI.create("/soccer-group/" + String.valueOf(soccerGroup.getGroupId()))).build();
                }
        );
    }

    @PutMapping(value = "{groupId}/profile-picture", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    Mono<Void> updateProfilePicture(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @PathVariable long groupId,
            @NotBlank(message="picture should not be blank") @RequestPart("picture") FilePart picture)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);

        return soccerGroupService
                .getSoccerGroupById(groupId)
                .switchIfEmpty(Mono.defer(()->{
                    throw new SoccerGroupNotFoundException("soccer group with id " + String.valueOf(groupId) + " is not found!");
                }))
                .flatMap( soccerGroup->{

                    //in production uncomment code below

                    /*
                    Long currentUserId = verifyTokenResult.getUser_id();
                    if(soccerGroup.getOwnerId().equals(currentUserId))
                    {
                        throw new UnAuthorizedActionException("user with " + String.valueOf(currentUserId) + " is not the owner of soccer group of id " + String.valueOf(soccerGroup.getGroupId()));
                    }

                     */

                    String oldPath = soccerGroup.getGroupProfileImgUrl();

                    soccerGroupService.updateProfilePict(picture, groupId, oldPath).subscribeOn(Schedulers.boundedElastic()).subscribe();
                    return Mono.empty();
                });
    }

    @PutMapping("{groupId}/group-description")
    Mono<Void> updateGroupDescription(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @PathVariable long groupId,
            @Valid @RequestBody UpdateGroupDescriptionRequestDto updateGroupDescriptionDto)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);


        return soccerGroupService
                .getSoccerGroupById(groupId)
                .switchIfEmpty(Mono.defer(()->{
                    throw new SoccerGroupNotFoundException("soccer group with id " + String.valueOf(groupId) + " is not found!");
                }))
                .flatMap( soccerGroup->{
                    Long currentUserId = verifyTokenResult.getUser_id();

                    //in production, uncomment the codes below
                    /*
                    if(soccerGroup.getOwnerId().equals(currentUserId))
                    {
                        throw new UnAuthorizedActionException("user with " + String.valueOf(currentUserId) + " is not the owner of soccer group of id " + String.valueOf(soccerGroup.getGroupId()));
                    }

                     */

                    return soccerGroupService.updateGroupDescription(updateGroupDescriptionDto.getGroup_description(), groupId);
                });
    }

    @PostMapping("{groupId}/join-requests")
    Mono<Void> createJoinRequests(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @PathVariable long groupId
            )
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);


        return soccerGroupService
                .checkIfJoinRequestExists(groupId, verifyTokenResult.getUser_id())
                .flatMap(re ->{
                    if(re)
                    {
                        throw new JoinRequestCreationException("already created the join request!");
                    }

                    return soccerGroupService.createJoinRequest(groupId, verifyTokenResult.getUser_id());
                })
                .doOnError(ex -> {
                    if(ex instanceof DataIntegrityViolationException) {
                        throw new SoccerGroupNotFoundException("soccer group not found!");
                    }
                })
                .flatMap(
                        joinRequest -> {
                            return Mono.empty();
                        }
                );


    }

    @GetMapping("{groupId}/join-requests")
    Mono<ResponseEntity<RequestsResponseDto>> getJoinRequestByGroupId(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @PathVariable long groupId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);

        Pageable pageable = PageRequest.of(page, size);

        return soccerGroupService.getJoinRequestByGroupId(groupId, pageable)
                .collectList()
                .map(joinRequests -> {
                    return ResponseEntity.ok(new RequestsResponseDto(joinRequests));
                });
    }

    @PostMapping("{groupId}/join-requests/{requestId}/approve")
    Mono<Void> approveJoinRequest(@RequestHeader(AUTH_CREDENTIALS) String authStr,
                                  @PathVariable long groupId,
                                  @PathVariable long requestId)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);

        return soccerGroupService.approveJoinRequest(requestId, groupId, verifyTokenResult.getUser_id());
    }

    @DeleteMapping("{groupId}/join-requests/{requestId}")
    Mono<Void> deleteJoinRequest(@RequestHeader(AUTH_CREDENTIALS) String authStr,
                                 @PathVariable long groupId,
                                 @PathVariable long requestId)
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);

        return soccerGroupService.deleteJoinRequest(requestId, verifyTokenResult.getUser_id());
    }

    @PostMapping("join-requests/get-my-requests")
    Mono<ResponseEntity<RequestsResponseDto>> getMyRequests(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    )
    {
        VerifyTokenResult verifyTokenResult = ObjectConverter.convertAuthCredentials(authStr);

        Pageable pageable = PageRequest.of(page, size);

        return soccerGroupService.getJoinRequestByUserId(verifyTokenResult.getUser_id(), pageable)
                .collectList()
                .map(joinRequests -> {
                    return ResponseEntity.ok(new RequestsResponseDto(joinRequests));
                });
    }

}
