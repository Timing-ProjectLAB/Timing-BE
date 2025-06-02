package com.jnu.projectlab.policy.entity.policyCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

/**
 * 정책-카테고리 복합키 클래스
 * JPA에서 복합키 사용을 위한 별도 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyCategoryId implements Serializable {

    private String policyId; // 정책번호
    private Integer categoryId; // 카테고리번호

    /**
     * equals 메서드 - 복합키 동등성 비교를 위해 필수
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PolicyCategoryId that = (PolicyCategoryId) obj;
        return Objects.equals(policyId, that.policyId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    /**
     * hashCode 메서드 - 복합키 해시값 생성을 위해 필수
     */
    @Override
    public int hashCode() {
        return Objects.hash(policyId, categoryId);
    }
}
