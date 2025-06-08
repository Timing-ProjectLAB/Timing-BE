package com.jnu.projectlab.code.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "code")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private CodeGroup group;

    @JsonProperty("cdNm")
    @Column(name = "name", nullable = false)
    private String name;  // 코드명

    @JsonProperty("cdExpln")
    @Column(name = "description", length = 1000)
    private String description;  // 코드설명
}