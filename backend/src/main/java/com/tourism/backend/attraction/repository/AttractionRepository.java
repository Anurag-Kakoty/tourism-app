package com.tourism.backend.attraction.repository;

import com.tourism.backend.attraction.entity.Attraction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    Optional<Attraction> findByNameIgnoreCaseAndDestination_Id(
            String name,
            Long destinationId
    );

    @EntityGraph(attributePaths = {"destination", "destination.state", "tags", "experiences"})
    Optional<Attraction> findWithDestinationById(Long id);

    @EntityGraph(attributePaths = {"destination", "destination.state", "tags", "experiences"})
    List<Attraction> findAllByOrderByDisplayOrderAscNameAsc();

    @EntityGraph(attributePaths = {"destination", "destination.state", "tags", "experiences"})
    List<Attraction> findAllByDestination_IdOrderByDisplayOrderAscNameAsc(Long destinationId);

    @EntityGraph(attributePaths = {"destination", "destination.state", "tags", "experiences"})
    List<Attraction> findAllByFeaturedTrueOrderByDisplayOrderAscNameAsc();
}