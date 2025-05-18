package com.jnu.projectlab.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LLMQuestionRequest {
    private String userId;    //ID
    private String question;  // Question
}
