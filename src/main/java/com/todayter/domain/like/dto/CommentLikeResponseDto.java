package com.todayter.domain.like.dto;

import com.todayter.domain.like.entity.CommentLike;
import lombok.Getter;

@Getter
public class CommentLikeResponseDto {

    private Long likeId;
    private Long commentId;
    private boolean isLiked;
    private Long likeCnt;

    public CommentLikeResponseDto(CommentLike like, boolean isLiked, Long likeCnt) {
        this.likeId = (like != null) ? like.getId() : null;
        this.commentId = (like != null && like.getComment() != null) ? like.getComment().getId() : null;
        this.isLiked = isLiked;
        this.likeCnt = likeCnt;
    }

}
