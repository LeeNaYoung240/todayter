package com.todayter.domain.follow.dto;

import com.todayter.domain.user.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FollowRequestDto {

    @NotBlank
    private Long following;
    private UserEntity.Gender gender;
    private Integer age;

}
