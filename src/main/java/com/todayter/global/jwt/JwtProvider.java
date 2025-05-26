package com.todayter.global.jwt;


import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j(topic = "JWT Token")
@Component
public class JwtProvider {

    private final String secretKey;

    // jwt 액세스 토큰 만료 시간 설정
    @Value("${jwt.access.token.expiration}")
    long tokenExpiration;

    // jwt 리프레시 토큰 만료 시간 설정
    @Value("${jwt.refresh.token.expiration}")
    long refreshTokenExpiration;

    private static final String BEARER_PREFIX = "Bearer ";

    // application.yml에서 설정된 secretKy를 Base64로 인코딩하여 저장
    public JwtProvider(@Value("${jwt.secret.key}") String secretKey) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 액세스 토큰 생성
    public String createAccessToken(String username, UserRoleEnum role) {

        return generateToken(username, role, tokenExpiration);
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username) {

        return generateRefreshToken(username, refreshTokenExpiration);
    }

    // JWT 토큰 생성 (액세스 토큰)
    private String generateToken(String username, UserRoleEnum role, long expiration) {

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime expirationTime = now.plusSeconds(expiration / 1000);
        System.out.println("UTC Time: " + now);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.getAuthority())
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(expirationTime.toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }

    // JWT 토큰 생성 (리프레시 토큰)
    private String generateRefreshToken(String username, long expiration) {

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime expirationTime = now.plusSeconds(expiration / 1000);

        return Jwts.builder()
                .setSubject(username)
                .claim("type", "refresh")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(expirationTime.toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 클레임 추출
    public Claims extractClaims(String token) {
        try {

            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {

            log.error("JWT parsing error: ", e);
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    // 토큰이 유효한지 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱하여 유효성 검증
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            // 토큰 유효하지 않으면 false 반환
            log.error("Invalid JWT token", e);
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {

        return validateToken(token);
    }

    // HTTP 요청에서 액세스 토큰을 추출하는 메서드
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 Bearer 접두어가 아닌 경우 예외 처리
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorizationHeader.substring(7);  // "Bearer " 이후의 토큰 반환
    }

    // JWT 토큰에서 클레임 추출 메서드
    public Claims getClaimsFromToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Refresh Token을 HTTPOnly 쿠키에 추가하는 메서드
    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);  // JavaScript에서 접근할 수 없도록
        refreshTokenCookie.setSecure(true);    // HTTPS 연결에서만 전송되도록
        refreshTokenCookie.setMaxAge((int) (refreshTokenExpiration / 1000));  // 초 단위로 설정
        refreshTokenCookie.setPath("/");  // 모든 경로에서 쿠키 접근 가능

        response.addCookie(refreshTokenCookie);  // 응답에 쿠키 추가
    }

}
