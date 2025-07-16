package com.todayter.domain.comment.repository;

import com.todayter.domain.comment.dto.CommentResponseDto;
import com.todayter.domain.comment.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepositoryCustom {

    List<CommentResponseDto> getPagedCommentsByBoardAndUser (Long scheduleId, Long userId);
    List<CommentResponseDto> getPagedCommentsByBoard(Long boardId);
    List<Comment> getCommentsWithUserProfileImageByBoard(Long boardId);

}
