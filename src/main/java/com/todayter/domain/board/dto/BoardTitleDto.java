package com.todayter.domain.board.dto;

import lombok.Getter;

@Getter
public class BoardTitleDto {

    private String title;

    public BoardTitleDto(String title)
    {
        this.title = title;
    }

}
