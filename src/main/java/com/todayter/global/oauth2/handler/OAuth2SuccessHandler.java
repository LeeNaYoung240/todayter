package com.todayter.global.oauth2.handler;

import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.jwt.JwtProvider;
import com.todayter.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserEntity user = userDetails.getUser();

        // JWT 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getUsername());

        // 리프레시 토큰 저장
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    u.updateRefresh(refreshToken);
                    userRepository.save(u);
                });

        // 응답 설정
        response.addHeader("Authorization", "Bearer " + accessToken);
        jwtProvider.addRefreshTokenToCookie(response, refreshToken);
        response.setStatus(HttpServletResponse.SC_OK);

        response.sendRedirect("http://localhost:3000/main");

    }
}