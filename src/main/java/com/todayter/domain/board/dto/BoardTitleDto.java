package com.todayter.domain.board.dto;

import com.todayter.domain.board.entity.Board;
import lombok.Getter;

@Getter
public class BoardTitleDto {

    private Long boardId;
    private String title;
    private String content;

    public BoardTitleDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.content = board.getContents();
    }
}
