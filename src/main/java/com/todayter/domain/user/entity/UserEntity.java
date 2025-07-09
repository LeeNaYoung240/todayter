package com.todayter.domain.user.entity;

import com.todayter.domain.follow.entity.Follow;
import com.todayter.domain.user.dto.SignupRequestDto;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class UserEntity extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();


    public UserEntity(SignupRequestDto dto, UserStatusEnum status, UserRoleEnum role) {
        this.email = dto.getEmail();
        this.username = dto.getUsername();
        this.nickname = dto.getNickname();
        this.name = dto.getName();
        this.status = status;
        this.role = role;
    }

    public UserEntity(String username, String email, String encodedPassword, String nickname, String name, UserStatusEnum userStatusEnum, UserRoleEnum userRoleEnum) {
        this.username = username;
        this.email = email;
        this.password = encodedPassword;
        this.nickname = nickname;
        this.name = name;
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

    public enum Gender {
        MALE, FEMALE
    }

    public boolean isAdmin() {
        return this.getRole() == UserRoleEnum.ADMIN;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }

    public void updateProfile(Gender gender, Integer age) {
        if (gender != null) {
            this.gender = gender;
        }
        if (age != null) {
            this.age = age;
        }
    }

    public int getFollowerCnt() {
        return followers != null ? followers.size() : 0;
    }


}