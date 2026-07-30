package com.tourism.backend.accommodation.repository;

import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.entity.AccommodationType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Override
    @EntityGraph(attributePaths = "destination")
    Optional<Accommodation> findById(Long id);

    @EntityGraph(attributePaths = "destination")
    List<Accommodation> findAllBy();

    @EntityGraph(attributePaths = "destination")
    List<Accommodation> findAllByDestination_Id(Long destinationId);

    @EntityGraph(attributePaths = "destination")
    List<Accommodation> findAllByType(AccommodationType type);

    @EntityGraph(attributePaths = "destination")
    List<Accommodation> findAllByAvailable(Boolean available);

    @EntityGraph(attributePaths = "destination")
    List<Accommodation> findAllByDestination_IdAndType(
            Long destinationId,
            AccommodationType type
    );

    boolean existsByNameIgnoreCaseAndDestination_Id(
            String name,
            Long destinationId
    );

    boolean existsByNameIgnoreCaseAndDestination_IdAndIdNot(
            String name,
            Long destinationId,
            Long id
    );
}