package com.todayter.domain.user.service;

import com.todayter.domain.user.dto.*;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.entity.UserRoleEnum;
import com.todayter.domain.user.entity.UserStatusEnum;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import com.todayter.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtProvider jwtProvider;
    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void signup(SignupRequestDto signupDto) {

        if (isUserNameExist(signupDto.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (isEmailExist(signupDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (isNicknameExist(signupDto.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        UserRoleEnum userRole = UserRoleEnum.USER;
        if (!signupDto.getAdminToken().isEmpty()) {
            if (!ADMIN_TOKEN.equals(signupDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            userRole = userRole.ADMIN;
        }

        UserStatusEnum userStatus = UserStatusEnum.ACTIVE;
        UserEntity userEntity = new UserEntity(signupDto, userStatus, userRole);

        String encodedPassword = bCryptPasswordEncoder.encode(signupDto.getPassword());
        userEntity.encryptionPassword(encodedPassword);

        userRepository.save(userEntity);
    }

    @Transactional
    public void checkUserExistence(CheckUserExistenceRequestDto requestDto) {
        if (isEmailExist(requestDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Transactional
    public void logout(UserEntity user) {
        user.updateRefresh(null);
        userRepository.save(user);
    }

    public void refreshAccessToken(String nickname, HttpServletResponse response) {

        UserEntity user = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!jwtProvider.validateRefreshToken(user.getRefreshToken())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_VALIDATE);
        }

        String accessToken = jwtProvider.createAccessToken(user.getNickname(), user.getRole());

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        response.setStatus(HttpServletResponse.SC_OK);
    }


    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(UserEntity user) {

        return new ProfileResponseDto(user.getId(), user.getUsername(), user.getNickname(), user.getName(),user.getStatus(), user.getRole());
    }

    @Transactional
    public ProfileResponseDto updateNickname(UserEntity user, String nickname) {

        if (isNicknameExist(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        user.updateNickname(nickname);
        userRepository.save(user);

        return getProfile(user);
    }

    public void updatePassword(UserEntity user, EditPasswordRequestDto editPasswordRequestDto) {

        if (!bCryptPasswordEncoder.matches(editPasswordRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (!editPasswordRequestDto.getNewPassword().equals(editPasswordRequestDto.getConfirmNewPassword())) {
            throw new CustomException(ErrorCode.CONFIRM_NEW_PASSWORD_NOT_MATCH);
        }

        if (editPasswordRequestDto.getNewPassword().equals(editPasswordRequestDto.getPassword())) {
            throw new CustomException(ErrorCode.NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD);
        }

        String encodePassword = bCryptPasswordEncoder.encode(editPasswordRequestDto.getNewPassword());

        user.updatePassword(encodePassword);
        userRepository.save(user);
    }

    @Transactional
    public void promoteToAdmin(Long targetUserId, UserEntity currentUser) {

        if (!currentUser.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_ACCESS);
        }

        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (targetUser.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.ALREADY_ADMIN);
        }

        targetUser.setRole(UserRoleEnum.ADMIN);

        userRepository.save(targetUser);

    }

    @Transactional
    public void withdrawUser(UserEntity user, String password) {

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        user.updateStatus(UserStatusEnum.WITHDRAW);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(UserEntity currentUser) {

        if (!currentUser.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_ACCESS);
        }

        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .map(UserResponseDto::new)
                .toList();
    }

    @Transactional
    public void blockUser(Long userId, UserEntity userDetails) {
        UserEntity userToBlock = getUserById(userId);

        if (!userDetails.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_BLOCK);
        }

        if (userToBlock.isBlock()) {
            throw new CustomException(ErrorCode.ALREADY_BLOCK);
        }

        userToBlock.updateStatus(UserStatusEnum.BLOCK);
    }

    public long getTotalUserCnt() {
        return userRepository.count();
    }

    @Transactional
    public boolean isUserNameExist(String username) {

        return userRepository.existsByUsername(username);
    }

    @Transactional
    public boolean isUsernameExist(String username) {

        return userRepository.existsByUsername(username);
    }

    @Transactional
    public boolean isNicknameExist(String nickname) {

        return userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public boolean isEmailExist(String email) {

        return userRepository.existsByEmail(email);
    }

    private UserEntity getUserById(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

}
