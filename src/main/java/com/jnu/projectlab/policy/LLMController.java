package com.jnu.projectlab.policy;

import com.jnu.projectlab.policy.dto.LLMAnswerResponse;
import com.jnu.projectlab.policy.dto.LLMQuestionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.jnu.projectlab.user.User;

@RestController
@RequestMapping("/llm")
public class LLMController {
    
    private final LLMService llmService;

    @Autowired
    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }

    @PreAuthorize("isAuthenticated()") // 세션 검사
    @PostMapping("/answers")
    public ResponseEntity<LLMAnswerResponse> getAnswer(
        @RequestBody LLMQuestionRequest request,
        @AuthenticationPrincipal User user
    ) {
        try {
            // 세션의 userId와 요청의 userId 비교
            if (!user.getUserId().equals(request.getUserId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LLMAnswerResponse("세션의 사용자와 일치하지 않습니다."));
            }
            
            // 질문 내용 검증
            if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LLMAnswerResponse("질문 내용이 비어있습니다."));
            }
            
            LLMAnswerResponse response = llmService.processQuestion(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new LLMAnswerResponse("질문 처리 중 오류가 발생했습니다."));
        }
    }
}
