package com.tourism.backend.guide.repository;

import com.tourism.backend.guide.entity.Guide;
import com.tourism.backend.guide.entity.Language;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    Optional<Guide> findWithDestinationById(Long id);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    List<Guide> findAllByOrderByRatingDescNameAsc();

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    List<Guide> findAllByDestination_IdOrderByRatingDescNameAsc(Long destinationId);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    List<Guide> findAllByAvailableTrueOrderByRatingDescNameAsc();

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    List<Guide> findAllByProvidesTransportTrueOrderByRatingDescNameAsc();

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    List<Guide> findAllByLanguagesContaining(Language language);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);
}