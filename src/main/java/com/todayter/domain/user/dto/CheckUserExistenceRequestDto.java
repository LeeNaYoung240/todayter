package com.todayter.domain.user.dto;

import lombok.Getter;

@Getter
public class CheckUserExistenceRequestDto {
    private String email;
    private String name;
}
