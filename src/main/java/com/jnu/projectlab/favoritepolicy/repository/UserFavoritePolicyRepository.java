package com.jnu.projectlab.favoritepolicy.repository;

import com.jnu.projectlab.favoritepolicy.entity.UserFavoritePolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 즐겨찾기 정책 Repository
 */
@Repository
public interface UserFavoritePolicyRepository extends JpaRepository<UserFavoritePolicy, Long> {

    /**
     * 특정 사용자의 특정 정책 즐겨찾기 조회
     * (중복 체크, 존재 여부 확인용)
     */
    Optional<UserFavoritePolicy> findByUserIdAndPolicyId(String userId, String policyId);

    /**
     * 특정 사용자의 특정 정책 즐겨찾기 존재 여부 확인
     * (isFavorited 플래그 반환용)
     */
    boolean existsByUserIdAndPolicyId(String userId, String policyId);

    /**
     * 특정 사용자의 모든 즐겨찾기 조회 (페이징)
     * (즐겨찾기 페이지용)
     */
    Page<UserFavoritePolicy> findByUserIdOrderByFavoritedAtDesc(String userId, Pageable pageable);

    /**
     * 특정 사용자의 모든 즐겨찾기 조회 (리스트)
     */
    List<UserFavoritePolicy> findByUserIdOrderByFavoritedAtDesc(String userId);

    /**
     * 특정 정책의 즐겨찾기 수 조회
     * (인기도 집계용)
     */
    long countByPolicyId(String policyId);

    /**
     * 특정 사용자와 정책 ID 리스트로 즐겨찾기 여부 일괄 조회
     * (정책 목록에서 isFavorited 플래그 일괄 처리용)
     */
    @Query("SELECT ufp.policyId FROM UserFavoritePolicy ufp WHERE ufp.userId = :userId AND ufp.policyId IN :policyIds")
    List<String> findPolicyIdsByUserIdAndPolicyIdIn(@Param("userId") String userId, @Param("policyIds") List<String> policyIds);

    /**
     * 특정 사용자가 즐겨찾기한 정책 ID 목록 조회
     * (내 즐겨찾기 정책 ID만 가져올 때)
     */
    @Query("SELECT ufp.policyId FROM UserFavoritePolicy ufp WHERE ufp.userId = :userId ORDER BY ufp.favoritedAt DESC")
    List<String> findPolicyIdsByUserId(@Param("userId") String userId);

    /**
     * 특정 사용자의 즐겨찾기 삭제
     */
    void deleteByUserIdAndPolicyId(String userId, String policyId);

    /**
     * 특정 사용자의 모든 즐겨찾기 개수
     */
    long countByUserId(String userId);
}

