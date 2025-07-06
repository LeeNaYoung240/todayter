package com.todayter.domain.comment.dto;

import com.todayter.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponseDto {

    private Long commentId;
    private Long boardId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long parentId;
    private List<CommentResponseDto> replies;

    private Long likeCount;
    private Boolean isLiked;
    private Long likeId;

    public CommentResponseDto(Comment comment, Long likeId) {
        this.commentId = comment.getId();
        this.boardId = comment.getBoard().getId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.replies = comment.getReplies() == null ? null :
                comment.getReplies().stream()
                        .map(reply -> new CommentResponseDto(reply, null))
                        .toList();

        this.likeCount = comment.getLikeCnt();

        if (likeId != null) {
            this.isLiked = true;
            this.likeId = likeId;
        } else {
            this.isLiked = false;
            this.likeId = null;
        }
    }

    public CommentResponseDto(Comment comment) {
        this(comment, null);
    }

    public CommentResponseDto(CommentResponseDto original, Long likeId) {
        this.commentId = original.getCommentId();
        this.boardId = original.getBoardId();
        this.nickname = original.getNickname();
        this.content = original.getContent();
        this.createdAt = original.getCreatedAt();
        this.modifiedAt = original.getModifiedAt();
        this.parentId = original.getParentId();
        this.replies = original.getReplies();
        this.likeCount = original.getLikeCount();
        this.isLiked = likeId != null;
        this.likeId = likeId;
    }

}
