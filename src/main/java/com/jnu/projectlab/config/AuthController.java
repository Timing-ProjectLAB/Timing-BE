package com.jnu.projectlab.config;

import com.jnu.projectlab.user.UserDto;
import com.jnu.projectlab.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(Authentication authentication) {
        // 인증 정보가 이미 SecurityContextHolder에 저장됨
        if (authentication != null && authentication.isAuthenticated()) {
            // 현재 인증된 사용자 정보 반환
            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인이 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 회원가입 엔드포인트 추가
    @PostMapping("/signup") // 명세에 맞게 엔드포인트 수정
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        Long userId = userService.save(userDto); // 회원가입 메서드 호출

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었으며 로그인되었습니다.");

        return ResponseEntity.ok(response); // Map 객체 반환
    }
}