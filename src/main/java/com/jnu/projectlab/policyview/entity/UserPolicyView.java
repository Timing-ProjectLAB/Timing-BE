package com.jnu.projectlab.policyview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정책 조회 이력 엔티티
 * 사용자가 정책 상세 페이지를 조회한 기록을 저장합니다.
 * 중복 조회 방지 및 통계 분석에 사용됩니다.
 */
@Entity
@Table(
    name = "user_policy_view",
    indexes = {
        @Index(name = "idx_view_user_policy", columnList = "user_id, policy_id"),
        @Index(name = "idx_view_user", columnList = "user_id"),
        @Index(name = "idx_view_policy", columnList = "policy_id"),
        @Index(name = "idx_view_time", columnList = "viewed_at")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicyView {

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
     * 조회 일시 (정책 상세 페이지를 본 시간)
     */
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    /**
     * 엔티티 생성 시 자동으로 현재 시간 설정
     */
    @PrePersist
    protected void onCreate() {
        if (viewedAt == null) {
            viewedAt = LocalDateTime.now();
        }
    }
}

