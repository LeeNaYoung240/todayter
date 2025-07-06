package com.todayter.domain.like.entity;

import com.todayter.domain.comment.entity.Comment;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment_likes")
public class CommentLike extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    public CommentLike(UserEntity user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}
