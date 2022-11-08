package com.example.soccergroup.controller.dto.requestDto;

import com.example.soccergroup.repository.entity.JoinRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RequestDto {

    private final Long request_id;
    private final Long user_id;
    private final Long group_id;
    private final LocalDateTime request_time;

    public RequestDto(JoinRequest joinRequest)
    {
        this.request_id = joinRequest.getRequestId();
        this.user_id = joinRequest.getUserId();
        this.group_id = joinRequest.getGroupId();
        this.request_time = joinRequest.getRequestTime();
    }


}
