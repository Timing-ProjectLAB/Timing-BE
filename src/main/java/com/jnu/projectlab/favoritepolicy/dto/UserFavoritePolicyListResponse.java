package com.jnu.projectlab.favoritepolicy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사용자 즐겨찾기 목록 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavoritePolicyListResponse {
    
    /**
     * 사용자 ID
     */
    private String userId;
    
    /**
     * 총 즐겨찾기 개수
     */
    private long totalCount;
    
    /**
     * 즐겨찾기한 정책 ID 목록
     */
    private List<String> policyIds;
}

