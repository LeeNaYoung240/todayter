package com.todayter.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.todayter.domain.user.dto.*;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.service.GoogleService;
import com.todayter.domain.user.service.KakaoService;
import com.todayter.domain.user.service.NaverService;
import com.todayter.domain.user.service.UserService;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final GoogleService googleService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDto> signup(@RequestBody SignupRequestDto signupDto) {

        userService.signup(signupDto);

        return new ResponseEntity<>(new CommonResponseDto(201, "회원가입에 성공했습니다. 🎉", null), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.logout(userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto(200, "로그아웃에 성공하였습니다. 🎉", null));
    }

    @PostMapping("/token")
    public ResponseEntity<CommonResponseDto> refreshAccessToken(@RequestBody RefreshAccessTokenRequestDto refreshAccessTokenRequestDto,
                                                                HttpServletResponse response) {

        userService.refreshAccessToken(refreshAccessTokenRequestDto.getNickname(), response);

        return ResponseEntity.ok(new CommonResponseDto(200, "액세스 토큰 재발급에 성공하였습니다. 🎉", null));
    }

    @GetMapping("/profile")
    public ResponseEntity<CommonResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        ProfileResponseDto profileResponseDto = userService.getProfile(userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto(200, "프로필 조회에 성공하였습니다. 🎉", profileResponseDto));
    }

    @PatchMapping("/profiles/nickname")
    public ResponseEntity<CommonResponseDto> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @Valid @RequestBody EditNicknameRequestDto editNicknameRequestDto) {

        ProfileResponseDto profileResponseDto = userService.updateNickname(userDetails.getUser(), editNicknameRequestDto.getNickname());

        return ResponseEntity.ok(new CommonResponseDto(200, "닉네임 수정에 성공하였습니다. 🎉", profileResponseDto));
    }

    @PatchMapping("/profiles/password")
    public ResponseEntity<CommonResponseDto> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @Valid @RequestBody EditPasswordRequestDto editPasswordRequestDto) {

        userService.updatePassword(userDetails.getUser(), editPasswordRequestDto);

        return ResponseEntity.ok(new CommonResponseDto(200, "비밀번호 수정에 성공하였습니다. 🎉", null));
    }

    @GetMapping("/check-username")
    public ResponseEntity<CommonResponseDto> checkLoginId(@RequestParam(name = "username") String username) {

        boolean isExist = userService.isUsernameExist(username);

        return ResponseEntity.ok(new CommonResponseDto(200, "아이디 중복 확인에 성공하였습니다. 🎉", isExist));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<CommonResponseDto> checkNickname(@RequestParam(name = "nickname") String nickname) {

        boolean isExist = userService.isNicknameExist(nickname);

        return ResponseEntity.ok(new CommonResponseDto(200, "닉네임 중복 확인에 성공하였습니다. 🎉", isExist));
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<CommonResponseDto> withdrawUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody WithDrawDto withDrawDto) {

        userService.withdrawUser(userDetails.getUser(), withDrawDto.getPassword());

        return ResponseEntity.ok(new CommonResponseDto(200, "회원 탈퇴 되었습니다. 😭", null));

    }

    @PatchMapping("{userId}/block")
    public ResponseEntity<CommonResponseDto> withdrawalUser(@PathVariable Long userId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserEntity user = userDetails.getUser();
        userService.blockUser(userId, user);

        return ResponseEntity.ok(new CommonResponseDto(200, "회원 차단에 성공하였습니다. 🎉", null));
    }


    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<CommonResponseDto> kakaoLogin(@RequestParam String code,
                                                        HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        List<String> kakaoToken = kakaoService.kakaoLogin(code, response);

        return ResponseEntity.ok(new CommonResponseDto(200, "카카오 로그인 성공", kakaoToken));
    }

    @GetMapping("/oauth/naver/callback")
    public ResponseEntity<CommonResponseDto> naverLogin(@RequestParam String code,
                                                        @RequestParam String state,
                                                         HttpServletResponse response) throws JsonProcessingException {
        List<String> naverToken = naverService.naverLogin(code, state, response);
        return ResponseEntity.ok(new CommonResponseDto(200, "네이버 로그인 성공", naverToken));
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<CommonResponseDto> googleLogin(@RequestParam String code,
                                                         HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        List<String> googleToken = googleService.googleLogin(code, response);
        return ResponseEntity.ok(new CommonResponseDto(200, "구글 로그인 성공", googleToken));
    }

}
