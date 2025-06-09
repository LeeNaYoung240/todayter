package com.todayter.domain.board.dto;

import com.todayter.domain.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private Long boardId;
    private String username;
    private String name;
    private String email;
    private String title;
    private String subTitle;
    private String content;
    private String region;
    private String type;
    private String category;
    private String section;
    private Long likeCnt;
    private Long hits;
    private Long hourHits;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String imageUrl;
    private boolean approved;

    public BoardResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.subTitle = board.getSubTitle();
        this.content = board.getContents();
        this.name = board.getUser().getName();
        this.username = board.getUser().getUsername();
        this.email = board.getUser().getEmail();
        this.region = board.getRegion();
        this.category = board.getCategory();
        this.section = board.getSection();
        this.type = board.getType().name();
        this.likeCnt = board.getLikeCnt();
        this.hourHits = board.getHourHits();
        this.hits = board.getHits();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.imageUrl = board.getImageUrl();
        this.approved = board.isApproved();
    }

}
