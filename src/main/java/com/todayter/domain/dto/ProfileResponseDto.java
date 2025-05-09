package com.todayter.domain.dto;

import com.todayter.domain.entity.UserRoleEnum;
import com.todayter.domain.entity.UserStatusEnum;
import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private Long id;
    private String username;
    private String nickname;
    private UserStatusEnum status;
    private UserRoleEnum role;

    public ProfileResponseDto(Long id, String username, String nickname, UserStatusEnum status, UserRoleEnum role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.status = status;
        this.role = role;
    }
}
