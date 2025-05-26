package com.todayter.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayter.domain.user.dto.NaverUserInfoDto;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${SOCIAL_NAVER_CLIENT_ID}")
    private String clientId;

    @Value("${SOCIAL_NAVER_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${SOCIAL_NAVER_REDIRECT_URI}")
    private String redirectUri;

    public List<String> naverLogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getAccessToken(code, state);
        NaverUserInfoDto userInfo = getUserInfo(accessToken);

        UserEntity user = registerIfNeeded(userInfo);
        String jwtAccessToken = jwtProvider.createAccessToken(user.getUsername(), user.getRole());
        String jwtRefreshToken = jwtProvider.createRefreshToken(user.getUsername());
        user.updateRefresh(jwtRefreshToken);
        userRepository.save(user);

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAccessToken);
        response.setStatus(HttpServletResponse.SC_OK);

        return List.of(accessToken, user.getNickname());
    }

    private String getAccessToken(String code, String state) throws JsonProcessingException {
        URI uri = UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .queryParam("redirect_uri", redirectUri)
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private NaverUserInfoDto getUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        JsonNode responseNode = jsonNode.get("response");

        return new NaverUserInfoDto(
                responseNode.get("id").asText(),
                responseNode.get("email").asText(),
                responseNode.get("name").asText()
        );
    }

    private UserEntity registerIfNeeded(NaverUserInfoDto userInfo) {
        String naverId = userInfo.getId();

        UserEntity user = userRepository.findByUsername(naverId).orElse(null);

        if (user == null) {
            String email = userInfo.getEmail();
            UserEntity sameEmailUser = userRepository.findByEmail(email).orElse(null);

            if (sameEmailUser != null) {
                sameEmailUser.setUsername(naverId);
                user = sameEmailUser;
            } else {
                String tempPassword = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(tempPassword);

                user = new UserEntity(
                        naverId,
                        userInfo.getEmail(),
                        encodedPassword,
                        userInfo.getNickname(),
                        UserStatusEnum.ACTIVE,
                        UserRoleEnum.USER
                );
            }
        }
        return user;
    }
}