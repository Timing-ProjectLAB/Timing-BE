package com.jnu.projectlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 클라이언트 설정을 위한 팩토리 생성
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 연결 타임아웃 설정 (서버와의 연결 시도 시간)
        // 10초 동안 연결을 시도하고, 실패하면 예외 발생
        factory.setConnectTimeout(10000);    // 10초

        // 읽기 타임아웃 설정 (서버로부터 응답을 기다리는 시간)
        // 30초 동안 응답을 기다리고, 응답이 없으면 예외 발생
        factory.setReadTimeout(30000);       // 30초

        // 설정된 팩토리를 RestTemplate에 적용
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }
}