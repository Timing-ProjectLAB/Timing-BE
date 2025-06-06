package com.jnu.projectlab.common.entity;

import com.jnu.projectlab.common.entity.policyKeyword.PolicyKeywordId;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "policy_keyword")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PolicyKeywordId.class)
public class PolicyKeyword {
    @Id
    @JsonProperty("plcyNo")
    @Column(name = "policy_id")
    private String policyId;  // 정책번호

    @Id
    @JsonProperty("keyword")
    @Column(name = "keyword")
    private String keyword;  // 키워드
}