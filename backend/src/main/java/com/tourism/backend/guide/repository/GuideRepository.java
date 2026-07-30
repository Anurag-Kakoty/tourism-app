package com.tourism.backend.guide.repository;

import com.tourism.backend.guide.entity.Guide;
import com.tourism.backend.guide.entity.Language;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<Guide> findAllByAvailableOrderByRatingDescNameAsc(Boolean available);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    List<Guide> findAllByProvidesTransportOrderByRatingDescNameAsc(Boolean providesTransport);

    @Query("""
            SELECT DISTINCT g
            FROM Guide g
            JOIN g.languages l
            WHERE l = :language
            ORDER BY g.rating DESC, g.name ASC
            """)
    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "languages"
    })
    List<Guide> findAllByLanguage(@Param("language") Language language);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);
}