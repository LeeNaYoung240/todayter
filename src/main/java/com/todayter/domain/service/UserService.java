package com.todayter.domain.service;

import com.todayter.domain.dto.EditPasswordRequestDto;
import com.todayter.domain.dto.ProfileResponseDto;
import com.todayter.domain.dto.SignupRequestDto;
import com.todayter.domain.entity.UserEntity;
import com.todayter.domain.entity.UserRoleEnum;
import com.todayter.domain.entity.UserStatusEnum;
import com.todayter.domain.repository.UserRepository;
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


@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtProvider jwtProvider;
    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void signup(SignupRequestDto signupDto){

        if(isUserNameExist(signupDto.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        UserRoleEnum userRole = UserRoleEnum.USER;
        if(!signupDto.getAdminToken().isEmpty()) {
            if(!ADMIN_TOKEN.equals(signupDto.getAdminToken())) {
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
    public void logout(UserEntity user) {
        user.updateRefresh(null);
        userRepository.save(user);
    }

    public void refreshAccessToken(String nickname, HttpServletResponse response) {

        UserEntity user = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!jwtProvider.validateRefreshToken(user.getRefreshToken())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_VALIDATE);
        }

        String accessToken = jwtProvider.createAccessToken(user.getNickname(), user.getRole());

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        response.setStatus(HttpServletResponse.SC_OK);
    }


    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(UserEntity user) {

        return new ProfileResponseDto(user.getId(), user.getUsername(), user.getNickname(), user.getStatus(), user.getRole());
    }

    @Transactional
    public ProfileResponseDto updateNickname(UserEntity user, String nickname) {

        if(isNicknameExist(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        user.updateNickname(nickname);
        userRepository.save(user);

        return getProfile(user);
    }

    public void updatePassword(UserEntity user, EditPasswordRequestDto editPasswordRequestDto) {

        if(!bCryptPasswordEncoder.matches(editPasswordRequestDto.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        if(!editPasswordRequestDto.getNewPassword().equals(editPasswordRequestDto.getConfirmNewPassword())) {
            throw new CustomException(ErrorCode.CONFIRM_NEW_PASSWORD_NOT_MATCH);
        }

        if(editPasswordRequestDto.getNewPassword().equals(editPasswordRequestDto.getPassword())) {
            throw new CustomException(ErrorCode.NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD);
        }

        String encodePassword = bCryptPasswordEncoder.encode(editPasswordRequestDto.getNewPassword());

        user.updatePassword(encodePassword);
        userRepository.save(user);
    }

    @Transactional
    public void withdrawUser(UserEntity user, String password) {

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        user.updateStatus(UserStatusEnum.WITHDRAW);
        userRepository.save(user);
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

}
