package com.todayter.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class NicknameChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private String oldNickname;
    private String newNickname;

    private LocalDateTime changedAt;

    public NicknameChangeLog(UserEntity user, String oldNickname, String newNickname) {
        this.user = user;
        this.oldNickname = oldNickname;
        this.newNickname = newNickname;
        this.changedAt = LocalDateTime.now();
    }
}
