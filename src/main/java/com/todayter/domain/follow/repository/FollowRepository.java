package com.todayter.domain.follow.repository;

import com.todayter.domain.follow.entity.Follow;
import com.todayter.domain.user.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(UserEntity follower, UserEntity following);

    Optional<Follow> findByFollowerAndFollowing(UserEntity follower, UserEntity following);

    List<Follow> findAllByFollower(UserEntity follower);

    List<Follow> findAllByFollowing(UserEntity following);

    int countByFollowing(UserEntity following);

    Long countByFollowingId(Long followingId);

    @Query("SELECT f.follower.gender, COUNT(f) FROM Follow f WHERE f.following.id = :userId GROUP BY f.follower.gender")
    List<Object[]> countFollowerGenderByUserId(@Param("userId") Long userId);

    @Query("SELECT f.follower.age, COUNT(f) FROM Follow f WHERE f.following.id = :userId AND f.follower.age IS NOT NULL GROUP BY f.follower.age")
    List<Object[]> countFollowerAgeByUserId(@Param("userId") Long userId);

}
