package com.jnu.projectlab.category.repository;

import com.jnu.projectlab.category.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {  // Integer → Long

    // 카테고리 그룹명으로 조회 (중복 체크용)
    Optional<CategoryGroup> findByName(String name);

    // 카테고리 그룹명 존재 여부 확인
    boolean existsByName(String name);
}