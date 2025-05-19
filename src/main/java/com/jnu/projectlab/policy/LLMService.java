package com.jnu.projectlab.policy;

import com.jnu.projectlab.policy.dto.LLMAnswerResponse;
import com.jnu.projectlab.policy.dto.LLMQuestionRequest;
import org.springframework.stereotype.Service;

@Service
public class LLMService {
    
    public LLMAnswerResponse processQuestion(LLMQuestionRequest request) {
        // 여기에 실제 LLM 처리 로직이 들어갈 예정입니다.
        // 현재는 임시 응답을 반환
        return new LLMAnswerResponse("질문이 접수되었습니다: " + request.getQuestion());
    }
}
