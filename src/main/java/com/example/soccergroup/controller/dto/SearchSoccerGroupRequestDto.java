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
public class SearchSoccerGroupRequestDto {

    @NotBlank(message = "keyword should not be blank!")
    private final String keyword;
}
