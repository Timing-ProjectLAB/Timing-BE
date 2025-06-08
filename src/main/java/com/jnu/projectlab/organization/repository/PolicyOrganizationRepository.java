package com.jnu.projectlab.organization.repository;

import com.jnu.projectlab.organization.entity.PolicyOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyOrganizationRepository extends JpaRepository<PolicyOrganization, Long> {

    // 특정 정책의 연관 기관들 조회
    List<PolicyOrganization> findByPolicyId(String policyId);

    // 특정 기관이 연관된 정책들 조회
    List<PolicyOrganization> findByOrganizationId(String organizationId);

    // 특정 역할의 기관들 조회
    List<PolicyOrganization> findByRole(String role);

    // 정책-기관-역할 조합으로 조회 (중복 체크용)
    Optional<PolicyOrganization> findByPolicyIdAndOrganizationIdAndRole(String policyId, String organizationId, String role);
}