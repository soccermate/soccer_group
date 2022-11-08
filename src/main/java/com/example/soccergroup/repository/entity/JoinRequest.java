package com.example.soccergroup.repository.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name="join_request")
public class JoinRequest {

    @Id
    @Column("request_id")
    private Long requestId;

    @Column("user_id")
    private Long userId;

    @Column("group_id")
    private Long groupId;

    @Column("request_time")
    private LocalDateTime requestTime;



}
