package com.todayter.domain.board.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BoardUpdateRequestDto {

    @Size(max = 100, message = "제목은 최대 100자까지만 입력이 가능합니다.")
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    @Size(max = 10000, message = "내용은 최대 10,000자까지 입력 가능합니다.")
    private String content;

}
