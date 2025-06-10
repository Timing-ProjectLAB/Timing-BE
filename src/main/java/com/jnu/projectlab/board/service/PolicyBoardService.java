package com.jnu.projectlab.board.service;

import com.jnu.projectlab.board.dto.PolicyBoardResponse;
import com.jnu.projectlab.board.dto.PolicyBoardItem;
import com.jnu.projectlab.board.dto.PolicyMainItem;
import com.jnu.projectlab.board.dto.PolicyMainResponse;
import com.jnu.projectlab.policy.entity.Policy;
import com.jnu.projectlab.policy.repository.PolicyRepository;
import com.jnu.projectlab.common.entity.PolicyKeyword;
import com.jnu.projectlab.common.repository.PolicyKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jnu.projectlab.policy.repository.PolicyCategoryRepository;
import com.jnu.projectlab.category.repository.CategoryGroupRepository;
import com.jnu.projectlab.category.entity.CategoryGroup;
import lombok.extern.slf4j.Slf4j;
import com.jnu.projectlab.user.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PolicyBoardService {

    private final PolicyRepository policyRepository;
    private final PolicyKeywordRepository policyKeywordRepository;
    private final PolicyCategoryRepository policyCategoryRepository;
    private final CategoryGroupRepository categoryGroupRepository;
    private final UserRepository userRepository;

    /**
     * 사용자별 전체 정책 게시판 데이터 조회
     * API 명세서의 GET /policy/board/{userId} 구현
     *
     * @param userId 요청한 사용자 ID
     * @return PolicyBoardResponse 전체 정책 게시판 응답 데이터
     */
    public PolicyBoardResponse getAllPoliciesForBoard(String userId) {
        // 🆕 1. 사용자 존재 여부 확인 (404 처리)
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자: " + userId);
        }
        
        // 2. 전체 정책 개수 조회 (API 명세서의 totalCount)
        long totalCount = policyRepository.count();

        // 3. 전체 정책 데이터 조회 + 마감일 필터링
        List<Policy> allPolicies = policyRepository.findAll().stream()
                .filter(this::isValidPolicy)
                .collect(Collectors.toList());

        // 4. 각 정책을 게시판 아이템으로 변환
        List<PolicyBoardItem> boardItems = allPolicies.stream()
                .map(this::convertToBoardItem)  // 개별 정책 → 게시판 아이템 변환
                .collect(Collectors.toList());

        // 5. 최종 응답 데이터 구성
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
                .supportSummary(
                    removeSpecialPrefix(extractSupportSummary(policy.getSupportContent()))
                )
                .applicationDeadline(extractApplicationDeadline(policy.getApplicationPeriod()))  // 마감일자: 종료일만 추출
                .keywords(getKeywords(policy.getPolicyId()))  // 키워드: PolicyKeyword 테이블에서 조회
                .build();
    }

    /**
     * 특수문자 제거 함수 추가
     *
     * @param text 입력 텍스트
     * @return 특수문자가 제거된 텍스트
     */
    private String removeSpecialPrefix(String text) {
        if (text == null) return null;
        return text.replaceAll("^[^가-힣a-zA-Z0-9\\(\\)\\[\\]\\{\\}]+", "");
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
            return "상시"; // 필드가 비어있을경우
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
            if (applicationPeriod.contains("상시")) {
                return "상시";
            }
            
        } catch (Exception e) {
            // 파싱 실패 시
            return "정보없음";
        }
        
        return "상시";
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

    /**
     * 메인페이지 정책 데이터 조회 (인기정책 3개 + 맞춤정책 3개)
     */
    public PolicyMainResponse getMainPagePolicies() {

        // 1. 인기정책 - Repository에서 Top 3 조회
        List<Policy> popularPolicies = policyRepository.findTop10ByOrderByInquiryCountDesc()
                .stream()
                .filter(this::isValidPolicy)
                .limit(3) // 10개중에 Top3 조회
                .collect(Collectors.toList());
        List<PolicyMainItem> popularItems = popularPolicies.stream()
                .map(this::convertToMainItem)  // 🔄 기존 패턴 재사용
                .collect(Collectors.toList());

        // 2. 맞춤정책 - 기존 findAll() 재사용 + 랜덤 선택
        List<Policy> allValidPolicies = policyRepository.findAll().stream()
                .filter(this::isValidPolicy)
                .collect(Collectors.toList());
        Collections.shuffle(allValidPolicies);  // 랜덤 섞기
        List<Policy> customPolicies = allValidPolicies.subList(0, Math.min(3, allValidPolicies.size()));
        List<PolicyMainItem> customItems = customPolicies.stream()
                .map(this::convertToMainItem)
                .collect(Collectors.toList());

        // 3. 응답 구성
        return PolicyMainResponse.builder()
                .popularPolicies(popularItems)
                .customPolicies(customItems)
                .build();
    }

    /**
     * Policy → PolicyMainItem 변환 (기존 convertToBoardItem 패턴 재사용)
     */
    private PolicyMainItem convertToMainItem(Policy policy) {
        return PolicyMainItem.builder()
                .policyName(policy.getName())
                .supportSummary(
                        removeSpecialPrefix(extractSupportSummary(policy.getSupportContent()))
                )
                .applicationDeadline(extractApplicationDeadline(policy.getApplicationPeriod()))  // 💡 기존 메서드 재사용!
                .inquiryCount(policy.getInquiryCount())
                .build();
    }

    /**
     * 카테고리별 필터링된 정책 게시판 조회
     * 
     * @param userId 요청한 사용자 ID 
     * @param categoryName 필터링할 카테고리명 (예: "복지문화", "취업지원")
     * @return PolicyBoardResponse 필터링된 정책 목록
     * @throws IllegalArgumentException 존재하지 않는 사용자인 경우
     */
    public PolicyBoardResponse getPolicyBoardByCategory(String userId, String categoryName) {
        log.info("카테고리 필터링 요청 - userId: {}, category: {}", userId, categoryName);
        
        try {
            // 1. 사용자 존재 여부 검증 (404 에러를 위한 체크)
            if (!userRepository.existsByUserId(userId)) {
                throw new IllegalArgumentException("존재하지 않는 사용자: " + userId);
            }
            
            // 2. 카테고리 그룹 조회 (없어도 에러 대신 빈 결과)
            CategoryGroup categoryGroup = categoryGroupRepository.findByName(categoryName)
                    .orElse(null);
            
            // 3. 존재하지 않는 카테고리 → 빈 결과 반환
            if (categoryGroup == null) {
                log.debug("존재하지 않는 카테고리: {}", categoryName);
                return PolicyBoardResponse.builder()
                        .userId(userId)
                        .filterCategory(categoryName)  // 필터링된 카테고리 정보
                        .totalCount(0)
                        .policies(List.of())
                        .build();
            }
            
            // 4. 카테고리에 속한 정책 ID 수집
            List<String> policyIds = policyCategoryRepository.findPolicyIdsByCategoryGroupId(categoryGroup.getId());
            
            // 5. 정책이 없는 경우 빈 결과
            if (policyIds.isEmpty()) {
                return PolicyBoardResponse.builder()
                        .userId(userId)
                        .filterCategory(categoryName)
                        .totalCount(0)
                        .policies(List.of())
                        .build();
            }
            
            // 6. 인기순(조회수) 정렬로 정책 조회
            List<Policy> policies = policyRepository.findByPolicyIdInOrderByInquiryCountDesc(policyIds)
                    .stream()
                    .filter(this::isValidPolicy)
                    .collect(Collectors.toList());
            
            // 7. DTO 변환 및 응답 구성
            List<PolicyBoardItem> boardItems = policies.stream()
                    .map(this::convertToBoardItem)
                    .collect(Collectors.toList());
            
            return PolicyBoardResponse.builder()
                    .userId(userId)
                    .filterCategory(categoryName)
                    .totalCount(boardItems.size())
                    .policies(boardItems)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            throw e;  // Controller에서 404 처리
        } catch (Exception e) {
            log.error("카테고리 필터링 중 오류 발생", e);
            throw new RuntimeException("정책 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 정책이 현재 신청 가능한지 확인 (마감일 체크 + 상시 포함)
     */
    private boolean isValidPolicy(Policy policy) {
        String deadline = extractApplicationDeadline(policy.getApplicationPeriod());

        // 상시 모집은 항상 포함
        if ("상시".equals(deadline)) {
            return true;
        }

        // 정보없음은 제외
        if ("정보없음".equals(deadline)) {
            return false;
        }

        try {
            // yyyy.MM.dd 형식을 LocalDate로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate deadlineDate = LocalDate.parse(deadline, formatter);
            LocalDate today = LocalDate.now();

            // 마감일이 오늘 이후면 포함 (오늘도 포함)
            return !deadlineDate.isBefore(today);
        } catch (Exception e) {
            // 파싱 실패시 제외
            return false;
        }
    }
}