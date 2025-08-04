package com.todayter.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    @NotBlank(message = "댓글의 내용을 입력해주세요.")
    @Size(max = 300, message = "댓글은 최대 300자까지 입력할 수 있습니다.")
    private String content;

    private Long parentId;
}
