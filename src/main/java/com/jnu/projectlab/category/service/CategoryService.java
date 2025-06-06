package com.jnu.projectlab.category.service;

import com.jnu.projectlab.category.entity.Category;
import com.jnu.projectlab.category.entity.CategoryGroup;
import com.jnu.projectlab.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category findOrCreateByNameAndGroup(String name, CategoryGroup categoryGroup) {
        if (!StringUtils.hasText(name) || categoryGroup == null) {
            return null;
        }

        // 중복 체크: 같은 그룹 내에서 같은 이름이 있는지 확인
        Optional<Category> existingCategory = categoryRepository.findByNameAndGroup(name, categoryGroup);
        if (existingCategory.isPresent()) {
            return existingCategory.get();
        }

        // 새로운 카테고리 생성 및 저장
        Category newCategory = Category.builder()
                .name(name)
                .group(categoryGroup)
                .build();
        return categoryRepository.save(newCategory);
    }
}