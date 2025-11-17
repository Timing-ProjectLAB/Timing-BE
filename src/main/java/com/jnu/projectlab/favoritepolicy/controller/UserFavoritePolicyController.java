package com.jnu.projectlab.favoritepolicy.controller;

import com.jnu.projectlab.favoritepolicy.dto.UserFavoritePolicyListResponse;
import com.jnu.projectlab.favoritepolicy.dto.UserFavoritePolicyRequest;
import com.jnu.projectlab.favoritepolicy.dto.UserFavoritePolicyResponse;
import com.jnu.projectlab.favoritepolicy.service.UserFavoritePolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 즐겨찾기 정책 Controller
 * 
 * API 엔드포인트:
 * - POST   /api/{userId}/favorites        : 즐겨찾기 추가
 * - DELETE /api/{userId}/favorites/{policyId} : 즐겨찾기 삭제
 * - GET    /api/{userId}/favorites        : 즐겨찾기 목록 조회
 * - GET    /api/{userId}/favorites/check/{policyId} : 즐겨찾기 여부 확인
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{userId}/favorites")
public class UserFavoritePolicyController {

    private final UserFavoritePolicyService favoritePolicyService;

    /**
     * 즐겨찾기 추가 API
     * POST /api/{userId}/favorites
     * 
     * @param userId 사용자 ID
     * @param request 정책 ID를 포함한 요청 본문
     * @return 즐겨찾기 추가 결과
     */
    @PostMapping
    public ResponseEntity<UserFavoritePolicyResponse> addFavorite(
            @PathVariable String userId,
            @RequestBody UserFavoritePolicyRequest request) {
        
        try {
            boolean added = favoritePolicyService.addFavorite(userId, request.getPolicyId());
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(true)
                    .message(added ? "즐겨찾기에 추가되었습니다." : "이미 즐겨찾기한 정책입니다.")
                    .isFavorited(true)
                    .userId(userId)
                    .policyId(request.getPolicyId())
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("즐겨찾기 추가 실패: {}", e.getMessage());
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .isFavorited(false)
                    .userId(userId)
                    .policyId(request.getPolicyId())
                    .build();
            
            // 사용자 또는 정책이 존재하지 않는 경우 404
            if (e.getMessage().contains("존재하지 않는")) {
                return ResponseEntity.status(404).body(response);
            }
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("즐겨찾기 추가 중 시스템 오류 발생", e);
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(false)
                    .message("즐겨찾기 추가 중 오류가 발생했습니다.")
                    .isFavorited(false)
                    .userId(userId)
                    .policyId(request.getPolicyId())
                    .build();
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 즐겨찾기 삭제 API
     * DELETE /api/{userId}/favorites/{policyId}
     * 
     * @param userId 사용자 ID
     * @param policyId 정책 ID
     * @return 즐겨찾기 삭제 결과
     */
    @DeleteMapping("/{policyId}")
    public ResponseEntity<UserFavoritePolicyResponse> removeFavorite(
            @PathVariable String userId,
            @PathVariable String policyId) {
        
        try {
            boolean removed = favoritePolicyService.removeFavorite(userId, policyId);
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(true)
                    .message(removed ? "즐겨찾기가 삭제되었습니다." : "즐겨찾기하지 않은 정책입니다.")
                    .isFavorited(false)
                    .userId(userId)
                    .policyId(policyId)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("즐겨찾기 삭제 실패: {}", e.getMessage());
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .isFavorited(false)
                    .userId(userId)
                    .policyId(policyId)
                    .build();
            
            // 사용자가 존재하지 않는 경우 404
            if (e.getMessage().contains("존재하지 않는 사용자")) {
                return ResponseEntity.status(404).body(response);
            }
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("즐겨찾기 삭제 중 시스템 오류 발생", e);
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(false)
                    .message("즐겨찾기 삭제 중 오류가 발생했습니다.")
                    .isFavorited(false)
                    .userId(userId)
                    .policyId(policyId)
                    .build();
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 즐겨찾기 목록 조회 API
     * GET /api/{userId}/favorites
     * 
     * @param userId 사용자 ID
     * @return 사용자의 즐겨찾기 정책 ID 목록
     */
    @GetMapping
    public ResponseEntity<UserFavoritePolicyListResponse> getFavorites(
            @PathVariable String userId) {
        
        try {
            List<String> policyIds = favoritePolicyService.getFavoritePolicyIds(userId);
            long totalCount = favoritePolicyService.getUserFavoriteCount(userId);
            
            UserFavoritePolicyListResponse response = UserFavoritePolicyListResponse.builder()
                    .userId(userId)
                    .totalCount(totalCount)
                    .policyIds(policyIds)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("즐겨찾기 목록 조회 실패: {}", e.getMessage());
            
            // 사용자가 존재하지 않는 경우 404
            if (e.getMessage().contains("존재하지 않는 사용자")) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("즐겨찾기 목록 조회 중 시스템 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 즐겨찾기 여부 확인 API
     * GET /api/{userId}/favorites/check/{policyId}
     * 
     * @param userId 사용자 ID
     * @param policyId 정책 ID
     * @return 즐겨찾기 여부 (isFavorited)
     */
    @GetMapping("/check/{policyId}")
    public ResponseEntity<UserFavoritePolicyResponse> checkFavorite(
            @PathVariable String userId,
            @PathVariable String policyId) {
        
        try {
            boolean isFavorited = favoritePolicyService.isFavorited(userId, policyId);
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(true)
                    .message(isFavorited ? "즐겨찾기한 정책입니다." : "즐겨찾기하지 않은 정책입니다.")
                    .isFavorited(isFavorited)
                    .userId(userId)
                    .policyId(policyId)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("즐겨찾기 여부 확인 중 오류 발생", e);
            
            UserFavoritePolicyResponse response = UserFavoritePolicyResponse.builder()
                    .success(false)
                    .message("즐겨찾기 여부 확인 중 오류가 발생했습니다.")
                    .isFavorited(false)
                    .userId(userId)
                    .policyId(policyId)
                    .build();
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

