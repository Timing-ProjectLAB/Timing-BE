package com.jnu.projectlab.category.service;

import com.jnu.projectlab.category.entity.CategoryGroup;
import com.jnu.projectlab.category.repository.CategoryGroupRepository;
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
public class CategoryGroupService {

    private final CategoryGroupRepository categoryGroupRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CategoryGroup findOrCreateByName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        // 중복 체크: 이미 있으면 기존 것 사용, 없으면 새로 생성
        Optional<CategoryGroup> existingGroup = categoryGroupRepository.findByName(name);
        if (existingGroup.isPresent()) {
            return existingGroup.get();
        }

        // 새로운 카테고리 그룹 생성 및 저장
        CategoryGroup newGroup = CategoryGroup.builder()
                .name(name)
                .build();
        return categoryGroupRepository.save(newGroup);
    }
}