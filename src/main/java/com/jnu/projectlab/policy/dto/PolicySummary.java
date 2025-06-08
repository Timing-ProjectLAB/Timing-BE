package com.jnu.projectlab.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicySummary {
    private String operatingAgency;    // 운영기관
    private String applicationPeriod;  // 신청기간
    private String applicationUrl;     // 신청URL
}