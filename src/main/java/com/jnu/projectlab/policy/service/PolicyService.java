package com.jnu.projectlab.policy.service;

import com.jnu.projectlab.common.entity.PolicyKeyword;
import com.jnu.projectlab.policy.dto.PolicyDetailResponse;
import com.jnu.projectlab.policy.dto.PolicySummary;
import com.jnu.projectlab.policy.entity.Policy;
import com.jnu.projectlab.policy.entity.PolicyCondition;
import com.jnu.projectlab.policy.entity.PolicyMeta;
import com.jnu.projectlab.policy.repository.PolicyRepository;
import com.jnu.projectlab.policy.repository.PolicyConditionRepository;
import com.jnu.projectlab.policy.repository.PolicyMetaRepository;
import com.jnu.projectlab.policyview.service.UserPolicyViewService;
import com.jnu.projectlab.organization.entity.Organization;
import com.jnu.projectlab.organization.repository.OrganizationRepository;
import com.jnu.projectlab.common.repository.PolicyKeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final OrganizationRepository organizationRepository;
    private final PolicyMetaRepository policyMetaRepository;
    private final PolicyKeywordRepository policyKeywordRepository;
    private final PolicyConditionRepository policyConditionRepository;
    private final UserPolicyViewService userPolicyViewService;

    @Autowired
    public PolicyService(
            PolicyRepository policyRepository,
            OrganizationRepository organizationRepository,
            PolicyMetaRepository policyMetaRepository,
            PolicyKeywordRepository policyKeywordRepository,
            PolicyConditionRepository policyConditionRepository,
            UserPolicyViewService userPolicyViewService) {
        this.policyRepository = policyRepository;
        this.organizationRepository = organizationRepository;
        this.policyMetaRepository = policyMetaRepository;
        this.policyKeywordRepository = policyKeywordRepository;
        this.policyConditionRepository = policyConditionRepository;
        this.userPolicyViewService = userPolicyViewService;
    }

    @Transactional
    public PolicyDetailResponse getPolicyDetail(String policyId, String userId) {
        // 1. Policy 조회
        Policy policy = policyRepository.findByPolicyId(policyId);
        if (policy == null) {
            throw new RuntimeException("정책을 찾을 수 없습니다.");
        }

        // 2. 조회수 증가 처리 (24시간 내 중복 조회 방지)
        userPolicyViewService.incrementViewCountIfNeeded(policyId, userId);

        // 3. 조회수 증가 후 최신 Policy 정보 다시 조회 (조회수 반영)
        policy = policyRepository.findByPolicyId(policyId);

        // ✅ 4. Organization 조회 (null 허용)
        Organization organization = null;
        if (policy.getOperInstCd() != null) {
            organization = organizationRepository.findByOrganizationId(policy.getOperInstCd());
        }

        // ✅ 5. PolicyMeta 조회 (null 허용)
        PolicyMeta policyMeta = policyMetaRepository.findByPolicyId(policyId);

        // ✅ 6. PolicyCondition 조회 (null 허용)
        PolicyCondition policyCondition = policyConditionRepository.findByPolicyId(policyId);

        // 7. PolicyKeyword 조회
        List<PolicyKeyword> policyKeywords = policyKeywordRepository.findByPolicyId(policyId);
        String keywords = policyKeywords.stream()
                .map(PolicyKeyword::getKeyword)
                .collect(Collectors.joining(","));

        // 8. 날짜 형식 변환
        String formattedDate = formatApplicationPeriod(policy.getApplicationPeriod());

        // ✅ 9. PolicySummary 생성 (null 안전)
        PolicySummary policySummary = PolicySummary.builder()
                .operatingAgency(organization != null ? organization.getName() : "정보 없음")
                .applicationPeriod(formattedDate)
                .applicationUrl(policy.getApplicationUrl())
                .build();

        // ✅ 10. 지원대상 파싱 (null 안전)
        List<String> targetAudience = parseTargetAudience(
            policyCondition != null ? policyCondition.getAdditionalCondition() : null
        );

        // ✅ 11. 지원내용 파싱 (null 안전)
        List<String> supportContent = parseSupportContent(policy.getSupportContent());

        // 12. PolicyDetailResponse 생성 및 반환 (조회수 포함)
        return PolicyDetailResponse.builder()
                .policy_id(policy.getPolicyId())
                .plcyKywdNm(keywords.isEmpty() ? null : keywords)
                .policyName(policy.getName())
                .policyDescription(policy.getDescription())
                .policySummary(policySummary)
                .targetAudience(targetAudience.isEmpty() ? List.of() : targetAudience)
                .supportContent(supportContent.isEmpty() ? List.of() : supportContent)
                .inquiryCount(policy.getInquiryCount())  // 조회수 포함
                .build();
    }

    private String formatApplicationPeriod(String aplyYmd) {
        if (aplyYmd == null || aplyYmd.trim().isEmpty()) {
            return "상시";
        }

        String[] dates = aplyYmd.split(" ~ ");
        if (dates.length != 2) {
            return aplyYmd;
        }

        // 첫 번째 날짜 변환
        LocalDate startDate = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        // 두 번째 날짜 변환
        LocalDate endDate = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("MM.dd"));

        return formattedStartDate + " ~ " + formattedEndDate;
    }

    private List<String> parseTargetAudience(String additionalCondition) {
        if (additionalCondition == null || additionalCondition.trim().isEmpty()) {
            return List.of();
        }

        // 방안 2: 간단한 줄바꿈 + 구분자 제거
        return Arrays.stream(additionalCondition.split("\\n"))  // 1. 줄바꿈으로 분리
                .map(line -> line.trim())                        // 2. 양쪽 공백 제거
                .filter(line -> !line.isEmpty())                 // 3. 빈 줄 제외
                .map(this::removeSpecialPrefix)
                .filter(line -> !line.isEmpty() && line.length() > 3) // 5. 너무 짧은 텍스트 제외
                .collect(Collectors.toList());
    }

    private List<String> parseSupportContent(String supportContent) {
        if (supportContent == null || supportContent.trim().isEmpty()) {
            return List.of();
        }

        // 방안 2: 간단한 줄바꿈 + 구분자 제거
        return Arrays.stream(supportContent.split("\\n"))       // 1. 줄바꿈으로 분리
                .map(line -> line.trim())                        // 2. 양쪽 공백 제거
                .filter(line -> !line.isEmpty())                 // 3. 빈 줄 제외
                .map(this::removeSpecialPrefix) // ← 여기서 특수문자 제거
                .map(line -> line.replaceAll("^지원[가-힣]*\\s*:\\s*", ""))
                .filter(line -> !line.isEmpty() && line.length() > 3)   // 5. 너무 짧은 텍스트 제외
                .collect(Collectors.toList());
    }

    public String removeSpecialPrefix(String text) {
        if (text == null) return null;
        // 문장 맨 앞의 특수문자, 숫자, 기호, 공백 등 제거
        return text.replaceAll("^[\\s\\-•※▴*①②③④⑤⑥⑦⑧⑨⑩ㅇ□○]+", "");
    }
}