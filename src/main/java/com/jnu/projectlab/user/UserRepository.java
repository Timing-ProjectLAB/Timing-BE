package com.jnu.projectlab.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // userId로 사용자 정보를 가져오는 메서드
    // 로그인 시 사용자 조회에 사용됨
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
