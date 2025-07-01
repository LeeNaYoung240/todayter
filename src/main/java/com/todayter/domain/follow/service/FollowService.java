package com.todayter.domain.follow.service;

import com.todayter.domain.follow.dto.FollowRequestDto;
import com.todayter.domain.follow.dto.FollowResponseDto;
import com.todayter.domain.follow.entity.Follow;
import com.todayter.domain.follow.repository.FollowRepository;
import com.todayter.domain.user.entity.UserEntity;
import com.todayter.domain.user.repository.UserRepository;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponseDto createFollow(FollowRequestDto followRequestDto, UserEntity follower) {

        UserEntity following = userRepository.findById(followRequestDto.getFollowing())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (follower.getId().equals(following.getId())) {
            throw new CustomException(ErrorCode.INVALID_FOLLOW_REQUEST);
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new CustomException(ErrorCode.ALREADY_FOLLOWING);
        }

        Follow follow = new Follow(follower, following);
        Follow savedFollow = followRepository.save(follow);

        return new FollowResponseDto(savedFollow);
    }

    @Transactional
    public void unfollow(UserEntity follower, Long followingId) {
        UserEntity following = userRepository.findById(followingId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowings(UserEntity user) {

        return followRepository.findAllByFollower(user).stream()
                .map(FollowResponseDto::new)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowers(UserEntity user) {

        return followRepository.findAllByFollowing(user).stream()
                .map(FollowResponseDto::new)
                .collect(toList());
    }

}
