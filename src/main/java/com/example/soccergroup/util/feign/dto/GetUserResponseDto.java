package com.example.soccergroup.util.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class GetUserResponseDto {

    private final String nickname;

    private final String region;

    private final String profile_picture_path;

    private final Long point;

}