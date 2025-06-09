package com.jnu.projectlab.policy.repository;

import com.jnu.projectlab.policy.entity.PolicyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyCategoryRepository extends JpaRepository<PolicyCategory, Long> {

    // 이 메서드가 없어서 오류 발생
    Optional<PolicyCategory> findByPolicyIdAndCategoryId(String policyId, Integer categoryId);
    
    // 🆕 카테고리 필터링을 위한 새로운 메서드들
    
    /**
     * 특정 카테고리에 속한 정책 ID 목록 조회
     */
    @Query("SELECT pc.policyId FROM PolicyCategory pc WHERE pc.categoryId = :categoryId")
    List<String> findPolicyIdsByCategoryId(@Param("categoryId") Integer categoryId);
    
    /**
     * 특정 카테고리 그룹에 속한 정책 ID 목록 조회 (카테고리를 거쳐서)
     */
    @Query("SELECT DISTINCT pc.policyId FROM PolicyCategory pc " +
           "JOIN Category c ON pc.categoryId = c.id " +
           "WHERE c.group.id = :categoryGroupId")
    List<String> findPolicyIdsByCategoryGroupId(@Param("categoryGroupId") Long categoryGroupId);
    
    /**
     * 정책 ID로 해당 정책의 모든 카테고리 ID 조회
     */
    @Query("SELECT pc.categoryId FROM PolicyCategory pc WHERE pc.policyId = :policyId")
    List<Integer> findCategoryIdsByPolicyId(@Param("policyId") String policyId);
}