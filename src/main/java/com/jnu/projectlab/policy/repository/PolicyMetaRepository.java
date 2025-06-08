package com.jnu.projectlab.policy.repository;

import com.jnu.projectlab.policy.entity.PolicyMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyMetaRepository extends JpaRepository<PolicyMeta, String> {
    // policyId로 PolicyMeta 조회
    PolicyMeta findByPolicyId(String policyId);
}