package com.jnu.projectlab.code.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "code_group")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("cdGroupNm")
    @Column(name = "name", nullable = false)
    private String name;  // 코드그룹명
}