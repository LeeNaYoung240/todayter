package com.todayter.global.security;

import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class UserDetailsImpl implements UserDetails, OAuth2User {

    private final UserEntity user;
    private Map<String, Object> attributes; // OAuth2 속성 저장

    // 기본 생성자 (일반 로그인용)
    public UserDetailsImpl(UserEntity user) {
        this.user = user;
    }

    // OAuth2용 생성자
    public UserDetailsImpl(UserEntity user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 사용자 정보 반환
    public UserEntity getUser() {
        return user;
    }

    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes != null ? attributes : Collections.emptyMap();
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    // UserDetails 인터페이스 구현
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String roleString = "ROLE_" + role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleString);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getStatus().equals(UserStatusEnum.BLOCK);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !user.getStatus().equals(UserStatusEnum.WITHDRAW);
    }
}
