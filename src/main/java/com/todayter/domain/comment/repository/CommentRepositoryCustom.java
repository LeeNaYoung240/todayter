package com.todayter.domain.comment.repository;

import com.todayter.domain.comment.dto.CommentResponseDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepositoryCustom {

    List<CommentResponseDto> getPagedCommentsByBoardAndUser (Long scheduleId, Long userId);

}
