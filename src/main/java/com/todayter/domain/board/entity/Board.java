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

    @Column(nullable = true)
    private String region;

    @Column
    private String section;

    @Column(nullable = false)
    private String category;

    @Column(nullable = true)
    private Boolean pick;

    public void setCategory(String category) {
        this.category = category;
    }

    @Builder
    public Board(String title, String contents, BoardType type) {
        this.title = title;
        this.contents = contents;
        this.type = type;
    }

    public Board(UserEntity user, BoardRequestDto requestDto, BoardType type) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContent();
        this.user = user;
        this.type = type;
        this.category = requestDto.getCategory();

        setCategory(requestDto.getCategory());

        if (type == BoardType.LOCAL) {
            this.region = requestDto.getRegion();
        } else if (type == BoardType.SECTION) {
            this.section = requestDto.getSection();
        }
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

    public void setPick(boolean pick) {
        this.pick = pick;
    }

    public boolean isPick() {
        return pick;
    }

}
