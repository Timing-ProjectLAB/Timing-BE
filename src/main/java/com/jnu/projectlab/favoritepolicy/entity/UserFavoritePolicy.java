package com.jnu.projectlab.favoritepolicy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 즐겨찾기 정책 엔티티
 * 사용자가 좋아요(하트)를 누른 정책을 저장합니다.
 */
@Entity
@Table(
    name = "user_favorite_policy",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_user_policy_favorite",
            columnNames = {"user_id", "policy_id"}
        )
    },
    indexes = {
        @Index(name = "idx_favorite_user", columnList = "user_id"),
        @Index(name = "idx_favorite_policy", columnList = "policy_id"),
        @Index(name = "idx_favorite_time", columnList = "favorited_at")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavoritePolicy {

    /**
     * 자동 증가 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 사용자 ID (users 테이블 참조)
     */
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    /**
     * 정책 ID (policy 테이블 참조)
     */
    @Column(name = "policy_id", nullable = false)
    private String policyId;

    /**
     * 즐겨찾기 추가 일시 (좋아요 누른 시간)
     */
    @Column(name = "favorited_at", nullable = false)
    private LocalDateTime favoritedAt;

    /**
     * 엔티티 생성 시 자동으로 현재 시간 설정
     */
    @PrePersist
    protected void onCreate() {
        if (favoritedAt == null) {
            favoritedAt = LocalDateTime.now();
        }
    }
}

