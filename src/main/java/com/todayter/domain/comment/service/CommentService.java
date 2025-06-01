package com.todayter.domain.comment.service;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.board.service.BoardService;
import com.todayter.domain.comment.dto.CommentRequestDto;
import com.todayter.domain.comment.dto.CommentResponseDto;
import com.todayter.domain.comment.entity.Comment;
import com.todayter.domain.comment.repository.CommentRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardService boardService;

    @Transactional
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Long boardId, UserEntity user) {
        Board board = boardService.findById(boardId);
        Comment comment = new Comment(commentRequestDto, board, user);
        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getBoardComments(Long boardId, UserEntity user) {

        return commentRepository.getPagedCommentsByBoardAndUser(boardId, user.getId());
    }

    @Transactional
    public CommentResponseDto updateComment(CommentRequestDto commentRequestDto, Long commentId, UserEntity user) {
        Comment comment = findById(commentId);
        validateUserMatch(comment, user);
        comment.update(commentRequestDto);
        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, UserEntity user) {
        Comment comment = findById(commentId);
        validateUserMatch(comment, user);
        commentRepository.delete(comment);
    }

    public long getTotalCommentCnt() {
        return commentRepository.count();
    }

    private Comment findById(Long commentId) {

        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    private void validateUserMatch(Comment comment, UserEntity user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.USER_NOT_MATCH_WITH_COMMENT);
        }
    }

}
