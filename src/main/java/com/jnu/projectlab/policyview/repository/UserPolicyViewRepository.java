package com.jnu.projectlab.policyview.repository;

import com.jnu.projectlab.policyview.entity.UserPolicyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 사용자 정책 조회 이력 Repository
 */
@Repository
public interface UserPolicyViewRepository extends JpaRepository<UserPolicyView, Long> {

    /**
     * 특정 사용자가 특정 정책을 조회한 최근 기록 조회
     * (중복 조회 방지용 - 24시간 내 조회 여부 확인)
     */
    @Query("SELECT upv FROM UserPolicyView upv " +
           "WHERE upv.userId = :userId AND upv.policyId = :policyId " +
           "AND upv.viewedAt >= :thresholdTime " +
           "ORDER BY upv.viewedAt DESC")
    Optional<UserPolicyView> findRecentViewByUserIdAndPolicyId(
            @Param("userId") String userId,
            @Param("policyId") String policyId,
            @Param("thresholdTime") LocalDateTime thresholdTime
    );

    /**
     * 특정 사용자가 특정 정책을 24시간 이내에 조회했는지 확인
     * (중복 조회 방지용)
     */
    @Query("SELECT COUNT(upv) > 0 FROM UserPolicyView upv " +
           "WHERE upv.userId = :userId AND upv.policyId = :policyId " +
           "AND upv.viewedAt >= :thresholdTime")
    boolean existsByUserIdAndPolicyIdWithin24Hours(
            @Param("userId") String userId,
            @Param("policyId") String policyId,
            @Param("thresholdTime") LocalDateTime thresholdTime
    );

    /**
     * 특정 정책의 총 조회 수 조회
     * (통계 분석용)
     */
    long countByPolicyId(String policyId);

    /**
     * 특정 사용자의 총 조회 수 조회
     * (사용자 활동 통계용)
     */
    long countByUserId(String userId);
}

