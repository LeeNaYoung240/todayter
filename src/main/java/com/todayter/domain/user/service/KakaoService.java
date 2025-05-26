package com.todayter.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayter.domain.user.dto.KaKaoUserInfoDto;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    @Value("${SOCIAL_KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${SOCIAL_KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;

    public List<String> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        List<String> res = new ArrayList<>();

        String accessToken = getToken(code);  // 카카오로부터 Access Token 받아오기
        KaKaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);  // 사용자 정보 받아오기
        UserEntity kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);  // 회원가입 또는 로그인

        // JWT 토큰 생성
        String jwtAccessToken = jwtProvider.createAccessToken(kakaoUser.getUsername(), kakaoUser.getRole());
        String jwtRefreshToken = jwtProvider.createRefreshToken(kakaoUser.getUsername());

        kakaoUser.updateRefresh(jwtRefreshToken);
        userRepository.save(kakaoUser);  // DB에 저장

        // 응답 헤더에 JWT 토큰 추가
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAccessToken);
        response.setStatus(HttpServletResponse.SC_OK);

        res.add(accessToken);  // Access Token
        res.add(kakaoUser.getNickname());  // 사용자 닉네임

        return res;
    }

    // 카카오 서버에 인가코들르 보내고 Access Token 발급받는 메서드
    private String getToken(String code) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 body 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);

        // POST 요청 구성
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );
        // 응답 JSON 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        // access_token 추출해서 반환
        return jsonNode.get("access_token").asText();
    }

    // access token을 이용해 카카오 사용자 정보 요청
    private KaKaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // POST 요청 전송 (body는 비워도 됨)
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());  // 요청 본문 빈 객체

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        //log.info("카카오 로그인 응답: {}", jsonNode.toPrettyString());

        // 사용자 정보 추출
        String socialId = jsonNode.has("id") ? jsonNode.get("id").asText() : null;
        String nickname = null;
        JsonNode propertiesNode = jsonNode.get("properties");
        if (propertiesNode != null && propertiesNode.has("nickname")) {
            nickname = propertiesNode.get("nickname").asText();
        } else {
            log.warn("카카오 응답에 nickname이 없습니다.");
        }
        String email = null;
        JsonNode accountNode = jsonNode.get("kakao_account");
        if (accountNode != null && accountNode.has("email")) {
            email = accountNode.get("email").asText();
        } else {
            log.warn("카카오 응답에 email이 없습니다.");
        }
        if (socialId == null || nickname == null || email == null) {
            throw new RuntimeException("카카오 사용자 정보가 일부 누락됐습니다.");
        }

        return new KaKaoUserInfoDto(socialId, nickname, email);  // DTO에 담아 반환
    }

    // 카카오 사용자 등록 또는 로그인
    private UserEntity registerKakaoUserIfNeeded(KaKaoUserInfoDto kakaoUserInfo) {
        String kakaoIdAsUsername = kakaoUserInfo.getId();

        UserEntity kakaoUser = userRepository.findByUsername(kakaoIdAsUsername).orElse(null);

        if (kakaoUser == null) {
            String kakaoEmail = kakaoUserInfo.getEmail();
            UserEntity sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);

            if (sameEmailUser != null) {
                sameEmailUser.setUsername(kakaoIdAsUsername);
                kakaoUser = sameEmailUser;
            } else {
                String tempPassword = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(tempPassword);

                kakaoUser = new UserEntity(
                        kakaoIdAsUsername,
                        kakaoUserInfo.getEmail(),
                        encodedPassword,
                        kakaoUserInfo.getNickname(),
                        UserStatusEnum.ACTIVE,
                        UserRoleEnum.USER
                );
            }
        }

        return kakaoUser;
    }

}
