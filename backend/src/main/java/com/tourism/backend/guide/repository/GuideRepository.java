package com.tourism.backend.guide.repository;

import com.tourism.backend.guide.entity.Guide;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GuideRepository
        extends JpaRepository<Guide, Long>,
        JpaSpecificationExecutor<Guide> {

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    Optional<Guide> findWithDestinationById(Long id);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(
            String phone,
            Long id
    );

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id
    );

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(
            String licenseNumber,
            Long id
    );
}