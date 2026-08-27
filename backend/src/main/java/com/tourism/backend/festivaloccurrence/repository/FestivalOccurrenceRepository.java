package com.tourism.backend.festivaloccurrence.repository;

import com.tourism.backend.festivaloccurrence.entity.FestivalOccurrence;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface FestivalOccurrenceRepository
        extends JpaRepository<FestivalOccurrence, Long>,
        JpaSpecificationExecutor<FestivalOccurrence> {

    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    Optional<FestivalOccurrence> findWithFestivalAndStateById(
            Long id
    );

    boolean existsByFestival_IdAndState_IdAndYear(
            Long festivalId,
            Long stateId,
            Integer year
    );

    boolean existsByFestival_IdAndState_IdAndYearAndIdNot(
            Long festivalId,
            Long stateId,
            Integer year,
            Long id
    );

    @Override
    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    List<FestivalOccurrence> findAll(
            Specification<FestivalOccurrence> specification,
            Sort sort
    );
}