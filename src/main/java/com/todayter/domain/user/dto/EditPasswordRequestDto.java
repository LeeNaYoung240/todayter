package com.todayter.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EditPasswordRequestDto {

    private String password;

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상, 15자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])\\S{10,}$", message = "password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상, 15자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])\\S{10,}$", message = "password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로만 구성되어야 합니다.")
    private String confirmNewPassword;

}
