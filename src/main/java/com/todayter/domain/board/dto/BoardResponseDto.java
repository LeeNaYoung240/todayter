package com.todayter.domain.board.dto;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.file.entity.File;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> imageUrl;
    private boolean approved;
    private Boolean pick;
    private Long userId;
    private Integer followerCnt;
    private String profileImageUrl;

    public BoardResponseDto(Board board, int followerCnt) {
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
        this.imageUrl = board.getFiles().stream()
                .map(File::getFileUrl)
                .toList();
        this.approved = board.isApproved();
        this.pick = board.getPick();
        this.userId = board.getUser().getId();
        this.followerCnt = followerCnt;
        this.profileImageUrl = null;

        if (board.getUser().getProfileImage() != null) {
            this.profileImageUrl = board.getUser().getProfileImage().getFileUrl();
        }
    }

}
