package com.jnu.projectlab.organization.repository;

import com.jnu.projectlab.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {
    // organizationId로 Organization 조회
    Organization findByOrganizationId(String organizationId);
}