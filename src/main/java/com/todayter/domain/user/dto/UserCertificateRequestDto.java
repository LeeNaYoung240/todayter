package com.todayter.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserCertificateRequestDto {

    @NotBlank(message = "email은 비워둘 수 없습니다.")
    @Email(message = "이메일 형식으로 입력해 주세요.")
    private String email;

}
