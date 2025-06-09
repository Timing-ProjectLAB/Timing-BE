package com.jnu.projectlab.board.controller;

import com.jnu.projectlab.board.dto.PolicyBoardResponse;
import com.jnu.projectlab.board.dto.PolicyMainResponse;
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

        try {
            // 1. Service를 통해 전체 정책 데이터 조회
            PolicyBoardResponse response = policyBoardService.getAllPoliciesForBoard(userId);

            // 2. 200 OK와 함께 응답 데이터 반환
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 🆕 3. 존재하지 않는 사용자인 경우 404 Not Found
            if (e.getMessage().contains("존재하지 않는 사용자")) {
                return ResponseEntity.notFound().build();
            }
            // 기타 IllegalArgumentException은 400으로 처리
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            // 4. 시스템 오류는 500 처리
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 카테고리별 정책 게시판 조회 API
     * 
     * 🆕 주요 특징:
     * - 존재하지 않는 사용자: 404 Not Found
     * - 존재하지 않는 카테고리: 200 OK (빈 결과)
     * - 사용자 친화적 에러 처리
     * 
     * @param userId 사용자 ID (Path Variable)
     * @param category 카테고리명 (Query Parameter)
     * @return 200: 성공, 404: 사용자 없음, 500: 서버 오류
     */
    @GetMapping("/board/filter/{userId}")
    public ResponseEntity<PolicyBoardResponse> getFilteredPolicyBoard(
            @PathVariable String userId, 
            @RequestParam String category) {

        try {
            // 🆕 Service 호출 - userId 검증 포함
            PolicyBoardResponse response = policyBoardService.getPolicyBoardByCategory(userId, category);

            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 🆕 사용자 존재 여부에 따른 세분화된 에러 처리
            if (e.getMessage().contains("존재하지 않는 사용자")) {
                return ResponseEntity.notFound().build();  // 404
            }
            return ResponseEntity.badRequest().build();  // 400
            
        } catch (Exception e) {
            // 🆕 시스템 오류는 500으로 처리
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 메인페이지 정책 조회 API
     * 인기정책 3개 + 맞춤정책 3개
     */
    @GetMapping("/board/main")
    public ResponseEntity<PolicyMainResponse> getMainPage() {

        PolicyMainResponse response = policyBoardService.getMainPagePolicies();
        return ResponseEntity.ok(response);
    }
}