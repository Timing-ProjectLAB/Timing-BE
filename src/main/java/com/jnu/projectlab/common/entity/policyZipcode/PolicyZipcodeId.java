package com.jnu.projectlab.common.entity.policyZipcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyZipcodeId implements Serializable {
    private String policyId;  // 정책번호
    private String zipcode;   // 우편번호

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PolicyZipcodeId that = (PolicyZipcodeId) obj;
        return Objects.equals(policyId, that.policyId) &&
                Objects.equals(zipcode, that.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyId, zipcode);
    }
}