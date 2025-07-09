package com.todayter.domain.user.dto;

import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class UserResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private UserRoleEnum role;
    private UserStatusEnum status;
    private UserEntity.Gender gender;
    private Integer age;
    private List<Long> followingIds;

    public UserResponseDto(UserEntity user, List<Long> followingIds) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.followingIds = followingIds;
    }
}