package com.jnu.projectlab.policy.controller;

import com.jnu.projectlab.policy.dto.PolicyDetailResponse;
import com.jnu.projectlab.policy.service.PolicyService;
import com.jnu.projectlab.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/policy")
public class PolicyController {

    private final PolicyService policyService;

    @Autowired
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{policy_id}")
    public ResponseEntity<?> getPolicyDetail(
            @PathVariable String policyId,
            @AuthenticationPrincipal User user
    ) {
        try {
            PolicyDetailResponse response = policyService.getPolicyDetail(policyId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("정책 상세 정보 조회 중 오류가 발생했습니다.");
        }
    }
}