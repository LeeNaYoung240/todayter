package com.todayter.domain.follow.repository;

import com.todayter.domain.follow.entity.Follow;
import com.todayter.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(UserEntity follower, UserEntity following);

    Optional<Follow> findByFollowerAndFollowing(UserEntity follower, UserEntity following);

    List<Follow> findAllByFollower(UserEntity follower);

    List<Follow> findAllByFollowing(UserEntity following);

    int countByFollowing(UserEntity following);

}
