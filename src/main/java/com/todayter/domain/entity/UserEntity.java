package com.todayter.domain.entity;

import com.todayter.domain.dto.SignupRequestDto;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class UserEntity extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String loginId;

    @Column
    private String refreshToken;

    @Column
    private String socialId;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status;

    public UserEntity(SignupRequestDto dto, UserStatusEnum status, UserRoleEnum role) {
        this.username = dto.getUsername();
        this.nickname = dto.getNickname();
        this.status = status;
        this.role = role;
    }

    public UserEntity(String email, String encodedPassword, String nickname, UserStatusEnum userStatusEnum, UserRoleEnum userRoleEnum, String socialId, String loginId) {
        this.username = email;
        this.password = encodedPassword;
        this.nickname = nickname;
        this.email = email;
        this.status = userStatusEnum;
        this.role = userRoleEnum;
        this.socialId = socialId;
        this.loginId = loginId;
    }

    public void encryptionPassword(String password) {
        this.password = password;
    }

    public void updateRefresh(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateStatus(UserStatusEnum status) {
        this.status = status;
    }

    public UserEntity socialIdUpdate(String socialId) {
        this.socialId = socialId;
        return this;
    }

    public String getLoginId() {
        return loginId;
    }

}