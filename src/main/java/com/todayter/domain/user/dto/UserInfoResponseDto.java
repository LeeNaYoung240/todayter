package com.todayter.domain.user.dto;

import lombok.Getter;

@Getter
public class UserInfoResponseDto {
    private final String name;
    private final String email;
    private final String role;

    public UserInfoResponseDto(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
}