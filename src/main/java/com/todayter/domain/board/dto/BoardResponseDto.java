package com.todayter.domain.board.dto;

import com.todayter.domain.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private String region;
    private String type;
    private String category;
    private String section;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public BoardResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.content = board.getContents();
        this.region = board.getRegion();
        this.category = board.getCategory();
        this.section = board.getSection();
        this.type = board.getType().name();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
    }

}
