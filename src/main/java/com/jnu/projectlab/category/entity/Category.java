package com.jnu.projectlab.category.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Builder.Default  // ← 추가
    private Integer id = null;  // ← 명시적 null 설정

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private CategoryGroup group;

    @JsonProperty("mclsfNm")
    @Column(name = "name", nullable = false)
    private String name;  // 정책중분류
}