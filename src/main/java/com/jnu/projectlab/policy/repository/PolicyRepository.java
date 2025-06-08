package com.jnu.projectlab.policy.repository;

import com.jnu.projectlab.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {
    // policyId로 Policy 조회
    Policy findByPolicyId(String policyId);
}