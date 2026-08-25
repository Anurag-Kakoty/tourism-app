package com.tourism.backend.accommodation.repository;

import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.entity.AccommodationType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AccommodationRepository
        extends JpaRepository<Accommodation, Long>,
        JpaSpecificationExecutor<Accommodation> {

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    Optional<Accommodation> findWithDestinationById(Long id);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Accommodation> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Accommodation> findAllByDestination_IdOrderByNameAsc(
            Long destinationId
    );

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Accommodation> findAllByTypeOrderByNameAsc(
            AccommodationType type
    );

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Accommodation> findAllByAvailableOrderByNameAsc(
            Boolean available
    );

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Accommodation> findAllByDestination_IdAndTypeOrderByNameAsc(
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