package com.jnu.projectlab.config;

/**
 * 사용자 로그인 요청을 처리하기 위한 DTO 클래스
 * 클라이언트로부터 로그인 시 필요한 사용자 ID와 비밀번호를 전달받습니다.
 */
public class LoginRequest {
    private String userId;
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 