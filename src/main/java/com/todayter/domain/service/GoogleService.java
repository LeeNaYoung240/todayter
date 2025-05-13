package com.todayter.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayter.domain.dto.GoogleUserInfoDto;
import com.todayter.domain.entity.UserEntity;
import com.todayter.domain.entity.UserRoleEnum;
import com.todayter.domain.entity.UserStatusEnum;
import com.todayter.domain.repository.UserRepository;
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
public class GoogleService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    @Value("${SOCIAL_GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${SOCIAL_GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${SOCIAL_GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    public List<String> googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        List<String> res = new ArrayList<>();

        String accessToken = getToken(code);  // 구글로부터 Access Token 받아오기
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);  // 사용자 정보 받아오기
        UserEntity googleUser = registerGoogleUserIfNeeded(googleUserInfo);  // 회원가입 또는 로그인

        // JWT 토큰 생성
        String jwtAccessToken = jwtProvider.createAccessToken(googleUser.getLoginId(), googleUser.getRole());
        String jwtRefreshToken = jwtProvider.createRefreshToken(googleUser.getLoginId());

        googleUser.updateRefresh(jwtRefreshToken);
        userRepository.save(googleUser);  // DB에 저장

        // 응답 헤더에 JWT 토큰 추가
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAccessToken);
        response.setStatus(HttpServletResponse.SC_OK);

        res.add(accessToken);  // Access Token
        res.add(googleUser.getNickname());  // 사용자 닉네임

        return res;
    }

    // Access Token 발급받는 메서드
    private String getToken(String code) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com/token")
                .encode()
                .build()
                .toUri();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUri);
        body.add("grant_type", "authorization_code");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    // 구글 사용자 정보 조회 메서드
    private GoogleUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v3/userinfo")
                .encode()
                .build()
                .toUri();

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("sub").asText();
        String name = jsonNode.get("name").asText();
        String email = jsonNode.get("email").asText();

        return new GoogleUserInfoDto(id, name, email);
    }

    // 구글 사용자 등록 또는 로그인
    private UserEntity registerGoogleUserIfNeeded(GoogleUserInfoDto googleUserInfo) {
        String socialId = googleUserInfo.getId();
        UserEntity googleUser = userRepository.findBySocialId(socialId).orElse(null);  // socialId로 검색

        if (googleUser == null) {
            String googleEmail = googleUserInfo.getEmail();
            UserEntity sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);
            if (sameEmailUser != null) {
                googleUser = sameEmailUser;
                googleUser = googleUser.socialIdUpdate(socialId);
            } else {
                String password = UUID.randomUUID().toString();  // 임시 비밀번호
                String encodedPassword = passwordEncoder.encode(password);
                String loginId = UUID.randomUUID().toString();  // 임시 로그인 ID
                String email = googleUserInfo.getEmail();

                googleUser = new UserEntity(email, encodedPassword, googleUserInfo.getName(), UserStatusEnum.ACTIVE, UserRoleEnum.USER, socialId, loginId);
            }
        }

        return googleUser;
    }
}
