package com.example.soccergroup.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name="soccer_group")
public class SoccerGroup {

    @Id
    @Column("group_id")
    private Long groupId;

    @Column("owner_id")
    private Long ownerId;

    @Column("group_profile_img_url")
    private String groupProfileImgUrl;

    @Column("group_name")
    private String groupName;

    @Column("group_point")
    private Long groupPoint;

    @Column("group_description")
    private String groupDescription;

    @Column("group_region")
    private String groupRegion;

    @Column("member_count")
    private Long memberCount;
}
