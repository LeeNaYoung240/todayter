package com.todayter.domain.cheer.entity;

import com.todayter.domain.user.entity.UserEntity;
import com.todayter.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "cheer_log", uniqueConstraints = {@UniqueConstraint(columnNames = {"supporter_id", "target_id", "cheered_date"})
})
public class CheerLog extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supporter_id", nullable = false)
    private UserEntity supporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private UserEntity target;

    @Column(nullable = false)
    private String cheeredDate;

    public CheerLog(UserEntity supporter, UserEntity target, String cheeredDate) {
        this.supporter = supporter;
        this.target = target;
        this.cheeredDate = cheeredDate;
    }

}
