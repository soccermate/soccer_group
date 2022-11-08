package com.example.soccergroup;

import com.example.soccergroup.controller.util.ObjectConverter;
import com.example.soccergroup.controller.util.VerifyTokenResult;
import com.example.soccergroup.repository.JoinRequestRepository;
import com.example.soccergroup.repository.MembersRepository;
import com.example.soccergroup.repository.SoccerGroupRepository;
import com.example.soccergroup.repository.entity.JoinRequest;
import com.example.soccergroup.repository.entity.SoccerGroup;
import com.example.soccergroup.service.SoccerGroupService;
import com.example.soccergroup.util.feign.UserFeignClient;
import com.example.soccergroup.util.feign.dto.GetUserResponseDto;
import com.example.soccergroup.util.pictureUtil.PictureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.awt.print.Pageable;
import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
@EnableConfigurationProperties
@ActiveProfiles(profiles = "dev")
class SoccerGroupApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	PictureUtil pictureUploader;

	@Autowired
	JoinRequestRepository joinRequestRepository;

	@Autowired
	MembersRepository membersRepository;

	@Autowired
	SoccerGroupRepository soccerGroupRepository;

	@Test
	void memberRepositoryTest()
	{
		Mono<Void> res = membersRepository.saveMember(Long.valueOf(4), Long.valueOf(2));

		StepVerifier.create(res).expectError().verify();
	}

	@Test
	void memberRepositoryTest1()
	{
		Flux<Long> members = membersRepository.getMembers(Long.valueOf(1));

		members.map(r ->{
			log.info(String.valueOf(r));
			return r;
		}).subscribe();

	}

	@Test
	void testJoinRequest()
		{
			PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("requestTime").descending());
			Flux<JoinRequest> joinRequestFlux = joinRequestRepository.findByGroupId(Long.valueOf(1), pageRequest);

			joinRequestFlux.log().subscribe();
		}

	@Test
	void testJoinRequestExist()
	{
		Mono<Boolean> b = membersRepository.existsByGroupIdAndUserId(Long.valueOf(10),Long.valueOf( 5));

		StepVerifier.create(b).expectNext(false);
	}

	@Test
	void testJoinRequest1()
	{
		JoinRequest joinRequest = JoinRequest.builder()
				.groupId(Long.valueOf(1))
				.userId(Long.valueOf(2))
				.requestTime(LocalDateTime.now())
				.build();

		Mono<JoinRequest> joinRequestFlux = joinRequestRepository.save(joinRequest);

		joinRequestFlux.log().subscribe();
	}


	@Test
	void setSoccerGroupRepositoryTest()
	{
		PageRequest pageRequest = PageRequest.of(0, 3);
		Flux<SoccerGroup> soccerGroupFlux = soccerGroupRepository.findByGroupNameContains("h", pageRequest.getPageNumber() * pageRequest.getPageSize(), pageRequest.getPageSize() );

		Flux<SoccerGroup> newS = soccerGroupFlux.map(soccerGroup -> {

			log.info(soccerGroup.toString());
			return soccerGroup;

		});

		newS.subscribe();

	}

	@Autowired
	SoccerGroupService soccerGroupService;

	@Test
	void soccerGroupRepoTest()
	{

	}

	@Test
	void testPictureUploader()
	{

	}


	@Autowired
	UserFeignClient userFeignClient;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void testUserFeign() throws Exception
	{
		VerifyTokenResult verifyTokenResult = new VerifyTokenResult(6, "USER", true);
		Mono<GetUserResponseDto> re = userFeignClient.getUser(objectMapper.writeValueAsString(verifyTokenResult));

		StepVerifier.create(re).expectComplete().log();
	}



}
