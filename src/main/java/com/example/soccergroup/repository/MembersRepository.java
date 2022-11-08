package com.example.soccergroup.repository;

import com.example.soccergroup.repository.entity.members.Members;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MembersRepository extends ReactiveCrudRepository<Members, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO MEMBERS(user_id, group_id) VALUES(:userId, :groupId)")
    Mono<Void> saveMember(@Param("userId") Long userId, @Param("groupId") Long groupId);

    /*
    @Query("SELECT COUNT(*) FROM MEMBERS WHERE group_id = :groupId")
    Mono<Integer> getMemberCount(@Param("groupId") Long groupId);
     */

    //@Query("select case when count(*) > 0 then 1 else 0 end exist from members where group_id = :groupId AND user_id = :userId;")
    Mono<Boolean> existsByGroupIdAndUserId(Long groupId, Long userId);

    @Query("SELECT user_id FROM MEMBERS WHERE group_id = :groupId")
    Flux<Long> getMembers(@Param("groupId") Long groupId);
}