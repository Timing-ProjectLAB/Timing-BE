package com.jnu.projectlab.favoritepolicy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 추가/삭제 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavoritePolicyResponse {
    
    /**
     * 성공 여부
     */
    private boolean success;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 현재 즐겨찾기 상태 (true: 즐겨찾기됨, false: 즐겨찾기 해제됨)
     */
    private boolean isFavorited;
    
    /**
     * 사용자 ID
     */
    private String userId;
    
    /**
     * 정책 ID
     */
    private String policyId;
}

