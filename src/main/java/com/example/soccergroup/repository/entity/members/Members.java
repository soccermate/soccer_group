package com.example.soccergroup.repository.entity.members;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name="members")
public class Members {

    @Id
    @Column("group_id")
    private Long groupId;

    //this is also a primary key but r2dbc does not support composite primary key yet!
    @Column("user_id")
    private Long userId;



}
