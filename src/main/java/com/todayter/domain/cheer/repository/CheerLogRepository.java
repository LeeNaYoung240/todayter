package com.todayter.domain.cheer.repository;

import com.todayter.domain.cheer.entity.CheerLog;
import com.todayter.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheerLogRepository extends JpaRepository<CheerLog, Long> {

    Optional<CheerLog> findBySupporterAndTargetAndCheeredDate(UserEntity supporter, UserEntity target, String date);

    Long countByTarget(UserEntity target);
}
