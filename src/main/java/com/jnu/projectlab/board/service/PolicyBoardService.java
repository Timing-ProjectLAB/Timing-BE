package com.jnu.projectlab.board.service;

import com.jnu.projectlab.board.dto.PolicyBoardResponse;
import com.jnu.projectlab.board.dto.PolicyBoardItem;
import com.jnu.projectlab.policy.entity.Policy;
import com.jnu.projectlab.policy.repository.PolicyRepository;
import com.jnu.projectlab.common.entity.PolicyKeyword;
import com.jnu.projectlab.common.repository.PolicyKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PolicyBoardService {

    private final PolicyRepository policyRepository;
    private final PolicyKeywordRepository policyKeywordRepository;

    /**
     * 사용자별 전체 정책 게시판 데이터 조회
     * API 명세서의 GET /policy/board/{userId} 구현
     *
     * @param userId 요청한 사용자 ID
     * @return PolicyBoardResponse 전체 정책 게시판 응답 데이터
     */
    public PolicyBoardResponse getAllPoliciesForBoard(String userId) {
        // 1. 전체 정책 개수 조회 (API 명세서의 totalCount)
        long totalCount = policyRepository.count();

        // 2. 전체 정책 데이터 조회
        List<Policy> allPolicies = policyRepository.findAll();

        // 3. 각 정책을 게시판 아이템으로 변환
        List<PolicyBoardItem> boardItems = allPolicies.stream()
                .map(this::convertToBoardItem)  // 개별 정책 → 게시판 아이템 변환
                .collect(Collectors.toList());

        // 4. 최종 응답 데이터 구성
        return PolicyBoardResponse.builder()
                .userId(userId)
                .totalCount((int) totalCount)
                .policies(boardItems)
                .build();
    }

    /**
     * Policy 엔티티를 PolicyBoardItem으로 변환
     * API 명세서의 policies 배열 내 객체 구조에 맞게 가공
     *
     * @param policy Policy 엔티티
     * @return PolicyBoardItem 게시판 표시용 정책 아이템
     */
    private PolicyBoardItem convertToBoardItem(Policy policy) {
        return PolicyBoardItem.builder()
                .policyName(policy.getName())  // 정책명: Policy.name 그대로 사용
                .supportSummary(extractSupportSummary(policy.getSupportContent()))  // 보충내용: 첫 번째 항목만
                .applicationDeadline(extractApplicationDeadline(policy.getApplicationPeriod()))  // 마감일자: 종료일만 추출
                .keywords(getKeywords(policy.getPolicyId()))  // 키워드: PolicyKeyword 테이블에서 조회
                .build();
    }

    /**
     * Policy.supportContent에서 첫 번째 지원내용 항목만 추출
     * API 명세서: "□ 지원금액: 월 최대 20만 원" 형태로 반환
     * 
     * @param supportContent Policy 엔티티의 supportContent 필드
     * @return String 첫 번째 지원내용 항목 (구분자 포함)
     */
    private String extractSupportSummary(String supportContent) {
        if (supportContent == null || supportContent.trim().isEmpty()) {
            return "정보없음";
        }
        
        // 줄바꿈으로 분리해서 첫 번째 유효한 항목 찾기
        String[] lines = supportContent.split("\\n");
        
        for (String line : lines) {
            String trimmed = line.trim();
            // 빈 줄이 아니고, 의미있는 내용이면 첫 번째로 선택
            if (!trimmed.isEmpty() && trimmed.length() > 3) {
                return trimmed; // 구분자(□, -, • 등) 그대로 유지
            }
        }
        
        return "정보없음";
    }

    /**
     * Policy.applicationPeriod에서 마감일자만 추출하여 yyyy.MM.dd 형식으로 변환
     * API 명세서: "20250515 ~ 20250605" → "2025.06.05"
     * 
     * @param applicationPeriod Policy 엔티티의 applicationPeriod 필드
     * @return String 마감일자 (yyyy.MM.dd 형식)
     */
    private String extractApplicationDeadline(String applicationPeriod) {
        if (applicationPeriod == null || applicationPeriod.trim().isEmpty()) {
            return "정보없음";
        }
        
        try {
            // "20250515 ~ 20250605" 형태에서 종료일(뒷부분)만 추출
            String[] dates = applicationPeriod.split(" ~ ");
            if (dates.length == 2) {
                String endDate = dates[1].trim(); // "20250605"
                
                // yyyyMMdd → yyyy.MM.dd 변환
                if (endDate.length() == 8) {
                    String year = endDate.substring(0, 4);   // "2025"
                    String month = endDate.substring(4, 6);  // "06"
                    String day = endDate.substring(6, 8);    // "05"
                    
                    return year + "." + month + "." + day;   // "2025.06.05"
                }
            }
            
            // 상시 모집 등의 경우
            if (applicationPeriod.contains(" ")) {
                return "상시";
            }
            
        } catch (Exception e) {
            // 파싱 실패 시
            return "정보없음";
        }
        
        return "정보없음";
    }

    /**
     * PolicyKeyword 테이블에서 해당 정책의 모든 키워드 조회
     * API 명세서: ["주거", "지원금", "청년"] 형태로 반환
     * 
     * @param policyId Policy 엔티티의 policyId
     * @return List<String> 키워드 목록 (중복 제거됨)
     */
    private List<String> getKeywords(String policyId) {
        if (policyId == null || policyId.trim().isEmpty()) {
            return List.of(); // 빈 배열 반환
        }
        
        try {
            // PolicyKeyword 테이블에서 해당 정책의 키워드들 조회
            List<PolicyKeyword> policyKeywords = policyKeywordRepository.findByPolicyId(policyId);
            
            return policyKeywords.stream()
                .map(PolicyKeyword::getKeyword)    // 키워드 문자열 추출
                .filter(keyword -> keyword != null && !keyword.trim().isEmpty())  // null/빈값 제거
                .map(String::trim)                 // 앞뒤 공백 제거
                .distinct()                        // 중복 제거
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            // 조회 실패 시 빈 배열 반환
            return List.of();
        }
    }
}