package com.jnu.projectlab.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyDetailResponse {
    private String policyId;           // 정책번호
    private String plcyKywdNm;         // 키워드
    private String policyName;         // 정책명
    private String policyDescription;  // 정책설명
    private PolicySummary policySummary;  // 정책요약
    private List<String> targetAudience;  // 지원대상
    private List<String> supportContent;  // 지원내용
}