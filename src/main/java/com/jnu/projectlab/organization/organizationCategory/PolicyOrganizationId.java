package com.jnu.projectlab.organization.organizationCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyOrganizationId implements Serializable {
    private String policyId;        // 정책번호
    private String organizationId;  // 기관코드

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PolicyOrganizationId that = (PolicyOrganizationId) obj;
        return Objects.equals(policyId, that.policyId) &&
                Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyId, organizationId);
    }
}
