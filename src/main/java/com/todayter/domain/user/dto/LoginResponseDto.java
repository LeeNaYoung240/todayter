package com.todayter.domain.user.dto;

import com.todayter.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private Long userId;
    private String username;
    private String name;
    private String nickname;

    public LoginResponseDto(UserEntity user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }

}