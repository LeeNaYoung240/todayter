package com.todayter.domain.follow.entity;

import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Follow extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity following;

    @ManyToOne
    private UserEntity follower;

    public Follow(UserEntity follower, UserEntity following) {
        this.follower = follower;
        this.following = following;
    }

}
