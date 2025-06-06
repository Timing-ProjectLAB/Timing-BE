package com.jnu.projectlab.common.entity.policyKeyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyKeywordId implements Serializable {
    private String policyId;  // 정책번호
    private String keyword;   // 키워드

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PolicyKeywordId that = (PolicyKeywordId) obj;
        return Objects.equals(policyId, that.policyId) &&
                Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyId, keyword);
    }
}