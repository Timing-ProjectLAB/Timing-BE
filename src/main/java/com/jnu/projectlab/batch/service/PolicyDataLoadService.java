package com.jnu.projectlab.batch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnu.projectlab.batch.dto.PolicyDataDto;
import com.jnu.projectlab.category.service.CategoryGroupService;
import com.jnu.projectlab.common.util.DateUtils;
import com.jnu.projectlab.common.entity.PolicyKeyword;
import com.jnu.projectlab.common.entity.PolicyZipcode;
import com.jnu.projectlab.common.repository.PolicyKeywordRepository;
import com.jnu.projectlab.common.repository.PolicyZipcodeRepository;
import com.jnu.projectlab.policy.entity.*;
import com.jnu.projectlab.policy.repository.*;
import com.jnu.projectlab.category.entity.Category;
import com.jnu.projectlab.category.entity.CategoryGroup;
import com.jnu.projectlab.organization.entity.Organization;
import com.jnu.projectlab.organization.entity.PolicyOrganization;
import com.jnu.projectlab.organization.repository.OrganizationRepository;
import com.jnu.projectlab.organization.repository.PolicyOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.StringUtils;
import com.jnu.projectlab.category.service.CategoryService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyDataLoadService {

    // ============== Repository 의존성 주입 ==============
    // 정책 관련
    private final PolicyRepository policyRepository;
    private final PolicyConditionRepository policyConditionRepository;
    private final PolicyMetaRepository policyMetaRepository;
    private final PolicyCategoryRepository policyCategoryRepository;
    
    // 연관 매핑 관련
    private final PolicyKeywordRepository policyKeywordRepository;
    private final PolicyZipcodeRepository policyZipcodeRepository;
    private final PolicyOrganizationRepository policyOrganizationRepository;
    
    // 마스터 데이터 관련
    private final OrganizationRepository organizationRepository;
    
    // 기타
    private final ObjectMapper objectMapper;

    // 의존성 주입 부분에 추가
    private final CategoryGroupService categoryGroupService;
    private final CategoryService categoryService;

    /**
     * 정책 데이터 전체 적재 메인 메서드
     * ERD의 모든 테이블에 데이터를 저장합니다.
     */
    @Transactional // 현재 효율성의 문제가 발생 무려 9시간.. 왜냐하면 혼자서 데이터를 다 적재하려고 하기 때문
    public void loadPolicyData() {
        try {
            // 1. JSON 파일 읽기
            log.info("정책 데이터 적재 시작");
            String json = Files.readString(Paths.get("data/policy_data.json"));
            List<PolicyDataDto> policyList = objectMapper.readValue(json, new TypeReference<List<PolicyDataDto>>() {});
            List<PolicyDataDto> sample = policyList.subList(0, Math.min(10, policyList.size()));
            policyList = sample; // 원본을 샘플로 교체
            log.info("JSON 파일 읽기 완료. 총 {}건의 정책 데이터 (테스트용 10건만 처리)", policyList.size()); // 우선적으로 1000개의 데이터만

            // 2. 각 정책별로 데이터 처리
            int processedCount = 0;
            for (PolicyDataDto dto : policyList) {
                try {
                    // 2-1. 마스터 데이터 저장 (카테고리, 기관)
                    CategoryGroup categoryGroup = saveCategoryGroupIfNotExists(dto);
                    Category category = saveCategoryIfNotExists(dto, categoryGroup);
                    List<Organization> organizations = saveOrganizationsIfNotExists(dto);

                    // 2-2. 정책 기본 정보 저장
                    Policy policy = savePolicy(dto);

                    // 2-3. 정책 조건/메타 정보 저장
                    savePolicyCondition(dto, policy);
                    savePolicyMeta(dto, policy);

                    // 2-4. 정책 연관 매핑 데이터 저장
                    savePolicyCategory(policy, category);
                    savePolicyKeywords(dto, policy);
                    savePolicyZipcodes(dto, policy);
                    savePolicyOrganizations(policy, organizations, dto);

                    processedCount++;
                    if (processedCount % 100 == 0) {
                        log.info("정책 데이터 처리 진행률: {}/{}", processedCount, policyList.size());
                    }

                } catch (Exception e) {
                    log.error("정책 데이터 처리 중 오류 발생: 정책번호={}, 오류={}", dto.getPlcyNo(), e.getMessage(), e);
                    // 개별 정책 오류 시에도 전체 작업은 계속 진행
                }
            }

            log.info("정책 데이터 적재 완료! 총 {}건 처리 완료", processedCount);

        } catch (Exception e) {
            log.error("정책 데이터 적재 중 전체 오류 발생", e);
            throw new RuntimeException("정책 데이터 적재 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 1. 카테고리 그룹(대분류) 저장
     * JSON의 lclsfNm 필드 → category_group 테이블
     */
    private CategoryGroup saveCategoryGroupIfNotExists(PolicyDataDto dto) {
        return categoryGroupService.findOrCreateByName(dto.getLclsfNm());
    }

    /**
     * 2. 카테고리(중분류) 저장
     * JSON의 mclsfNm 필드 → category 테이블
     */
    private Category saveCategoryIfNotExists(PolicyDataDto dto, CategoryGroup categoryGroup) {
        return categoryService.findOrCreateByNameAndGroup(dto.getMclsfNm(), categoryGroup);
    }

    /**
     * 3. 기관 정보 저장
     * JSON의 여러 기관 필드들 → organization 테이블
     */
    private List<Organization> saveOrganizationsIfNotExists(PolicyDataDto dto) {
        List<Organization> organizations = new java.util.ArrayList<>();

        // 감독 기관
        if (StringUtils.hasText(dto.getSprvsnInstCd())) {
            Organization org = saveOrganizationIfNotExists(
                dto.getSprvsnInstCd(), 
                dto.getSprvsnInstCdNm(), 
                dto.getSprvsnInstPicNm()
            );
            if (org != null) organizations.add(org);
        }

        // 운영 기관
        if (StringUtils.hasText(dto.getOperInstCd())) {
            Organization org = saveOrganizationIfNotExists(
                dto.getOperInstCd(), 
                dto.getOperInstCdNm(), 
                dto.getOperInstPicNm()
            );
            if (org != null) organizations.add(org);
        }

        // 등록 기관
        if (StringUtils.hasText(dto.getRgtrInstCd())) {
            Organization org = saveOrganizationIfNotExists(
                dto.getRgtrInstCd(), 
                dto.getRgtrInstCdNm(), 
                null // 등록기관 담당자명은 DTO에 없음
            );
            if (org != null) organizations.add(org);
        }

        return organizations;
    }

    /**
     * 개별 기관 저장 (중복 체크 포함)
     */
    private Organization saveOrganizationIfNotExists(String orgId, String orgName, String contactName) {
        if (!StringUtils.hasText(orgId)) {
            return null;
        }

        // PK(organization_id)로 중복 체크
        Optional<Organization> existingOrg = organizationRepository.findById(orgId);
        if (existingOrg.isPresent()) {
            return existingOrg.get();
        }

        // 새로운 기관 생성 및 저장
        Organization newOrg = Organization.builder()
                .organizationId(orgId)
                .name(orgName)
                .contactName(contactName)
                .build();
        return organizationRepository.save(newOrg);
    }

    /**
     * 4. 정책 기본 정보 저장
     * JSON의 정책 기본 필드들 → policy 테이블
     */
    private Policy savePolicy(PolicyDataDto dto) {
        Policy policy = Policy.builder()
                .policyId(dto.getPlcyNo())
                .name(dto.getPlcyNm())
                .description(dto.getPlcyExplnCn())
                .supportContent(dto.getPlcySprtCn())
                .applicationMethod(dto.getPlcyAplyMthdCn())
                .reviewMethod(dto.getSrngMthdCn())
                .submissionDocs(dto.getSbmsnDcmntCn())
                .extraInfo(dto.getEtcMttrCn())
                .referenceUrl1(dto.getRefUrlAddr1())
                .referenceUrl2(dto.getRefUrlAddr2())
                .applicationUrl(dto.getAplyUrlAddr())
                .supportScale(dto.getSprtSclCnt())
                .supportScaleLimited("Y".equals(dto.getSprtSclLmtYn()))
                .supportFcfs("Y".equals(dto.getSprtArvlSeqYn()))
                .applicationPeriod(dto.getAplyYmd())
                .businessStartDate(DateUtils.parseDate(dto.getBizPrdBgngYmd()))
                .businessEndDate(DateUtils.parseDate(dto.getBizPrdEndYmd()))
                .businessPeriodNote(dto.getBizPrdEtcCn())
                .inquiryCount(dto.getInqCnt())
                .firstRegisteredAt(DateUtils.parseDateTime(dto.getFrstRegDt()))
                .lastModifiedAt(DateUtils.parseDateTime(dto.getLastMdfcnDt()))
                .operInstCd(dto.getOperInstCd())
                .build();

        return policyRepository.save(policy);
    }

    /**
     * 5. 정책 조건 저장
     * JSON의 조건 필드들 → policy_condition 테이블
     */
    private void savePolicyCondition(PolicyDataDto dto, Policy policy) {
        PolicyCondition condition = PolicyCondition.builder()
                .policyId(policy.getPolicyId())
                .minAge(dto.getSprtTrgtMinAge())
                .maxAge(dto.getSprtTrgtMaxAge())
                .ageLimited("Y".equals(dto.getSprtTrgtAgeLmtYn()))
                .marriageStatusCd(dto.getMrgSttsCd())
                .incomeConditionCd(dto.getEarnCndSeCd())
                .incomeMin(dto.getEarnMinAmt())
                .incomeMax(dto.getEarnMaxAmt())
                .incomeNote(dto.getEarnEtcCn())
                .schoolCd(dto.getSchoolCd())
                .jobCd(dto.getJobCd())
                .majorCd(dto.getPlcyMajorCd())
                .specialConditionCd(dto.getSbizCd())
                .additionalCondition(dto.getAddAplyQlfcCndCn())
                .participantTarget(dto.getPtcpPrpTrgtCn())
                .build();

        policyConditionRepository.save(condition);
    }

    /**
     * 6. 정책 메타 정보 저장
     * JSON의 메타 필드들 → policy_meta 테이블
     */
    private void savePolicyMeta(PolicyDataDto dto, Policy policy) {
        PolicyMeta meta = PolicyMeta.builder()
                .policyId(policy.getPolicyId())
                .planCycle(dto.getBscPlanCycl())
                .planWayNo(dto.getBscPlanPlcyWayNo())
                .focusTaskNo(dto.getBscPlanFcsAsmtNo())
                .taskNo(dto.getBscPlanAsmtNo())
                .approvalStatusCd(dto.getPlcyAprvSttsCd())
                .policyMethodCd(dto.getPlcyPvsnMthdCd())
                .providerGroupCd(dto.getPvsnInstGroupCd())
                .applicationPeriodCd(dto.getAplyPrdSeCd())
                .businessPeriodCd(dto.getBizPrdSeCd())
                .build();

        policyMetaRepository.save(meta);
    }

    /**
     * 7. 정책-카테고리 매핑 저장
     * policy ↔ category 다대다 관계 → policy_category 테이블
     */
    private void savePolicyCategory(Policy policy, Category category) {
        if (category == null) {
            return;
        }

        // 중복 체크: 같은 정책-카테고리 조합이 이미 있는지 확인
        Optional<PolicyCategory> existing = policyCategoryRepository
                .findByPolicyIdAndCategoryId(policy.getPolicyId(), category.getId());
        
        if (existing.isPresent()) {
            return; // 이미 있으면 스킵
        }

        PolicyCategory policyCategory = PolicyCategory.builder()
                .policyId(policy.getPolicyId())
                .categoryId(category.getId())
                .build();

        policyCategoryRepository.save(policyCategory);
    }

    /**
     * 8. 정책 키워드 저장
     * JSON의 plcyKywdNm(쉼표 분리) → policy_keyword 테이블
     */
    private void savePolicyKeywords(PolicyDataDto dto, Policy policy) {
        if (!StringUtils.hasText(dto.getPlcyKywdNm())) {
            return;
        }

        // 쉼표로 분리해서 각각 저장
        String[] keywords = dto.getPlcyKywdNm().split(",");
        for (String keyword : keywords) {
            String trimmedKeyword = keyword.trim();
            if (StringUtils.hasText(trimmedKeyword)) {
                // 중복 체크: 같은 정책에 같은 키워드가 이미 있는지 확인
                Optional<PolicyKeyword> existing = policyKeywordRepository
                        .findByPolicyIdAndKeyword(policy.getPolicyId(), trimmedKeyword);
                
                if (existing.isEmpty()) { // 중복되지 않은 경우만 저장
                    PolicyKeyword policyKeyword = PolicyKeyword.builder()
                            .policyId(policy.getPolicyId())
                            .keyword(trimmedKeyword)
                            .build();
                    policyKeywordRepository.save(policyKeyword);
                }
            }
        }
    }

    /**
     * 9. 정책 지원 지역(우편번호) 저장
     * JSON의 zipCd(쉼표 분리) → policy_zipcode 테이블
     */
    private void savePolicyZipcodes(PolicyDataDto dto, Policy policy) {
        if (!StringUtils.hasText(dto.getZipCd())) {
            return;
        }

        // 쉼표로 분리해서 각각 저장
        String[] zipcodes = dto.getZipCd().split(",");
        for (String zipcode : zipcodes) {
            String trimmedZipcode = zipcode.trim();
            if (StringUtils.hasText(trimmedZipcode)) {
                // 중복 체크: 같은 정책에 같은 우편번호가 이미 있는지 확인
                Optional<PolicyZipcode> existing = policyZipcodeRepository
                        .findByPolicyIdAndZipcode(policy.getPolicyId(), trimmedZipcode);
                
                if (existing.isEmpty()) { // 중복되지 않은 경우만 저장
                    PolicyZipcode policyZipcode = PolicyZipcode.builder()
                            .policyId(policy.getPolicyId())
                            .zipcode(trimmedZipcode)
                            .build();
                    policyZipcodeRepository.save(policyZipcode);
                }
            }
        }
    }

    /**
     * 10. 정책-기관 매핑 저장
     * policy ↔ organization 관계 → policy_organization 테이블
     */
    private void savePolicyOrganizations(Policy policy, List<Organization> organizations, PolicyDataDto dto) {
        for (Organization org : organizations) {
            // 기관별 역할 결정
            String role = determineOrganizationRole(org.getOrganizationId(), dto);
            
            // 중복 체크
            Optional<PolicyOrganization> existing = policyOrganizationRepository
                    .findByPolicyIdAndOrganizationIdAndRole(
                        policy.getPolicyId(), 
                        org.getOrganizationId(), 
                        role
                    );
            
            if (existing.isEmpty()) {
                PolicyOrganization policyOrg = PolicyOrganization.builder()
                        .policyId(policy.getPolicyId())
                        .organizationId(org.getOrganizationId())
                        .role(role)
                        .build();
                policyOrganizationRepository.save(policyOrg);
            }
        }
    }

    /**
     * 기관별 역할 결정 (감독, 운영, 등록 등)
     */
    private String determineOrganizationRole(String organizationId, PolicyDataDto dto) {
        if (organizationId.equals(dto.getSprvsnInstCd())) {
            return "supervising"; // 감독 기관
        } else if (organizationId.equals(dto.getOperInstCd())) {
            return "operating"; // 운영 기관
        } else if (organizationId.equals(dto.getRgtrInstCd())) {
            return "registering"; // 등록 기관
        }
        return "unknown"; // 알 수 없는 역할
    }
} 