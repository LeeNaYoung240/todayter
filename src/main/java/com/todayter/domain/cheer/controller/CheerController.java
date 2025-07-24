package com.todayter.domain.cheer.controller;

import com.todayter.domain.cheer.service.CheerService;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cheers")
public class CheerController {

    private final CheerService cheerService;

    // 하루 1회 응원하기
    @PostMapping("/{targetUserId}")
    public ResponseEntity<CommonResponseDto> cheer(@PathVariable Long targetUserId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserEntity supporter = userDetails.getUser();
        cheerService.cheer(supporter, targetUserId);

        return ResponseEntity.ok(new CommonResponseDto(200, "응원 성공 🎉", null));
    }

    // 특정 사용자의 총 응원 수 조회
    @GetMapping("/count/{userId}")
    public ResponseEntity<CommonResponseDto> getCheerCount(@PathVariable Long userId) {
        long count = cheerService.getCheerCount(userId);
        return ResponseEntity.ok(new CommonResponseDto(200, "응원 수 조회 성공 🎉", count));
    }

    // 오늘 이미 응원했는지 체크
    @GetMapping("/check-today/{targetUserId}")
    public ResponseEntity<CommonResponseDto> checkCheeredToday(@PathVariable Long targetUserId,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserEntity supporter = userDetails.getUser();
        boolean cheered = cheerService.checkCheeredToday(supporter, targetUserId);
        return ResponseEntity.ok(new CommonResponseDto(200, "오늘 응원 여부 확인 성공 🎉", cheered));
    }
}
