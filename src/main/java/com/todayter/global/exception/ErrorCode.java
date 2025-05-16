package com.todayter.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

     NOT_BLOCK(500,"차단 권한이 없습니다."),
     ALREADY_BLOCK(500,"이미 차단된 사용자입니다."),
     DUPLICATE_NICKNAME(500, "이미 사용중인 닉네임입니다."),
     DUPLICATE_USERNAME(500, "이미 사용중인 ID입니다."),
     INVALID_JWT_TOKEN(401, "유효하지 않은 JWT 토큰입니다."),
     USER_NOT_ACTIVE_BLOCK(401, "계정이 차단되었습니다."),
     USER_NOT_ACTIVE_WITHDRAW(401, "계정이 탈퇴되었습니다."),
     MISSING_AUTHORIZATION_HEADER(401, "Authorization header 값이 틀렸습니다."),
     WRONG_HTTP_REQUEST(500, "잘못된 http 요청입니다." ),
     LOGIN_FAIL(404, "로그인에 실패했습니다."),
     LOGIN_FAIL_NULL(404, "아이디나 비밀번호 값이 null입니다."),
     USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
     REFRESH_TOKEN_NOT_VALIDATE(500, "리프레시 토큰이 만료 되었거나 잘못되었습니다."),
     INCORRECT_PASSWORD(500, "현재 비밀번호가 일치하지 않습니다."),
     CONFIRM_NEW_PASSWORD_NOT_MATCH(500, "새로운 비밀번호가 서로 일치하지 않습니다."),
     NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD(500,"새 비밀번호는 현재 비밀번호와 같을 수 없습니다."),
     ;

    private int status;
    private String message;

}
