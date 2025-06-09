package com.jnu.projectlab.policy.repository;

import com.jnu.projectlab.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {
    // policyId로 Policy 조회
    Policy findByPolicyId(String policyId);

    /**
     * 특정 정책 ID 목록으로 정책들 조회 (IN 절 사용)
     */
    List<Policy> findByPolicyIdIn(List<String> policyIds);
    
    /**
     * 정책 ID 목록으로 정책들 조회 + 정렬
     */
    @Query("SELECT p FROM Policy p WHERE p.policyId IN :policyIds ORDER BY p.inquiryCount DESC")
    List<Policy> findByPolicyIdInOrderByInquiryCountDesc(@Param("policyIds") List<String> policyIds);

    /**
     * 조회수 기준 상위 3개 정책 조회 (인기정책용)
     */
    List<Policy> findTop3ByOrderByInquiryCountDesc();
}