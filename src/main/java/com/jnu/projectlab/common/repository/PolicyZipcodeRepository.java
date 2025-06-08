package com.jnu.projectlab.common.repository;

import com.jnu.projectlab.common.entity.PolicyZipcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyZipcodeRepository extends JpaRepository<PolicyZipcode, Long> {

    // 특정 정책의 지원 지역(우편번호) 조회
    List<PolicyZipcode> findByPolicyId(String policyId);

    // 특정 우편번호로 지원하는 정책들 조회
    List<PolicyZipcode> findByZipcode(String zipcode);

    // 정책-우편번호 조합으로 조회 (중복 체크용)
    Optional<PolicyZipcode> findByPolicyIdAndZipcode(String policyId, String zipcode);


}