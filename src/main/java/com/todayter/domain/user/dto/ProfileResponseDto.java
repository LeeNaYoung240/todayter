package com.todayter.domain.user.dto;

import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private Long id;
    private String username;
    private String name;
    private String nickname;
    private UserStatusEnum status;
    private UserRoleEnum role;
    private String email;
    private int followerCnt;

    public ProfileResponseDto(Long id, String username,String name, String nickname, UserStatusEnum status, UserRoleEnum role, String email, int followerCnt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.name = name;
        this.status = status;
        this.role = role;
        this.email = email;
        this.followerCnt = followerCnt;
    }
}
