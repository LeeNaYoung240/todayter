package com.todayter.global.security;

import com.todayter.domain.entity.UserEntity;
import com.todayter.domain.entity.UserStatusEnum;
import com.todayter.domain.repository.UserRepository;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // 사용자 정보를 불러옴
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        UserEntity user = userOptional.get();

        // 계정이 차단된 경우 예외 발생
        if (user.getStatus().equals(UserStatusEnum.BLOCK)) {
            throw new CustomException(ErrorCode.USER_NOT_ACTIVE_BLOCK);
        }

        if(user.getStatus().equals(UserStatusEnum.WITHDRAW)) {
            throw new CustomException(ErrorCode.USER_NOT_ACTIVE_WITHDRAW);
        }

        return new UserDetailsImpl(user);
    }
}
