package com.todayter.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeStamped {

    // 엔티티 생성 시점 저장
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 엔티티 마지막 수정 시점 저장
    @Column(name = "modifiedAt")
    private LocalDateTime modifiedAt;

    // 한국 시간대에서 현재 시간을 가져오는 메서드
    private LocalDateTime nowInKorea() {
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime nowInKorea = ZonedDateTime.now(koreaZoneId);

        return nowInKorea.toLocalDateTime();
    }

    // 엔티티가 저장되기 전
    @PrePersist
    protected void onCreated() {
        LocalDateTime now = nowInKorea();
        this.createdAt = now;
        this.modifiedAt = now;
    }

    // 엔티티가 수정되기 전
    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = nowInKorea();
    }

    // Getter 추가
    public LocalDateTime getCreatedAt() {

        return createdAt;
    }

    public LocalDateTime getModifiedAt() {

        return modifiedAt;
    }

}
