package com.todayter.domain.follow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FollowRequestDto {

    @NotBlank
    private Long following;
}
