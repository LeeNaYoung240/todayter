package com.todayter.global.oauth2.service;

import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2UserService로 사용자 정보 조회
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 제공자 구분 (kakao, google, naver)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 제공자별 속성 처리
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = convertAttributes(provider, attributes);

        // DB에서 사용자 조회/생성
        UserEntity user = userRepository.findByUsername(userInfo.getId())
                .orElseGet(() -> registerUser(userInfo, provider));

        // UserDetailsImpl 반환 (OAuth2 속성 포함)
        return new UserDetailsImpl(user, attributes);
    }

    // 제공자별 속성 변환
    private OAuth2UserInfo convertAttributes(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "kakao" -> extractKakaoUserInfo(attributes);
            case "google" -> extractGoogleUserInfo(attributes);
            case "naver" -> extractNaverUserInfo(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    // 카카오 사용자 정보 추출
    private OAuth2UserInfo extractKakaoUserInfo(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return new OAuth2UserInfo(
                String.valueOf(attributes.get("id")),
                (String) account.get("email"),
                (String) properties.get("nickname")
        );
    }

    // 구글 사용자 정보 추출
    private OAuth2UserInfo extractGoogleUserInfo(Map<String, Object> attributes) {
        return new OAuth2UserInfo(
                (String) attributes.get("sub"),
                (String) attributes.get("email"),
                (String) attributes.get("name")
        );
    }

    // 네이버 사용자 정보 추출
    private OAuth2UserInfo extractNaverUserInfo(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            throw new IllegalArgumentException("Naver 응답에 'response' 객체가 없습니다");
        }

        return new OAuth2UserInfo(
                (String) response.get("id"),
                (String) response.get("email"),
                (String) response.get("name")
        );
    }

    // 사용자 등록 로직
    private UserEntity registerUser(OAuth2UserInfo userInfo, String provider) {
        String email = userInfo.getEmail();
        String providerId = userInfo.getId();

        // 1. 같은 이메일로 가입된 사용자 확인
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            user.setUsername(providerId); // 기존 사용자에 소셜 ID 연결
            return userRepository.save(user);
        }

        // 2. 신규 사용자 생성
        String tempPassword = UUID.randomUUID().toString();
        String uniqueNickname = generateUniqueNickname(userInfo.getNickname());

        return userRepository.save(new UserEntity(
                providerId,
                email,
                passwordEncoder.encode(tempPassword),
                uniqueNickname,
                userInfo.getNickname(), // 프로필 이미지 대신 닉네임 사용
                UserStatusEnum.ACTIVE,
                UserRoleEnum.USER
        ));
    }

    // 고유 닉네임 생성
    private String generateUniqueNickname(String baseNickname) {
        String nickname = baseNickname;
        int suffix = 1;
        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + suffix++;
        }
        return nickname;
    }

    // OAuth2 사용자 정보 DTO
    @Getter
    @AllArgsConstructor
    private static class OAuth2UserInfo {
        private final String id;
        private final String email;
        private final String nickname;
    }
}
