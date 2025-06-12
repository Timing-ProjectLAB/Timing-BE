package com.jnu.projectlab.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메인페이지 정책 아이템 DTO (인기정책, 맞춤정책 공통 사용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyMainItem {
    private String policy_id; // Add Policy_id
    private String policyName;           // 정책명
    private String supportSummary;       // 지원내용 요약
    private String applicationDeadline;  // 신청 마감일
    private Integer inquiryCount;        // 조회수 (항상 포함)
}