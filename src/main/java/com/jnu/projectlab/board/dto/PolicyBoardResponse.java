package com.jnu.projectlab.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyBoardResponse {
    private String userId;
    private int totalCount;
    private List<PolicyBoardItem> policies;
    private String filterCategory; // 필터 카테고리 (nullable)
}