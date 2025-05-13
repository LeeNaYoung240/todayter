package com.todayter.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {

    private String id;
    private String name;
    private String email;

    public GoogleUserInfoDto(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}