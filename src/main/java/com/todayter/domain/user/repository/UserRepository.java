package com.todayter.domain.user.repository;

import com.todayter.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByNickname(String nickname);

    Optional<UserEntity> findByEmail(String Email);

}
