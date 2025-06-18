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
import java.util.ArrayList;
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

    // ============== 배치 처리용 리스트 ==============
    private List<Policy> batchPolicies;
    private List<PolicyCondition> batchConditions;
    private List<PolicyMeta> batchMetas;
    private List<PolicyCategory> batchPolicyCategories;
    private List<Organization> batchOrganizations;
    private List<PolicyKeyword> batchPolicyKeywords;
    private List<PolicyZipcode> batchPolicyZipcodes;
    private List<PolicyOrganization> batchPolicyOrganizations;

    private void initializeBatchLists() {
        batchPolicies = new ArrayList<>();
        batchConditions = new ArrayList<>();
        batchMetas = new ArrayList<>();
        batchPolicyCategories = new ArrayList<>();
        batchOrganizations = new ArrayList<>();
        batchPolicyKeywords = new ArrayList<>();
        batchPolicyZipcodes = new ArrayList<>();
        batchPolicyOrganizations = new ArrayList<>();
    }

    /**
     * 정책 데이터 전체 적재 메인 메서드
     * ERD의 모든 테이블에 데이터를 저장합니다.
     */
    @Transactional
    public void loadPolicyData() {
        try {
            String json = Files.readString(Paths.get("data/all_policy_data.json"));
            List<PolicyDataDto> policyList = objectMapper.readValue(json, new TypeReference<List<PolicyDataDto>>() {});
            int batchSize = 100;

            for (int i = 0; i < policyList.size(); i += batchSize) {
                initializeBatchLists();
                
                int endIndex = Math.min(i + batchSize, policyList.size());
                List<PolicyDataDto> batch = policyList.subList(i, endIndex);

                for (PolicyDataDto dto : batch) {
                    // 기존 저장 로직 그대로 사용
                    CategoryGroup categoryGroup = saveCategoryGroupIfNotExists(dto);
                    Category category = saveCategoryIfNotExists(dto, categoryGroup);
                    List<Organization> organizations = saveOrganizationsIfNotExists(dto);

                    // 객체 생성 (저장 안 함)
                    Policy policy = buildPolicy(dto);
                    PolicyCondition condition = buildPolicyCondition(dto, policy);
                    PolicyMeta meta = buildPolicyMeta(dto, policy);
                    PolicyCategory policyCategory = buildPolicyCategory(policy, category);

                    // 배치 리스트에 추가
                    batchPolicies.add(policy);
                    batchConditions.add(condition);
                    batchMetas.add(meta);
                    if (policyCategory != null) {
                        batchPolicyCategories.add(policyCategory);
                    }

                    // 1:N 관계도 배치 처리로 변경
                    buildPolicyKeywords(dto, policy);
                    buildPolicyZipcodes(dto, policy);
                    buildPolicyOrganizations(policy, organizations, dto);
                }
                
                // 배치 저장
                saveAllBatches();
                
                log.info("배치 처리 진행률: {}/{}", endIndex, policyList.size());
            }
        } catch (Exception e) {
            log.error("정책 데이터 적재 중 오류 발생", e);
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
        String orgId = dto.getSprvsnInstCd();
        if (orgId == null || orgId.trim().isEmpty()) {
            orgId = "UNKNOWN_SUPERVISOR"; // 기본값
        }
        Organization org = buildOrganizationIfNotExists(orgId, dto.getSprvsnInstCdNm(), dto.getSprvsnInstPicNm());
        if (org != null) organizations.add(org);

        // 운영 기관
        String operOrgId = dto.getOperInstCd();
        if (operOrgId == null || operOrgId.trim().isEmpty()) {
            operOrgId = "UNKNOWN_OPERATOR"; // 기본값
        }
        Organization operOrg = buildOrganizationIfNotExists(operOrgId, dto.getOperInstCdNm(), dto.getOperInstPicNm());
        if (operOrg != null) organizations.add(operOrg);

        // 등록 기관
        String rgtrOrgId = dto.getRgtrInstCd();
        if (rgtrOrgId == null || rgtrOrgId.trim().isEmpty()) {
            rgtrOrgId = "UNKNOWN_REGISTRAR"; // 기본값
        }
        Organization rgtrOrg = buildOrganizationIfNotExists(rgtrOrgId, dto.getRgtrInstCdNm(), null);
        if (rgtrOrg != null) organizations.add(rgtrOrg);

        return organizations;
    }

    /**
     * 기관 객체 생성 (배치 저장용)
     */
    private Organization buildOrganizationIfNotExists(String orgId, String orgName, String contactName) {
        // PK(organization_id)로 중복 체크
        Optional<Organization> existingOrg = organizationRepository.findById(orgId);
        if (existingOrg.isPresent()) {
            return existingOrg.get();
        }

        // 새로운 기관 생성 (저장하지 않음)
        Organization newOrg = Organization.builder()
                .organizationId(orgId)
                .name(orgName)
                .contactName(contactName)
                .build();
        
        // 배치 리스트에 추가
        batchOrganizations.add(newOrg);
        return newOrg;
    }

    /**
     * 4. 정책 기본 정보 생성 (저장하지 않음)
     * JSON의 정책 기본 필드들 → policy 객체 생성
     */
    private Policy buildPolicy(PolicyDataDto dto) {
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

        return policy; // 저장하지 않고 객체만 반환
    }

    /**
     * 5. 정책 조건 저장
     * JSON의 조건 필드들 → policy_condition 테이블
     */
    private PolicyCondition buildPolicyCondition(PolicyDataDto dto, Policy policy) {
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

        return condition;
    }

    /**
     * 6. 정책 메타 정보 저장
     * JSON의 메타 필드들 → policy_meta 테이블
     */
    private PolicyMeta buildPolicyMeta(PolicyDataDto dto, Policy policy) {
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

        return meta;
    }

    /**
     * 7. 정책-카테고리 매핑 저장
     * policy ↔ category 다대다 관계 → policy_category 테이블
     */
    private PolicyCategory buildPolicyCategory(Policy policy, Category category) {
        if (category == null) {
            CategoryGroup defaultGroup = categoryGroupService.findOrCreateByName("기타");
            category = categoryService.findOrCreateByNameAndGroup("일반정책", defaultGroup);
        }

        // 중복 체크: 같은 정책-카테고리 조합이 이미 있는지 확인
        Optional<PolicyCategory> existing = policyCategoryRepository
                .findByPolicyIdAndCategoryId(policy.getPolicyId(), category.getId());
        
        if (existing.isPresent()) {
            return existing.get();
        }

        PolicyCategory policyCategory = PolicyCategory.builder()
                .policyId(policy.getPolicyId())
                .categoryId(category.getId())
                .build();

        return policyCategory;
    }

    /**
     * 8. 정책 키워드 생성 (배치 저장용)
     * JSON의 plcyKywdNm(쉼표 분리) → policy_keyword 객체 생성
     */
    private void buildPolicyKeywords(PolicyDataDto dto, Policy policy) {
        String keywordStr = dto.getPlcyKywdNm();
        if (keywordStr == null || keywordStr.trim().isEmpty()) {
            keywordStr = "일반정책"; // 기본 키워드
        }

        // 쉼표로 분리해서 각각 생성
        String[] keywords = keywordStr.split(",");
        for (String keyword : keywords) {
            String trimmedKeyword = keyword.trim();
            // 모든 키워드 생성 (중복 체크는 배치 저장 시 DB에서 처리)
            PolicyKeyword policyKeyword = PolicyKeyword.builder()
                    .policyId(policy.getPolicyId())
                    .keyword(trimmedKeyword)
                    .build();
            batchPolicyKeywords.add(policyKeyword);
        }
    }

    /**
     * 9. 정책 지원 지역(우편번호) 생성 (배치 저장용)
     * JSON의 zipCd(쉼표 분리) → policy_zipcode 객체 생성
     */
    private void buildPolicyZipcodes(PolicyDataDto dto, Policy policy) {
        String zipcodeStr = dto.getZipCd();
        if (zipcodeStr == null || zipcodeStr.trim().isEmpty()) {
            zipcodeStr = "00000"; // 전국 대상 의미
        }

        // 쉼표로 분리해서 각각 생성
        String[] zipcodes = zipcodeStr.split(",");
        for (String zipcode : zipcodes) {
            String trimmedZipcode = zipcode.trim();
            // 모든 우편번호 생성 (중복 체크는 배치 저장 시 DB에서 처리)
            PolicyZipcode policyZipcode = PolicyZipcode.builder()
                    .policyId(policy.getPolicyId())
                    .zipcode(trimmedZipcode)
                    .build();
            batchPolicyZipcodes.add(policyZipcode);
        }
    }

    /**
     * 10. 정책-기관 매핑 생성 (배치 저장용)
     * policy ↔ organization 관계 → policy_organization 객체 생성
     */
    private void buildPolicyOrganizations(Policy policy, List<Organization> organizations, PolicyDataDto dto) {
        for (Organization org : organizations) {
            // 기관별 역할 결정
            String role = determineOrganizationRole(org.getOrganizationId(), dto);
            
            // 중복 체크 없이 객체 생성 (배치 저장 시 DB에서 처리)
            PolicyOrganization policyOrg = PolicyOrganization.builder()
                    .policyId(policy.getPolicyId())
                    .organizationId(org.getOrganizationId())
                    .role(role)
                    .build();
            batchPolicyOrganizations.add(policyOrg);
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

    /**
     * 배치로 모은 데이터들을 한 번에 저장
     */
    private void saveAllBatches() {
        // 기존 배치들
        if (!batchPolicies.isEmpty()) {
            policyRepository.saveAll(batchPolicies);
        }
        if (!batchConditions.isEmpty()) {
            policyConditionRepository.saveAll(batchConditions);
        }
        if (!batchMetas.isEmpty()) {
            policyMetaRepository.saveAll(batchMetas);
        }
        if (!batchPolicyCategories.isEmpty()) {
            policyCategoryRepository.saveAll(batchPolicyCategories);
        }
        
        // 새로 추가된 배치들
        if (!batchOrganizations.isEmpty()) {
            organizationRepository.saveAll(batchOrganizations);
        }
        if (!batchPolicyKeywords.isEmpty()) {
            policyKeywordRepository.saveAll(batchPolicyKeywords);
        }
        if (!batchPolicyZipcodes.isEmpty()) {
            policyZipcodeRepository.saveAll(batchPolicyZipcodes);
        }
        if (!batchPolicyOrganizations.isEmpty()) {
            policyOrganizationRepository.saveAll(batchPolicyOrganizations);
        }
    }
} 