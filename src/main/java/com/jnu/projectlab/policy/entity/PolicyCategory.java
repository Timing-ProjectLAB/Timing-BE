package com.jnu.projectlab.policy.entity;

import com.jnu.projectlab.category.entity.Category;
import com.jnu.projectlab.policy.entity.policyCategory.PolicyCategoryId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정책-카테고리 연결 엔티티
 * 정책과 카테고리 간의 다대다 관계를 나타내는 테이블
 * 복합키 (policy_id + category_id) 사용
 */
@Entity
@Table(name = "policy_category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PolicyCategoryId.class)
public class PolicyCategory {

    @Id
    @Column(name = "policy_id")
    private String policyId; // 정책번호

    @Id
    @Column(name = "category_id")
    private Integer categoryId; // 카테고리번호

    // Policy와의 관계
    @ManyToOne
    @JoinColumn(name = "policy_id", insertable = false, updatable = false)
    private Policy policy;

    // Category와의 관계
    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
}