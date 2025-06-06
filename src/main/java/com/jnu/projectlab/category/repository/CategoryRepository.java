package com.jnu.projectlab.category.repository;

import com.jnu.projectlab.category.entity.Category;
import com.jnu.projectlab.category.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // 특정 카테고리 그룹의 카테고리들 조회
    List<Category> findByGroupId(Long groupId);  // Integer → Long

    // 카테고리명으로 조회
    Optional<Category> findByName(String name);

    // 카테고리명과 그룹으로 조회 (중복 체크용)
    Optional<Category> findByNameAndGroup(String name, CategoryGroup group);

    // 카테고리명과 그룹ID로 조회
    Optional<Category> findByNameAndGroupId(String name, Long groupId);  // Integer → Long
}