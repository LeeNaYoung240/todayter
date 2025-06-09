package com.todayter.domain.board.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardSummaryDto {

    private String title;
    private String subTitle;
    private String contents;
    private String category;
    private String nickname;
    private LocalDateTime createdAt;

    public BoardSummaryDto(String title, String subTitle, String contents, String category, String nickname, LocalDateTime createdAt) {
        this.title = title;
        this.subTitle = subTitle;
        this.contents = contents;
        this.category = category;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

}
