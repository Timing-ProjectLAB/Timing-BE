package com.jnu.projectlab.common.repository;

import com.jnu.projectlab.common.entity.PolicyKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyKeywordRepository extends JpaRepository<PolicyKeyword, Long> {
    // policyId로 PolicyKeyword 목록 조회
    List<PolicyKeyword> findByPolicyId(String policyId);
    Optional<PolicyKeyword> findByPolicyIdAndKeyword(String policyId, String keyword);
}