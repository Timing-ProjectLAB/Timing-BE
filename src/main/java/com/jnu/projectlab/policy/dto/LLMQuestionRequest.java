package com.jnu.projectlab.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LLMQuestionRequest {
    @JsonProperty("user_id")
    private String userId;    //ID
    private String question;  // Question
}
