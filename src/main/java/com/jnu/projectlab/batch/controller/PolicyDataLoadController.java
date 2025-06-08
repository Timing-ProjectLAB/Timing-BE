package com.jnu.projectlab.batch.controller;

import com.jnu.projectlab.batch.service.PolicyDataLoadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyDataLoadController {

    private final PolicyDataLoadService policyDataLoadService;

    @PostMapping("/load")
    public ResponseEntity<String> loadPolicyData() {
        try {
            policyDataLoadService.loadPolicyData();
            return ResponseEntity.ok("정책 데이터 적재 완료!");
        } catch (Exception e) {
            log.error("정책 데이터 적재 중 에러 발생", e);
            return ResponseEntity.internalServerError().body("에러: " + e.getMessage());
        }
    }
}
