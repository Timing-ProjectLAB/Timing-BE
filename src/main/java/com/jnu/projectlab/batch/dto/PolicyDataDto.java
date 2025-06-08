package com.jnu.projectlab.batch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PolicyDataDto {
    @JsonProperty("plcyNo")
    private String plcyNo;

    @JsonProperty("plcyNm")
    private String plcyNm;

    @JsonProperty("plcyExplnCn")
    private String plcyExplnCn;

    @JsonProperty("plcySprtCn")
    private String plcySprtCn;

    @JsonProperty("plcyAplyMthdCn")
    private String plcyAplyMthdCn;

    @JsonProperty("srngMthdCn")
    private String srngMthdCn;

    @JsonProperty("sbmsnDcmntCn")
    private String sbmsnDcmntCn;

    @JsonProperty("etcMttrCn")
    private String etcMttrCn;

    @JsonProperty("refUrlAddr1")
    private String refUrlAddr1;

    @JsonProperty("refUrlAddr2")
    private String refUrlAddr2;

    @JsonProperty("aplyUrlAddr")
    private String aplyUrlAddr;

    @JsonProperty("sprtSclCnt")
    private Integer sprtSclCnt;

    @JsonProperty("sprtSclLmtYn")
    private String sprtSclLmtYn;

    @JsonProperty("sprtArvlSeqYn")
    private String sprtArvlSeqYn;

    @JsonProperty("aplyYmd")
    private String aplyYmd;

    @JsonProperty("bizPrdBgngYmd")
    private String bizPrdBgngYmd;

    @JsonProperty("bizPrdEndYmd")
    private String bizPrdEndYmd;

    @JsonProperty("bizPrdEtcCn")
    private String bizPrdEtcCn;

    @JsonProperty("inqCnt")
    private Integer inqCnt;

    @JsonProperty("frstRegDt")
    private String frstRegDt;

    @JsonProperty("lastMdfcnDt")
    private String lastMdfcnDt;

    // ---- 추가 필드 (policy_condition, policy_meta, category, 기관 등) ----
    @JsonProperty("sprtTrgtMinAge")
    private Integer sprtTrgtMinAge;
    @JsonProperty("sprtTrgtMaxAge")
    private Integer sprtTrgtMaxAge;
    @JsonProperty("sprtTrgtAgeLmtYn")
    private String sprtTrgtAgeLmtYn;
    @JsonProperty("mrgSttsCd")
    private String mrgSttsCd;
    @JsonProperty("earnCndSeCd")
    private String earnCndSeCd;
    @JsonProperty("earnMinAmt")
    private Integer earnMinAmt;
    @JsonProperty("earnMaxAmt")
    private Integer earnMaxAmt;
    @JsonProperty("earnEtcCn")
    private String earnEtcCn;
    @JsonProperty("schoolCd")
    private String schoolCd;
    @JsonProperty("jobCd")
    private String jobCd;
    @JsonProperty("plcyMajorCd")
    private String plcyMajorCd;
    @JsonProperty("sbizCd")
    private String sbizCd;
    @JsonProperty("addAplyQlfcCndCn")
    private String addAplyQlfcCndCn;
    @JsonProperty("ptcpPrpTrgtCn")
    private String ptcpPrpTrgtCn;

    // 메타
    @JsonProperty("bscPlanCycl")
    private String bscPlanCycl;
    @JsonProperty("bscPlanPlcyWayNo")
    private String bscPlanPlcyWayNo;
    @JsonProperty("bscPlanFcsAsmtNo")
    private String bscPlanFcsAsmtNo;
    @JsonProperty("bscPlanAsmtNo")
    private String bscPlanAsmtNo;
    @JsonProperty("plcyAprvSttsCd")
    private String plcyAprvSttsCd;
    @JsonProperty("plcyPvsnMthdCd")
    private String plcyPvsnMthdCd;
    @JsonProperty("pvsnInstGroupCd")
    private String pvsnInstGroupCd;
    @JsonProperty("aplyPrdSeCd")
    private String aplyPrdSeCd;
    @JsonProperty("bizPrdSeCd")
    private String bizPrdSeCd;

    // 카테고리
    @JsonProperty("lclsfNm")
    private String lclsfNm;
    @JsonProperty("mclsfNm")
    private String mclsfNm;

    // 키워드, 우편번호
    @JsonProperty("plcyKywdNm")
    private String plcyKywdNm;
    @JsonProperty("zipCd")
    private String zipCd;

    // 기관
    @JsonProperty("sprvsnInstCd")
    private String sprvsnInstCd;
    @JsonProperty("sprvsnInstCdNm")
    private String sprvsnInstCdNm;
    @JsonProperty("sprvsnInstPicNm")
    private String sprvsnInstPicNm;
    @JsonProperty("operInstCd")
    private String operInstCd;
    @JsonProperty("operInstCdNm")
    private String operInstCdNm;
    @JsonProperty("operInstPicNm")
    private String operInstPicNm;
    @JsonProperty("rgtrInstCd")
    private String rgtrInstCd;
    @JsonProperty("rgtrInstCdNm")
    private String rgtrInstCdNm;
    @JsonProperty("rgtrUpInstCd")
    private String rgtrUpInstCd;
    @JsonProperty("rgtrUpInstCdNm")
    private String rgtrUpInstCdNm;
    @JsonProperty("rgtrHghrkInstCd")
    private String rgtrHghrkInstCd;
    @JsonProperty("rgtrHghrkInstCdNm")
    private String rgtrHghrkInstCdNm;
} 