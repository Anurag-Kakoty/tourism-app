package com.tourism.backend.destination.repository;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.entity.DestinationType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository
        extends JpaRepository<Destination, Long>,
        JpaSpecificationExecutor<Destination> {

    @Override
    @EntityGraph(attributePaths = "state")
    Optional<Destination> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "state")
    List<Destination> findAll(
            Specification<Destination> specification,
            Sort sort
    );

    @EntityGraph(attributePaths = "state")
    List<Destination> findAllByOrderByDisplayOrderAscNameAsc();

    @EntityGraph(attributePaths = "state")
    List<Destination> findAllByState_IdOrderByDisplayOrderAscNameAsc(
            Long stateId
    );

    @EntityGraph(attributePaths = "state")
    List<Destination> findAllByTypeOrderByDisplayOrderAscNameAsc(
            DestinationType type
    );

    @EntityGraph(attributePaths = "state")
    List<Destination> findAllByFeaturedTrueOrderByDisplayOrderAsc();

    @EntityGraph(attributePaths = "state")
    List<Destination> findAllByPopularTrueOrderByDisplayOrderAsc();

    boolean existsByNameIgnoreCaseAndState_Id(
            String name,
            Long stateId
    );

    boolean existsByNameIgnoreCaseAndState_IdAndIdNot(
            String name,
            Long stateId,
            Long id
    );
}