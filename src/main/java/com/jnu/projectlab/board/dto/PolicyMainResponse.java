package com.jnu.projectlab.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 정책 메인페이지 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyMainResponse {

    private List<PolicyMainItem> popularPolicies;  // 인기정책 (프론트에서 조회수 표시)
    private List<PolicyMainItem> customPolicies;   // 맞춤정책 (프론트에서 조회수 숨김)
}