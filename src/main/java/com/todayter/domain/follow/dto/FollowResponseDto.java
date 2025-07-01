package com.todayter.domain.follow.dto;

import com.todayter.domain.follow.entity.Follow;
import com.todayter.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FollowResponseDto {

    private Long following;
    private Long follower;
    private UserEntity.Gender followerGender;
    private Integer followerAge;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public FollowResponseDto(Follow follow) {
        this.following = follow.getFollowing().getId();
        this.follower = follow.getFollower().getId();
        this.followerGender = follow.getFollower().getGender();
        this.followerAge = follow.getFollower().getAge();
        this.createdAt = follow.getCreatedAt();
        this.modifiedAt = follow.getModifiedAt();
    }

}
