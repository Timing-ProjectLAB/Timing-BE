package com.jnu.projectlab.category.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category_group")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Builder.Default  // ← 추가
    private Long id = null;  // ← 명시적 null 설정

    @JsonProperty("lclsfNm")
    @Column(name = "name", nullable = false)
    private String name;  // 정책대분류
}