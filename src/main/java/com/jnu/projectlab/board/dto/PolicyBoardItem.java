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
public class PolicyBoardItem {
    private String policy_id; // Add Policy_id
    private String policyName;
    private String supportSummary;
    private String applicationDeadline;
    private List<String> keywords;
}