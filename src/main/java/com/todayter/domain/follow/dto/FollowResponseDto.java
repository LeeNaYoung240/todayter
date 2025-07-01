package com.todayter.domain.follow.dto;

import com.todayter.domain.follow.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FollowResponseDto {

    private Long following;
    private Long follower;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public FollowResponseDto(Follow follow) {
        this.following = follow.getFollowing().getId();
        this.follower = follow.getFollower().getId();
        this.createdAt = follow.getCreatedAt();
        this.modifiedAt = follow.getModifiedAt();
    }

}
