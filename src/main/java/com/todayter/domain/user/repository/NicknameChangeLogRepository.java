package com.todayter.domain.user.repository;

import com.todayter.domain.user.entity.NicknameChangeLog;
import com.todayter.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NicknameChangeLogRepository extends JpaRepository<NicknameChangeLog, Long> {

    List<NicknameChangeLog> findByUserAndChangedAtAfter(UserEntity user, LocalDateTime after);

}
