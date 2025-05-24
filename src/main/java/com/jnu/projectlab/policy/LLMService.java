package com.jnu.projectlab.policy;

import com.jnu.projectlab.policy.dto.LLMAnswerResponse;
import com.jnu.projectlab.policy.dto.LLMQuestionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LLMService {
    private final RestTemplate restTemplate;
    private final String LLM_SERVER_URL = "http://llm.timing.n-e.kr:8000/llm/answers";

    @Autowired
    public LLMService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LLMAnswerResponse processQuestion(LLMQuestionRequest request) {
        try {

            // 1. 요청 본문 구성
            // LLM 서버가 요구하는 형식에 맞춰 JSON 데이터 구성
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("question", request.getQuestion());
            requestBody.put("user_id", request.getUserId());  // user_id 필드 추가

            // 2. HTTP 헤더 설정
            // Content-Type을 application/json으로 설정하여 JSON 형식임을 명시
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. HTTP 요청 엔티티 생성
            // 헤더와 본문을 함께 포함하는 HTTP 엔티티 생성
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // 4. LLM 서버로 요청 전송
            // POST 요청을 보내고 응답을 받음
            ResponseEntity<String> response = restTemplate.postForEntity(
                LLM_SERVER_URL,
                entity,  // HttpEntity 사용
                String.class
            );

            // 5. 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                return new LLMAnswerResponse(response.getBody());
            } else {
                // 실패한 경우 에러 메시지와 함께 상태 코드 반환
                return new LLMAnswerResponse("LLM 서버 응답 오류: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            // 6. 예외 처리
            // Time out or 422 Error etc..
            return new LLMAnswerResponse("LLM 서버 통신 오류: " + e.getMessage());
        }
    }
}