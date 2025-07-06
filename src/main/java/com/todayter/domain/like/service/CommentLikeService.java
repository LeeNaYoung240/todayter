package com.todayter.domain.like.service;

import com.todayter.domain.comment.entity.Comment;
import com.todayter.domain.comment.repository.CommentRepository;
import com.todayter.domain.like.dto.CommentLikeResponseDto;
import com.todayter.domain.like.entity.CommentLike;
import com.todayter.domain.like.repository.CommentLikeRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentLikeResponseDto createCommentLike(Long commentId, UserEntity user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        CommentLike like = new CommentLike(user, comment);
        commentLikeRepository.save(like);
        comment.addLikeCnt();

        return new CommentLikeResponseDto(like, true, comment.getLikeCnt());
    }

    @Transactional
    public CommentLikeResponseDto deleteCommentLike(Long likeId, UserEntity user) {
        CommentLike like = commentLikeRepository.findById(likeId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_EXIST));

        if (!like.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CANNOT_CANCEL_OTHERS_LIKE);
        }

        Comment comment = like.getComment();
        commentLikeRepository.delete(like);
        comment.minusLikeCnt();

        return new CommentLikeResponseDto(null, false, comment.getLikeCnt());
    }

    public CommentLikeResponseDto getCommentLike(Long commentId, UserEntity user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        return commentLikeRepository.findByCommentAndUser(comment, user)
                .map(like -> new CommentLikeResponseDto(like, true, comment.getLikeCnt()))
                .orElseGet(() -> new CommentLikeResponseDto(null, false, comment.getLikeCnt()));
    }
}