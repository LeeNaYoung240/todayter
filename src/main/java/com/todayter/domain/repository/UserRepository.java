package com.todayter.domain.repository;

import com.todayter.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByNickname(String nickname);

}
