package com.todayter.domain.follow.controller;

import com.todayter.domain.follow.dto.FollowRequestDto;
import com.todayter.domain.follow.dto.FollowResponseDto;
import com.todayter.domain.follow.service.FollowService;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.dto.CommonResponseDto;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import com.todayter.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CommonResponseDto<FollowResponseDto>> createFollow(@RequestBody FollowRequestDto followRequestDto,
                                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserEntity currentUser = userDetails.getUser();
        FollowResponseDto responseDto = followService.createFollow(followRequestDto, currentUser);

        return new ResponseEntity<>(new CommonResponseDto<>(HttpStatus.CREATED.value(), "팔로우에 성공하였습니다. 🎉", responseDto), HttpStatus.CREATED);

    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<CommonResponseDto<Void>> unfollow(@PathVariable Long followingId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserEntity currentUser = userDetails.getUser();
        followService.unfollow(currentUser, followingId);

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "언팔로우에 성공하였습니다. 🎉", null));
    }

    @GetMapping("/followings")
    public ResponseEntity<CommonResponseDto<List<FollowResponseDto>>> getFollowings(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<FollowResponseDto> followings = followService.getFollowings(userDetails.getUser());

        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "내가 팔로우한 목록 조회 성공 🎉", followings));
    }

    @GetMapping("/followers")
    public ResponseEntity<CommonResponseDto<List<FollowResponseDto>>> getFollowers(@RequestParam(value = "targetUserId", required = false) Long targetUserId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserEntity targetUser;

        if (targetUserId != null) {
            targetUser = userRepository.findById(targetUserId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        } else {
            targetUser = userDetails.getUser();
        }

        List<FollowResponseDto> followers = followService.getFollowers(targetUser);
        return ResponseEntity.ok(new CommonResponseDto<>(HttpStatus.OK.value(), "팔로워 조회 성공 🎉", followers));
    }

}
