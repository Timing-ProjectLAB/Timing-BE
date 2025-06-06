package com.jnu.projectlab.common.entity;

import com.jnu.projectlab.common.entity.policyZipcode.PolicyZipcodeId;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "policy_zipcode")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PolicyZipcodeId.class)
public class PolicyZipcode {
    @Id
    @JsonProperty("plcyNo")
    @Column(name = "policy_id")
    private String policyId;  // 정책번호

    @Id
    @JsonProperty("zipCd")
    @Column(name = "zipcode")
    private String zipcode;  // 우편번호

    @JsonProperty("zipCdNm")
    @Column(name = "zipcode_name", length = 1000) // 길이초과로 변경
    private String zipcodeName;  // 우편번호명
}