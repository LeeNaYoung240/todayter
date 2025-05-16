package com.todayter.domain.board.entity;

import com.todayter.domain.board.dto.BoardRequestDto;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "boards")
public class Board extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Builder
    public Board(String title, String contents, BoardType type)
    {
        this.title = title;
        this.contents = contents;
        this. type = type;
    }

    public Board(UserEntity user, BoardRequestDto requestDto, BoardType type) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContent();
        this.user = user;
        this.type = type;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContents(String contents) {
        this.contents = contents;
    }

    public void updateType(BoardType type) {
        this.type = type;
    }


    public enum BoardType {
        NORMAL, // 일반
        LOCAL, // 지역별
        SECTION, //분야별
        FEATURE, // 기획특집
        NOTICE, // 공지
        PICK // 픽
    }
}
