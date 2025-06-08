package com.jnu.projectlab.policy.repository;

import com.jnu.projectlab.policy.entity.PolicyCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyConditionRepository extends JpaRepository<PolicyCondition, String> {
    // policyId로 PolicyCondition 조회
    PolicyCondition findByPolicyId(String policyId);
}