package com.jnu.projectlab.organization.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organization")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
    @Id
    @JsonProperty("sprvsnInstCd")
    @Column(name = "organization_id")
    private String organizationId;  // 기관코드

    @JsonProperty("sprvsnInstCdNm")
    @Column(name = "name", length = 500)
    private String name;  // 기관명

    @JsonProperty("sprvsnInstPicNm")
    @Column(name = "contact_name")
    private String contactName;  // 기관담당자명
}