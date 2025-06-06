package com.jnu.projectlab.organization.entity;

import com.jnu.projectlab.organization.organizationCategory.PolicyOrganizationId;  // 이렇게 수정
import com.jnu.projectlab.policy.entity.Policy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "policy_organization")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PolicyOrganizationId.class)
public class PolicyOrganization {
    @Id
    @Column(name = "policy_id")
    private String policyId;  // 정책번호

    @Id
    @Column(name = "organization_id")
    private String organizationId;  // 기관코드

    @Column(name = "role")
    private String role;  // 기관 역할 (supervising, operating, registering)
}
