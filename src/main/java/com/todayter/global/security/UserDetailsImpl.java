package com.todayter.global.security;

import com.todayter.domain.entity.UserEntity;
import com.todayter.domain.entity.UserRoleEnum;
import com.todayter.domain.entity.UserStatusEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final UserEntity user;

    public UserDetailsImpl(UserEntity user) {
        this.user = user;
    }

    // 사용자 정보 반환
    public UserEntity getUser() {
        return user;
    }

    // 사용자 이름(ID) 반환
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 사용자 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 계정 만료 여부 확인
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 기능 없음
    }

    // 사용자 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        UserRoleEnum role = user.getRole();
        String roleString = "ROLE_" + role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleString);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    // 계정 차단 여부
    @Override
    public boolean isAccountNonLocked() {
        return !user.getStatus().equals(UserStatusEnum.BLOCK); // 차단된 계정이면 false
    }

    //비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 기능 없음
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return !user.getStatus().equals(UserStatusEnum.WITHDRAW); // 탈퇴한 계정이면 false
    }
}
