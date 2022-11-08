package com.example.soccergroup.repository;

import com.example.soccergroup.repository.entity.SoccerGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface SoccerGroupRepository extends ReactiveCrudRepository<SoccerGroup, Long>
{

    @Query("SELECT *, (select COUNT(*) from members m where m.group_id = sg.group_id) as member_count FROM soccer_group sg WHERE group_name LIKE CONCAT('%', :keyword, '%') ORDER BY group_point DESC OFFSET :offset LIMIT :limit")
    //page는 0 부터 시작
    Flux<SoccerGroup> findByGroupNameContains(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);


    @Query("SELECT *, (select COUNT(*) from members m where m.group_id = sg.group_id) as member_count FROM soccer_group sg WHERE group_region LIKE CONCAT('%', :region, '%') ORDER BY group_point DESC OFFSET :offset LIMIT :limit;")
    Flux<SoccerGroup> recommendSoccerGroup(@Param("region") String region, @Param("offset") int offset, @Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("UPDATE SOCCER_GROUP SET group_profile_img_url = :groupProfileImgUrl WHERE group_id = :groupId")
    Mono<Void> updateProfileImgPath(@Param("groupProfileImgUrl") String groupProfileImgUrl, @Param("groupId") Long groupId);

    @Modifying
    @Transactional
    @Query("UPDATE SOCCER_GROUP SET group_point = :groupPoint + group_point WHERE group_id = :groupId")
    Mono<Integer> incrementGroupPoint(@Param("groupPoint") int groupPoint, @Param("groupId") Long groupId);

    @Modifying
    @Transactional
    @Query("UPDATE SOCCER_GROUP SET group_description = :groupDescription WHERE group_id = :groupId")
    Mono<Void> updateGroupDescription(@Param("groupDescription") String groupDescription, @Param("groupId") Long groupId);

    Mono<Boolean> existsByOwnerIdAndGroupId(Long ownerId, Long groupId);


    @Query("select group_id ,owner_id, group_profile_img_url, group_name, group_point, group_region, group_description," +
            " array(select user_id from members m where m.group_id = sg.group_id) as members_count from soccer_group sg natural join members m where user_id = :userId OFFSET :offset LIMIT :limit")
    Flux<SoccerGroup> findByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

}
