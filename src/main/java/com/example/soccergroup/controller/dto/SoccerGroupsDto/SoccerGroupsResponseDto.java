package com.example.soccergroup.controller.dto.SoccerGroupsDto;

import com.example.soccergroup.repository.entity.SoccerGroup;
import com.example.soccergroup.service.dto.SoccerGroupServiceDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class SoccerGroupsResponseDto {

    private final List<SoccerGroupDto> soccer_group_list;

    public SoccerGroupsResponseDto(List<SoccerGroupServiceDto> soccerGroups)
    {
        soccer_group_list = new ArrayList<>();

        for(SoccerGroupServiceDto soccerGroup: soccerGroups)
        {
            soccer_group_list.add(new SoccerGroupDto(soccerGroup));
        }
    }
}
