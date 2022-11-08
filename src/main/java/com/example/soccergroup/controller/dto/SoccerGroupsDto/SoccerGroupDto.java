package com.example.soccergroup.controller.dto.SoccerGroupsDto;

import com.example.soccergroup.repository.entity.SoccerGroup;
import com.example.soccergroup.service.dto.SoccerGroupServiceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SoccerGroupDto {
    private final Long group_id;

    private final String group_name;

    private final Long group_member_count;

    private final String group_profile_pict_path;

    private final String group_description;

    private final String group_region;

    private final Long owner_id;

    private final List<Long> members;

    public SoccerGroupDto(SoccerGroupServiceDto soccerGroup)
    {
        this.group_id = soccerGroup.getGroupId();
        this.group_name = soccerGroup.getGroupName();
        this.group_member_count = soccerGroup.getGroupMemberCount();
        this.group_profile_pict_path = soccerGroup.getGroupProfilePictPath();
        this.group_description = soccerGroup.getGroupDescription();
        this.group_region = soccerGroup.getGroupRegion();
        this.owner_id = soccerGroup.getOwnerId();
        this.members = soccerGroup.getMembers();
    }
}
