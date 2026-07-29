package com.tourism.backend.festivaloccurrence.repository;

import com.tourism.backend.festivaloccurrence.entity.FestivalOccurrence;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FestivalOccurrenceRepository
        extends JpaRepository<FestivalOccurrence, Long> {

    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    List<FestivalOccurrence> findAllBy();

    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    List<FestivalOccurrence> findAllByState_Id(Long stateId);

    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    List<FestivalOccurrence> findAllByYear(Integer year);

    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    List<FestivalOccurrence> findAllByStartDateGreaterThanEqualOrderByStartDate(
            LocalDate date
    );

    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    List<FestivalOccurrence> findAllByState_IdAndYear(
            Long stateId,
            Integer year
    );

    @EntityGraph(attributePaths = {
            "festival",
            "state"
    })
    Optional<FestivalOccurrence> findById(Long id);

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
}