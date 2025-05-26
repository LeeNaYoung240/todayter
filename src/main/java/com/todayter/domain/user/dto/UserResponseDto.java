package com.todayter.domain.user.dto;

import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private UserRoleEnum role;
    private UserStatusEnum status;

    public UserResponseDto(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.status = user.getStatus();
    }
}