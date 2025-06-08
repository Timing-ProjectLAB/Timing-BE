package com.jnu.projectlab.board.controller;

import com.jnu.projectlab.board.dto.PolicyBoardResponse;
import com.jnu.projectlab.board.service.PolicyBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/policy")
public class PolicyBoardController {

    private final PolicyBoardService policyBoardService;

    /**
     * 정책 게시판 조회 API
     * API 명세서: GET /policy/board/{userId}
     *
     * @param userId 요청한 사용자 ID (Path Variable)
     * @return ResponseEntity<PolicyBoardResponse> 전체 정책 게시판 데이터
     */
    @GetMapping("/board/{userId}")
    public ResponseEntity<PolicyBoardResponse> getPolicyBoard(@PathVariable String userId) {

        // 1. Service를 통해 전체 정책 데이터 조회
        PolicyBoardResponse response = policyBoardService.getAllPoliciesForBoard(userId);

        // 2. 200 OK와 함께 응답 데이터 반환
        return ResponseEntity.ok(response);
    }
}