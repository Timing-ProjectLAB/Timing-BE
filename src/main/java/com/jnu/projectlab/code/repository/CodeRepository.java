package com.jnu.projectlab.code.repository;

import com.jnu.projectlab.code.entity.Code;
import com.jnu.projectlab.code.entity.CodeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {  // String → Long

    // 특정 코드 그룹의 코드들 조회
    List<Code> findByGroup(CodeGroup group);

    // 코드명으로 조회
    Optional<Code> findByName(String name);

    // 코드ID와 그룹으로 조회 (중복 체크용)
    Optional<Code> findByIdAndGroup(Long id, CodeGroup group);  // String → Long

    // 코드ID 존재 여부 확인
    boolean existsById(Long id);  // String → Long
}