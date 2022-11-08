package com.example.soccergroup.service.dto;

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
public class SoccerGroupServiceDto
{
    private final Long groupId;

    private final String groupName;

    private final Long groupMemberCount;

    private final String groupProfilePictPath;

    private final String groupDescription;

    private final String groupRegion;

    private final Long ownerId;

    private final List<Long> members;

    public SoccerGroupServiceDto(SoccerGroup soccerGroup, List<Long> members)
    {
        this.groupId = soccerGroup.getGroupId();
        this.groupName = soccerGroup.getGroupName();
        this.groupMemberCount = soccerGroup.getMemberCount();
        this.groupProfilePictPath = soccerGroup.getGroupProfileImgUrl();
        this.groupDescription = soccerGroup.getGroupDescription();
        this.groupRegion = soccerGroup.getGroupRegion();
        this.ownerId = soccerGroup.getOwnerId();
        this.members = members;
    }


}
