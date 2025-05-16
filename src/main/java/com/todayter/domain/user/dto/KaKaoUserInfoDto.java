package com.todayter.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KaKaoUserInfoDto {

    private String id;
    private String nickname;
    private String email;

    public KaKaoUserInfoDto(String id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

}