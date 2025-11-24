package com.jnu.projectlab.policyview.service;

import com.jnu.projectlab.policy.entity.Policy;
import com.jnu.projectlab.policy.repository.PolicyRepository;
import com.jnu.projectlab.policyview.entity.UserPolicyView;
import com.jnu.projectlab.policyview.repository.UserPolicyViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사용자 정책 조회 이력 Service
 * 정책 조회수 증가 및 조회 이력 관리를 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPolicyViewService {

    private final UserPolicyViewRepository userPolicyViewRepository;
    private final PolicyRepository policyRepository;

    /**
     * 조회수 증가 처리
     * 정책을 조회할 때마다 조회수가 증가합니다.
     *
     * @param policyId 정책 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void incrementViewCountIfNeeded(String policyId, String userId) {
        // 1. 정책 존재 여부 확인
        Policy policy = policyRepository.findByPolicyId(policyId);
        if (policy == null) {
            log.warn("존재하지 않는 정책 조회 시도: policyId={}, userId={}", policyId, userId);
            return;
        }

        // 2. 조회수 증가
        policy.incrementInquiryCount();
        policyRepository.save(policy);

        // 3. 조회 기록 저장 (통계 분석용)
        UserPolicyView view = UserPolicyView.builder()
                .userId(userId)
                .policyId(policyId)
                .viewedAt(LocalDateTime.now())
                .build();
        userPolicyViewRepository.save(view);

        log.debug("조회수 증가: policyId={}, userId={}, inquiryCount={}", 
                policyId, userId, policy.getInquiryCount());
    }

    /**
     * 특정 정책의 총 조회 수 조회
     * (통계 분석용)
     *
     * @param policyId 정책 ID
     * @return 총 조회 수
     */
    public long getTotalViewCount(String policyId) {
        return userPolicyViewRepository.countByPolicyId(policyId);
    }

    /**
     * 특정 사용자의 총 조회 수 조회
     * (사용자 활동 통계용)
     *
     * @param userId 사용자 ID
     * @return 총 조회 수
     */
    public long getUserTotalViewCount(String userId) {
        return userPolicyViewRepository.countByUserId(userId);
    }
}

