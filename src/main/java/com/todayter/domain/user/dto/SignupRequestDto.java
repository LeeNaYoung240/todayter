package com.todayter.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class SignupRequestDto {

    @NotBlank(message = "사용자 ID는 비워둘 수 없습니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{4,10}$", message = "사용자 ID는 알파벳 소문자와 숫자로 이루어진 4자에서 10자 사이여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상, 15자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])\\S{10,}$", message = "password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")
    private String password;

    @NotBlank(message = "email은 비워둘 수 없습니다.")
    @Email(message = "이메일 형식으로 입력해 주세요.")
    private String email;

    @NotBlank(message = " nickname은 비워둘 수 없습니다.")
    private String nickname;

    private boolean isAdmin;
    private String adminToken;

    public String getAdminToken() {

        return adminToken != null ? adminToken : "";
    }
}


