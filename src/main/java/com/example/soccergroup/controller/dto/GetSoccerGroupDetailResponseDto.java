package com.example.soccergroup.controller.dto;

import com.example.soccergroup.repository.entity.SoccerGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class GetSoccerGroupDetailResponseDto {
    private final Long group_id;

    private final Long owner_id;

    private final List<Long> members;

    private final String group_name;

    private final Long group_member_count;

    private final String group_profile_pict_path;

    private final String group_description;

    private final String group_region;

    public GetSoccerGroupDetailResponseDto(SoccerGroup soccerGroup, List<Long> members)
    {
        this.group_id = soccerGroup.getGroupId();
        this.owner_id = soccerGroup.getOwnerId();
        this.members = members;
        this.group_name = soccerGroup.getGroupName();
        this.group_member_count = soccerGroup.getMemberCount();
        this.group_profile_pict_path = soccerGroup.getGroupProfileImgUrl();
        this.group_description = soccerGroup.getGroupDescription();
        this.group_region = soccerGroup.getGroupRegion();
    }
}
