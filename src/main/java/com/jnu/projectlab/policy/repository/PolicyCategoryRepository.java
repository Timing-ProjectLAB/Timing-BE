package com.jnu.projectlab.policy.repository;

import com.jnu.projectlab.policy.entity.PolicyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyCategoryRepository extends JpaRepository<PolicyCategory, Long> {

    // 이 메서드가 없어서 오류 발생
    Optional<PolicyCategory> findByPolicyIdAndCategoryId(String policyId, Integer categoryId);
}