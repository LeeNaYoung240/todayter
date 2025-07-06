package com.todayter.domain.like.repository;

import com.todayter.domain.comment.entity.Comment;
import com.todayter.domain.like.entity.CommentLike;
import com.todayter.domain.user.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByUserAndComment(UserEntity user, Comment comment);

    Optional<CommentLike> findByCommentAndUser(Comment comment, UserEntity user);

    @Query("SELECT cl FROM CommentLike cl WHERE cl.user.id = :userId AND cl.comment.id IN :commentIds")
    List<CommentLike> findByUserIdAndCommentIds(@Param("userId") Long userId,
                                                @Param("commentIds") List<Long> commentIds);
}