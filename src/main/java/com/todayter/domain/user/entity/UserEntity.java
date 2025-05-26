package com.todayter.domain.user.entity;

import com.todayter.domain.user.dto.SignupRequestDto;
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

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status;

    public UserEntity(SignupRequestDto dto, UserStatusEnum status, UserRoleEnum role) {
        this.email = dto.getEmail();
        this.username = dto.getUsername();
        this.nickname = dto.getNickname();
        this.status = status;
        this.role = role;
    }

    public UserEntity(String username, String email, String encodedPassword, String nickname, UserStatusEnum userStatusEnum, UserRoleEnum userRoleEnum) {
        this.username = username;
        this.password = encodedPassword;
        this.nickname = nickname;
        this.email = email;
        this.status = userStatusEnum;
        this.role = userRoleEnum;
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

    public boolean isBlock() {
        return this.status == UserStatusEnum.BLOCK;
    }

    public boolean hasRole(String role) {
        return this.role != null && this.role.getAuthority().equals(role);
    }

    public void setUsername(String username) {
        this.username = username;
    }

}