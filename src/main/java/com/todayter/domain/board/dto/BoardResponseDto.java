package com.todayter.domain.board.dto;

import com.todayter.domain.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public BoardResponseDto(Board board)
    {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.content = board.getContents();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
    }
}
