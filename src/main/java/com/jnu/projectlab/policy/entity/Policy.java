package com.jnu.projectlab.policy.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 정책 메인 엔티티
 * 청년정책의 기본 정보를 저장하는 테이블
 */
@Entity
@Table(name = "policy")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @JsonProperty("plcyNo")
    @Column(name = "policy_id")
    private String policyId; // 정책번호

    @JsonProperty("plcyNm")
    @Column(name = "name")
    private String name; // 정책명

    @JsonProperty("plcyExplnCn")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 정책설명내용

    @JsonProperty("plcySprtCn")
    @Column(name = "support_content", columnDefinition = "TEXT")
    private String supportContent; // 정책지원내용

    @JsonProperty("plcyAplyMthdCn")
    @Column(name = "application_method", columnDefinition = "TEXT")
    private String applicationMethod; // 신청방법

    @JsonProperty("srngMthdCn")
    @Column(name = "review_method", columnDefinition = "TEXT")
    private String reviewMethod; // 심사방법

    @JsonProperty("sbmsnDcmntCn")
    @Column(name = "submission_docs", columnDefinition = "TEXT")
    private String submissionDocs; // 제출서류내용

    @JsonProperty("etcMttrCn")
    @Column(name = "extra_info", columnDefinition = "TEXT")
    private String extraInfo; // 기타사항내용

    @JsonProperty("refUrlAddr1")
    @Column(name = "reference_url1")
    private String referenceUrl1; // 참고URL1

    @JsonProperty("refUrlAddr2")
    @Column(name = "reference_url2")
    private String referenceUrl2; // 참고URL2

    @JsonProperty("aplyUrlAddr")
    @Column(name = "application_url")
    private String applicationUrl; // 신청URL

    @JsonProperty("sprtSclCnt")
    @Column(name = "support_scale")
    private Integer supportScale; // 지원규모수

    @JsonProperty("sprtSclLmtYn")
    @Column(name = "support_scale_limited")
    private Boolean supportScaleLimited; // 지원규모제한여부

    @JsonProperty("sprtArvlSeqYn")
    @Column(name = "support_fcfs")
    private Boolean supportFcfs; // 선착순지원여부

    @JsonProperty("aplyYmd")
    @Column(name = "application_period")
    private String applicationPeriod; // 신청기간

    @JsonProperty("bizPrdBgngYmd")
    @Column(name = "business_start_date")
    private LocalDate businessStartDate; // 사업시작일

    @JsonProperty("bizPrdEndYmd")
    @Column(name = "business_end_date")
    private LocalDate businessEndDate; // 사업종료일

    @JsonProperty("bizPrdEtcCn")
    @Column(name = "business_period_note", columnDefinition = "TEXT")
    private String businessPeriodNote; // 사업기간기타내용

    @JsonProperty("inqCnt")
    @Column(name = "inquiry_count")
    private Integer inquiryCount; // 조회수

    @JsonProperty("frstRegDt")
    @CreationTimestamp
    @Column(name = "first_registered_at")
    private LocalDateTime firstRegisteredAt; // 최초등록일시

    @JsonProperty("lastMdfcnDt")
    @UpdateTimestamp
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt; // 최종수정일시
}