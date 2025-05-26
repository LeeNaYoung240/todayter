package com.todayter.domain.board.entity;

import com.todayter.domain.board.dto.BoardRequestDto;
import com.todayter.domain.board.dto.BoardUpdateRequestDto;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Long likeCnt = 0L;

    @Column(columnDefinition = "bigint default 0", nullable = false)
    private Long hits = 0L;

    public void setCategory(String category) {
        this.category = category;
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

    public void addLikeCnt() {
        this.likeCnt++;
    }

    public void minusLikeCount() {

        this.likeCnt--;
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

    public void update(BoardUpdateRequestDto dto) {
        this.title = dto.getTitle();
        this.contents = dto.getContent();
    }

    public Long getHits() {

        return hits;
    }

}
