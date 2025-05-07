package com.jnu.projectlab.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(Authentication authentication) {
        // 인증 정보가 이미 SecurityContextHolder에 저장됨
        if (authentication != null && authentication.isAuthenticated()) {
            // 현재 인증된 사용자 정보 반환
            Map<String, Object> response = new HashMap<>();
            response.put("userId", authentication.getName());
            response.put("authenticated", true);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}