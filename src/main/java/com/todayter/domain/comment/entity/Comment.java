package com.todayter.domain.comment.entity;

import com.todayter.domain.board.entity.Board;
import com.todayter.domain.comment.dto.CommentRequestDto;
import com.todayter.domain.like.entity.CommentLike;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private Long likeCnt = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();


    public void addLikeCnt() {
        this.likeCnt++;
    }

    public void minusLikeCnt() {
        this.likeCnt--;
    }

    public Comment(CommentRequestDto commentRequestDto, Board board, UserEntity user, Comment parent) {
        this.content = commentRequestDto.getContent();
        this.board = board;
        this.user = user;
        this.parent = parent;
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
    }

}
