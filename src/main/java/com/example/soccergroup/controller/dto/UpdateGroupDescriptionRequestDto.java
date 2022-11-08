package com.example.soccergroup.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class UpdateGroupDescriptionRequestDto {

    @NotBlank(message = "group description should not be blank!")
    private final String group_description;
}
