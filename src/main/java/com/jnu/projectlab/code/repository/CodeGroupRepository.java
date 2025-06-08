package com.jnu.projectlab.code.repository;

import com.jnu.projectlab.code.entity.CodeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeGroupRepository extends JpaRepository<CodeGroup, Long> {  // String → Long

    // 코드 그룹명으로 조회
    Optional<CodeGroup> findByName(String name);

    // 코드 그룹명 존재 여부 확인
    boolean existsByName(String name);
}