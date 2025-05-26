package com.todayter.domain.like.dto;

import com.todayter.domain.like.entity.Like;
import lombok.Getter;

@Getter
public class LikeResponseDto {

    private Long likeId;
    private Long boardId;
    private boolean isLiked;

    public LikeResponseDto(Like like, boolean isLiked) {
        this.likeId = (like != null) ? like.getId() : null;
        this.boardId = (like != null && like.getBoard() != null) ? like.getBoard().getId() : null;
        this.isLiked = isLiked;
    }
}
