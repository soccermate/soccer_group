package com.example.soccergroup.controller.dto.requestDto;

import com.example.soccergroup.repository.entity.JoinRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class RequestsResponseDto {

    private final List<RequestDto>  request_list;

    public RequestsResponseDto(List<JoinRequest> joinRequests)
    {
        request_list = new ArrayList<>();
        for(JoinRequest joinRequest: joinRequests)
        {
            request_list.add(new RequestDto(joinRequest));
        }
    }
}
