package com.jnu.projectlab.policy.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정책 메타데이터 엔티티
 * 정책의 계획정보, 승인상태 등 메타데이터를 저장하는 테이블
 */
@Entity
@Table(name = "policy_meta")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyMeta {

    @Id
    @JsonProperty("plcyNo")
    @Column(name = "policy_id")
    private String policyId; // 정책번호

    @JsonProperty("bscPlanCycl")
    @Column(name = "plan_cycle")
    private String planCycle; // 기본계획차수

    @JsonProperty("bscPlanPlcyWayNo")
    @Column(name = "plan_way_no")
    private String planWayNo; // 정책방향번호

    @JsonProperty("bscPlanFcsAsmtNo")
    @Column(name = "focus_task_no")
    private String focusTaskNo; // 중점과제번호

    @JsonProperty("bscPlanAsmtNo")
    @Column(name = "task_no")
    private String taskNo; // 과제번호

    @JsonProperty("plcyAprvSttsCd")
    @Column(name = "approval_status_cd")
    private String approvalStatusCd; // 승인상태코드

    @JsonProperty("plcyPvsnMthdCd")
    @Column(name = "policy_method_cd")
    private String policyMethodCd; // 제공방법코드

    @JsonProperty("pvsnInstGroupCd")
    @Column(name = "provider_group_cd")
    private String providerGroupCd; // 제공기관그룹코드

    @JsonProperty("aplyPrdSeCd")
    @Column(name = "application_period_cd")
    private String applicationPeriodCd; // 신청기간구분코드

    @JsonProperty("bizPrdSeCd")
    @Column(name = "business_period_cd")
    private String businessPeriodCd; // 사업기간구분코드
}