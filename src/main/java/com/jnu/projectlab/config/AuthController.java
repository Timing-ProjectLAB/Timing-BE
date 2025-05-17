package com.jnu.projectlab.config;

import com.jnu.projectlab.user.UserDto;
import com.jnu.projectlab.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 사용자 로그인을 처리하는 엔드포인트
     * 
     * @param loginRequest 로그인 요청 정보 (사용자 ID와 비밀번호 포함)
     * @return ResponseEntity<?> 로그인 성공 시 성공 메시지, 실패 시 401 Unauthorized
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 인증 매니저를 통해 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUserId(),
                    loginRequest.getPassword()
                )
            );

            // 인증 성공 시 SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 성공 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인이 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // 인증 실패 시 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        Long userId = userService.save(userDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었으며 로그인되었습니다.");
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }
}