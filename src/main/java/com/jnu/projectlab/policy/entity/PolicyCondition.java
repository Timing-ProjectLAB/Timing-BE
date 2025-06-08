package com.jnu.projectlab.policy.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정책 조건 엔티티
 * 정책 신청 자격 조건과 관련된 정보를 저장하는 테이블
 */
@Entity
@Table(name = "policy_condition")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyCondition {

    @Id
    @JsonProperty("plcyNo")
    @Column(name = "policy_id")
    private String policyId; // 정책번호

    @JsonProperty("sprtTrgtMinAge")
    @Column(name = "min_age")
    private Integer minAge; // 최소연령

    @JsonProperty("sprtTrgtMaxAge")
    @Column(name = "max_age")
    private Integer maxAge; // 최대연령

    @JsonProperty("sprtTrgtAgeLmtYn")
    @Column(name = "age_limited")
    private Boolean ageLimited; // 연령제한여부

    @JsonProperty("mrgSttsCd")
    @Column(name = "marriage_status_cd")
    private String marriageStatusCd; // 결혼상태코드

    @JsonProperty("earnCndSeCd")
    @Column(name = "income_condition_cd")
    private String incomeConditionCd; // 소득조건코드

    @JsonProperty("earnMinAmt")
    @Column(name = "income_min")
    private Integer incomeMin; // 소득최소금액

    @JsonProperty("earnMaxAmt")
    @Column(name = "income_max")
    private Integer incomeMax; // 소득최대금액

    @JsonProperty("earnEtcCn")
    @Column(name = "income_note", length = 1000)
    private String incomeNote; // 소득기타내용

    @JsonProperty("schoolCd")
    @Column(name = "school_cd")
    private String schoolCd; // 학력요건

    @JsonProperty("jobCd")
    @Column(name = "job_cd")
    private String jobCd; // 직업요건

    @JsonProperty("plcyMajorCd")
    @Column(name = "major_cd")
    private String majorCd; // 전공요건

    @JsonProperty("sBizCd")
    @Column(name = "special_condition_cd")
    private String specialConditionCd; // 특화요건코드

    @JsonProperty("addAplyQlfcCndCn")
    @Column(name = "additional_condition", length = 2000)
    private String additionalCondition; // 추가신청조건

    @JsonProperty("ptcpPrpTrgtCn")
    @Column(name = "participant_target", length = 2000)
    private String participantTarget; // 참여제안대상
}