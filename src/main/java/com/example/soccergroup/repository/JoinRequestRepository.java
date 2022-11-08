package com.example.soccergroup.repository;

import com.example.soccergroup.repository.entity.JoinRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JoinRequestRepository extends ReactiveCrudRepository<JoinRequest, Long> {

    Flux<JoinRequest> findByGroupId(Long groupId, Pageable pageable);

    Flux<JoinRequest> findByUserId(Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE joinRequest WHERE request_id = :requestId")
    Mono<Void> deleteById(@Param("requestId") Long requestId);

    Mono<Void> delete(JoinRequest joinRequest);

    Mono<Boolean> existsByUserIdAndGroupId(Long userId, Long groupId);
}
