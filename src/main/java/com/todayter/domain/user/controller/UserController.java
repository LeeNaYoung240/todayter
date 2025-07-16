package com.todayter.domain.user.controller;

import com.todayter.domain.user.dto.*;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.service.EmailService;
import com.todayter.domain.user.service.UserService;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import io.netty.handler.codec.MessageAggregationException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/me")
    public ResponseEntity<CommonResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto(401, "인증되지 않은 사용자입니다.", null));
        }

        UserEntity user = userDetails.getUser();
        UserInfoResponseDto userInfo = new UserInfoResponseDto(
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(new CommonResponseDto(200, "사용자 정보 조회 성공했습니다. 🎉", userInfo));
    }


    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDto> signup(@RequestBody SignupRequestDto signupDto) {

        userService.signup(signupDto);

        return new ResponseEntity<>(new CommonResponseDto(201, "회원가입에 성공했습니다. 🎉", null), HttpStatus.CREATED);
    }

    @PostMapping("/check-existence")
    public ResponseEntity<CommonResponseDto> checkUserExistence(@RequestBody CheckUserExistenceRequestDto requestDto) {
        userService.checkUserExistence(requestDto);

        return new ResponseEntity<>(new CommonResponseDto(200, "사용 가능한 이메일과 이름입니다. 🎉", null), HttpStatus.OK);
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

        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto(401, "인증되지 않은 사용자입니다.", null));
        }

        ProfileResponseDto profileResponseDto = userService.getProfile(userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto(200, "프로필 조회에 성공하였습니다. 🎉", profileResponseDto));
    }

    @GetMapping("/public-profile/{userId}")
    public ResponseEntity<CommonResponseDto> getPublicProfile(@PathVariable Long userId) {
        ProfileResponseDto profile = userService.getPublicProfile(userId);

        return ResponseEntity.ok(new CommonResponseDto(200, "공개 프로필 조회 성공 🎉", profile));
    }


    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto<String>> uploadProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                        @RequestPart("file") MultipartFile file) {

        String fileUrl = userService.uploadProfileImage(userDetails.getUser(), file);
        return ResponseEntity.ok(new CommonResponseDto<>(200, "프로필 이미지 업로드 성공", fileUrl));
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<CommonResponseDto<String>> deleteProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteProfileImage(userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto<>(200, "프로필 이미지 삭제 완료", null));
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

    @PostMapping("/send-Email")
    public ResponseEntity<CommonResponseDto> sendNumber(@Validated @RequestBody UserCertificateRequestDto dto) throws NoSuchAlgorithmException, MessageAggregationException, MessagingException {

        emailService.sendNumber(dto.getEmail());

        return ResponseEntity.ok(new CommonResponseDto(200, "이메일 전송에 성공하였습니다. 🎉", null));

    }

    @GetMapping("/verify")
    public ResponseEntity<CommonResponseDto> verifyCertificationNumber(@RequestParam(name = "certificationNumber") String certificationNumber,
                                                                       @RequestParam(name = "email") String email) {
        emailService.verifyEmail(certificationNumber, email);

        return ResponseEntity.ok(new CommonResponseDto(200, "이메일 인증에 성공하였습니다. 🎉", null));
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto> getUsers(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<UserResponseDto> users = userService.getAllUsers(userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto(200, "회원 목록 조회에 성공하였습니다. 🎉", users));
    }

    @PatchMapping("/{userId}/promote")
    public ResponseEntity<CommonResponseDto> promoteToAdmin(@PathVariable Long userId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.promoteToAdmin(userId, userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto(200, "회원 권한을 ADMIN으로 승격하였습니다. 🎉", null));
    }

    @GetMapping("/user-cnt")
    public ResponseEntity<CommonResponseDto<Long>> getUserCnt() {
        long totalUsers = userService.getTotalUserCnt();

        return ResponseEntity.ok(new CommonResponseDto<>(200, "전체 회원 수 조회에 성공하였습니다. 🎉", totalUsers));
    }

}
