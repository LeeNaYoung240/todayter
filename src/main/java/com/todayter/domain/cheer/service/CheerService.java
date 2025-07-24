package com.todayter.domain.cheer.service;

import com.todayter.domain.cheer.entity.CheerLog;
import com.todayter.domain.cheer.repository.CheerLogRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CheerService {

    private final CheerLogRepository cheerLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void cheer(UserEntity supporter, Long targetUserId) {
        UserEntity target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (supporter.getId().equals(target.getId())) {
            throw new CustomException(ErrorCode.CANNOT_CHEER_YOURSELF);
        }

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        boolean alreadyCheered = cheerLogRepository
                .findBySupporterAndTargetAndCheeredDate(supporter, target, today)
                .isPresent();

        if (alreadyCheered) {
            throw new CustomException(ErrorCode.ALREADY_CHEERED_TODAY);
        }

        cheerLogRepository.save(new CheerLog(supporter, target, today));
    }

    @Transactional(readOnly = true)
    public boolean checkCheeredToday(UserEntity supporter, Long targetUserId) {
        UserEntity target = userRepository .findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        return cheerLogRepository.findBySupporterAndTargetAndCheeredDate(supporter, target, today)
                .isPresent();
    }

    public long getCheerCount(Long targetUserId) {
        UserEntity target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return cheerLogRepository.countByTarget(target);
    }

}
