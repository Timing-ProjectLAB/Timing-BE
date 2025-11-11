package com.jnu.projectlab.favoritepolicy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 추가 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavoritePolicyRequest {
    
    /**
     * 정책 ID
     */
    private String policyId;
}

