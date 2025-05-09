package com.todayter.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayter.domain.dto.LoginRequestDto;
import com.todayter.domain.dto.LoginResponseDto;
import com.todayter.domain.entity.UserEntity;
import com.todayter.domain.entity.UserRoleEnum;
import com.todayter.domain.entity.UserStatusEnum;
import com.todayter.domain.repository.UserRepository;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import com.todayter.global.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/api/users/login"); // 로그인 요청 URL
    }

    // 로그인 요청
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new CustomException(ErrorCode.WRONG_HTTP_REQUEST);
        }

        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            if (requestDto.getUsername() == null || requestDto.getPassword() == null) {
                throw new CustomException(ErrorCode.LOGIN_FAIL_NULL);
            }

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword(), null)
            );
        } catch (IOException e) {
            log.error("Login request parsing error", e);
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }
    }

    // 로그인 성공 처리
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {

        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUsername();
        UserRoleEnum userRole = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();
        UserStatusEnum userStatus = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getStatus();

        // DB에서 사용자 조회
        Optional<UserEntity> userOptional  = userRepository.findByUsername(username);

        // 사용자가 탈퇴했거나 차단된 경우 로그인 실패
        if (userOptional.isEmpty() || userOptional.get().getStatus().equals(UserStatusEnum.WITHDRAW) || userOptional.get().getStatus().equals(UserStatusEnum.BLOCK)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("유효하지 않은 사용자 정보입니다.");
            return;
        }

        // JWT 토큰 생성
        UserEntity user = userOptional.get();

        String accessToken = jwtProvider.createAccessToken(username, userRole);
        String refreshToken = jwtProvider.createRefreshToken(username);

        // RefreshToken을 DB에 저장
        user.updateRefresh(refreshToken);
        userRepository.save(user);

        // Refresh Token을 HTTPOnly 쿠키로 저장
        jwtProvider.addRefreshTokenToCookie(response, refreshToken);

        CommonResponseDto<LoginResponseDto> commonResponse = new CommonResponseDto<>(200, "로그인 성공 🎉", new LoginResponseDto(user));

        // 응답헤더에 JWT 토큰 추가
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    }

    // 로그인 실패 처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        CommonResponseDto<?> commonResponse = new CommonResponseDto<>(400, "아이디 또는 비밀번호가 틀렸습니다 ⚠", null);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    }
}
