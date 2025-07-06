package com.todayter.domain.like.repository;

import com.todayter.domain.comment.entity.Comment;
import com.todayter.domain.like.entity.CommentLike;
import com.todayter.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByUserAndComment(UserEntity user, Comment comment);

    Optional<CommentLike> findByCommentAndUser(Comment comment, UserEntity user);

}