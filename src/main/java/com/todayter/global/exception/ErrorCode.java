package com.todayter.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // user 관련 오류 처리
    NOT_BLOCK(500, "차단 권한이 없습니다."),
    ALREADY_BLOCK(500, "이미 차단된 사용자입니다."),
    DUPLICATE_NICKNAME(500, "이미 사용중인 닉네임입니다."),
    DUPLICATE_USERNAME(500, "이미 사용중인 ID입니다."),
    INVALID_JWT_TOKEN(401, "유효하지 않은 JWT 토큰입니다."),
    USER_NOT_ACTIVE_BLOCK(401, "계정이 차단되었습니다."),
    USER_NOT_ACTIVE_WITHDRAW(401, "계정이 탈퇴되었습니다."),
    MISSING_AUTHORIZATION_HEADER(401, "Authorization header 값이 틀렸습니다."),
    WRONG_HTTP_REQUEST(500, "잘못된 http 요청입니다."),
    LOGIN_FAIL(404, "로그인에 실패했습니다."),
    LOGIN_FAIL_NULL(404, "아이디나 비밀번호 값이 null입니다."),
    USER_NOT_FOUND(404, "존재하지 않는 사용자입니다."),
    REFRESH_TOKEN_NOT_VALIDATE(500, "리프레시 토큰이 만료 되었거나 잘못되었습니다."),
    INCORRECT_PASSWORD(500, "현재 비밀번호가 일치하지 않습니다."),
    CONFIRM_NEW_PASSWORD_NOT_MATCH(500, "새로운 비밀번호가 서로 일치하지 않습니다."),
    NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD(500, "새 비밀번호는 현재 비밀번호와 같을 수 없습니다."),
    DUPLICATE_EMAIL(404, "이미 존재하는 이메일입니다."),
    NOT_ACCESS(404, "접근 권한이 없습니다."),
    ALREADY_ADMIN(404, "이미 관리자 권한을 가지고 있습니다."),

    // board 관련 오류 처리
    BOARD_NOT_FOUND(404, "존재하지 않는 게시글입니다."),
    SORT_NOT_FOUND(404, "찾을 수 없는 정렬입니다."),
    SECTION_TYPE_NOT_FOUND(404, "섹션 타입을 찾을 수 없습니다."),
    UNAUTHORIZED(404, "권환이 없습니다."),
    USER_NOT_MATCH_WITH_BOARD(404, "해당 게시글의 작성자가 아니면 수정할 수 없습니다."),
    ADMIN_ACCESS(404, "관리자만 승인할 수 있습니다."),

    // Redis 관련 오류 처리
    REDIS_NOT_CONNECT(500, "레디스 서버에 연결할 수 없습니다."),
    VERIFY_NOT_ALLOWED(400, "인증 요청이 잘못 되었습니다."),

    // Comment 관련 오류 처리
    COMMENT_NOT_FOUND(404, "존재하지 않는 오류입니다."),
    USER_NOT_MATCH_WITH_COMMENT(404, "해당 댓글의 작성자가 아니면 수정할 수 없습니다."),
    BAD_WORD_DETECTED(403, "욕설/비방 단어가 포함되어 있습니다."),

    // Like 관련 오류 처리
    ALREADY_LIKED(409, "이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_EXIST(404, "좋아요가 존재하지 않습니다."),
    CANNOT_CANCEL_OTHERS_LIKE(403, "다른 사람의 좋아요는 취소할 수 없습니다."),
    LIKE_NOT_FOUND(404, "해당 게시글에 좋아요를 누른 기록이 없습니다."),
    BOARD_ALREADY_DISAPPROVED(404, "이미 승인 취소된 게시글입니다."),

    // Follow 관련 오류 처리
    INVALID_FOLLOW_REQUEST(404, "자기 자신은 팔로우할 수 없습니다."),
    ALREADY_FOLLOWING(404, "이미 팔로우중입니다."),
    FOLLOW_NOT_FOUND(404, "팔로우하고 있지 않습니다."),
    INVALID_PARENT_COMMENT(404, "부모 댓글이 해당 게시글에 속하지 않습니다."),

    //s3
    PUT_OBJECT_EXCEPTION(500, "s3 업로드에 문제가 발생했습니다."),
    FILE_NAME_INVALID(400, "잘못된 파일명입니다."),
    EXTENSION_IS_EMPTY(404, "파일을 찾을 수 없습니다."),
    EXTENSION_INVALID(404, "잘못된 확장자명입니다."),
    IO_EXCEPTION_ON_IMAGE_DELETE(404, "업로드에 문제가 발생했습니다."),
    NULL_MULTIPART_FILES_EXCEPTION(404, "파일 업로드 요청에서 파일이 포함되지 않았습니다. 파일을 선택하고 다시 시도하십시오."),

    TOO_MANY_NICKNAME_CHANGES(404, "닉네임은 30일 내에 최대 3회까지 변경할 수 있습니다."),
    ;

    private int status;
    private String message;

}
