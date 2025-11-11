package com.jnu.projectlab.favoritepolicy.service;

import com.jnu.projectlab.favoritepolicy.entity.UserFavoritePolicy;
import com.jnu.projectlab.favoritepolicy.repository.UserFavoritePolicyRepository;
import com.jnu.projectlab.policy.repository.PolicyRepository;
import com.jnu.projectlab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 즐겨찾기 정책 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFavoritePolicyService {

    private final UserFavoritePolicyRepository favoritePolicyRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;

    /**
     * 즐겨찾기 추가 (좋아요 누르기)
     * 멱등성 보장: 이미 존재하면 무시
     * 
     * @param userId 사용자 ID
     * @param policyId 정책 ID
     * @return 추가 성공 여부 (true: 새로 추가, false: 이미 존재)
     */
    @Transactional
    public boolean addFavorite(String userId, String policyId) {
        // 1. 사용자 존재 여부 확인
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        // 2. 정책 존재 여부 확인
        if (!policyRepository.existsById(policyId)) {
            throw new IllegalArgumentException("존재하지 않는 정책입니다: " + policyId);
        }

        // 3. 이미 즐겨찾기한 경우 무시 (멱등성)
        if (favoritePolicyRepository.existsByUserIdAndPolicyId(userId, policyId)) {
            log.info("이미 즐겨찾기한 정책입니다: userId={}, policyId={}", userId, policyId);
            return false;
        }

        // 4. 즐겨찾기 추가
        UserFavoritePolicy favorite = UserFavoritePolicy.builder()
                .userId(userId)
                .policyId(policyId)
                .build();

        favoritePolicyRepository.save(favorite);
        log.info("즐겨찾기 추가 성공: userId={}, policyId={}", userId, policyId);
        return true;
    }

    /**
     * 즐겨찾기 삭제 (좋아요 취소)
     * 멱등성 보장: 존재하지 않아도 예외 발생 안 함
     * 
     * @param userId 사용자 ID
     * @param policyId 정책 ID
     * @return 삭제 성공 여부 (true: 삭제됨, false: 없었음)
     */
    @Transactional
    public boolean removeFavorite(String userId, String policyId) {
        // 1. 사용자 존재 여부 확인
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        // 2. 즐겨찾기 존재 여부 확인
        Optional<UserFavoritePolicy> favorite = favoritePolicyRepository.findByUserIdAndPolicyId(userId, policyId);
        
        if (favorite.isEmpty()) {
            log.info("삭제할 즐겨찾기가 없습니다: userId={}, policyId={}", userId, policyId);
            return false;
        }

        // 3. 즐겨찾기 삭제
        favoritePolicyRepository.deleteByUserIdAndPolicyId(userId, policyId);
        log.info("즐겨찾기 삭제 성공: userId={}, policyId={}", userId, policyId);
        return true;
    }

    /**
     * 즐겨찾기 토글 (추가/삭제 자동 판단)
     * 
     * @param userId 사용자 ID
     * @param policyId 정책 ID
     * @return 토글 후 상태 (true: 즐겨찾기됨, false: 즐겨찾기 해제됨)
     */
    @Transactional
    public boolean toggleFavorite(String userId, String policyId) {
        if (favoritePolicyRepository.existsByUserIdAndPolicyId(userId, policyId)) {
            removeFavorite(userId, policyId);
            return false;
        } else {
            addFavorite(userId, policyId);
            return true;
        }
    }

    /**
     * 특정 사용자의 즐겨찾기 목록 조회 (페이징)
     * 
     * @param userId 사용자 ID
     * @param pageable 페이지 정보
     * @return 즐겨찾기 목록 (페이징)
     */
    public Page<UserFavoritePolicy> getFavoritesByUser(String userId, Pageable pageable) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        return favoritePolicyRepository.findByUserIdOrderByFavoritedAtDesc(userId, pageable);
    }

    /**
     * 특정 사용자의 즐겨찾기 목록 조회 (전체)
     * 
     * @param userId 사용자 ID
     * @return 즐겨찾기 목록
     */
    public List<UserFavoritePolicy> getAllFavoritesByUser(String userId) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        return favoritePolicyRepository.findByUserIdOrderByFavoritedAtDesc(userId);
    }

    /**
     * 특정 사용자가 특정 정책을 즐겨찾기했는지 확인
     * 
     * @param userId 사용자 ID
     * @param policyId 정책 ID
     * @return 즐겨찾기 여부
     */
    public boolean isFavorited(String userId, String policyId) {
        return favoritePolicyRepository.existsByUserIdAndPolicyId(userId, policyId);
    }

    /**
     * 특정 사용자의 즐겨찾기 정책 ID 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 정책 ID 목록
     */
    public List<String> getFavoritePolicyIds(String userId) {
        return favoritePolicyRepository.findPolicyIdsByUserId(userId);
    }

    /**
     * 특정 정책의 즐겨찾기 수 조회
     * 
     * @param policyId 정책 ID
     * @return 즐겨찾기 수
     */
    public long getFavoriteCount(String policyId) {
        return favoritePolicyRepository.countByPolicyId(policyId);
    }

    /**
     * 특정 사용자의 총 즐겨찾기 수 조회
     * 
     * @param userId 사용자 ID
     * @return 즐겨찾기 수
     */
    public long getUserFavoriteCount(String userId) {
        return favoritePolicyRepository.countByUserId(userId);
    }

    /**
     * 정책 ID 리스트에서 사용자가 즐겨찾기한 정책 ID만 추출
     * (정책 목록에서 isFavorited 플래그 일괄 처리용)
     * 
     * @param userId 사용자 ID
     * @param policyIds 정책 ID 리스트
     * @return 즐겨찾기한 정책 ID 리스트
     */
    public List<String> filterFavoritedPolicyIds(String userId, List<String> policyIds) {
        if (policyIds == null || policyIds.isEmpty()) {
            return List.of();
        }
        return favoritePolicyRepository.findPolicyIdsByUserIdAndPolicyIdIn(userId, policyIds);
    }
}

