package com.todayter.domain.comment.service;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.board.service.BoardService;
import com.todayter.domain.comment.dto.CommentRequestDto;
import com.todayter.domain.comment.dto.CommentResponseDto;
import com.todayter.domain.comment.entity.Comment;
import com.todayter.domain.comment.repository.CommentRepository;
import com.todayter.domain.like.entity.CommentLike;
import com.todayter.domain.like.repository.CommentLikeRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardService boardService;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Long boardId, UserEntity user) {
        Board board = boardService.findById(boardId);

        Comment parent = null;
        if (commentRequestDto.getParentId() != null) {
            parent = findById(commentRequestDto.getParentId());

            if (!parent.getBoard().getId().equals(boardId)) {
                throw new CustomException(ErrorCode.INVALID_PARENT_COMMENT);
            }
        }

        Comment comment = new Comment(commentRequestDto, board, user, parent);
        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getBoardComments(Long boardId, Long userId) {
        List<Comment> comments = commentRepository.getCommentsWithUserProfileImageByBoard(boardId);

        List<Long> commentIds = comments.stream().map(Comment::getId).toList();

        Map<Long, Long> likedCommentMap = userId != null ?
                commentLikeRepository.findByUserIdAndCommentIds(userId, commentIds)
                        .stream()
                        .collect(Collectors.toMap(cl -> cl.getComment().getId(), CommentLike::getId))
                : Map.of();

        return comments.stream()
                .map(comment -> {
                    Long likeId = likedCommentMap.get(comment.getId());
                    return new CommentResponseDto(comment, likeId);
                })
                .toList();
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

    @Transactional
    public void deleteCommentByAdmin(Long commentId, UserEntity user) {
        if(!user.isAdmin())  {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Comment comment = findById(commentId);
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
