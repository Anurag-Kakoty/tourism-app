package com.tourism.backend.festivaloccurrence.service;

import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.festival.entity.Festival;
import com.tourism.backend.festival.repository.FestivalRepository;
import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceRequest;
import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceResponse;
import com.tourism.backend.festivaloccurrence.entity.FestivalOccurrence;
import com.tourism.backend.festivaloccurrence.mapper.FestivalOccurrenceMapper;
import com.tourism.backend.festivaloccurrence.repository.FestivalOccurrenceRepository;
import com.tourism.backend.festivaloccurrence.specification.FestivalOccurrenceSpecification;
import com.tourism.backend.state.entity.State;
import com.tourism.backend.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FestivalOccurrenceServiceImpl
        implements FestivalOccurrenceService {

    private final FestivalOccurrenceRepository occurrenceRepository;
    private final FestivalRepository festivalRepository;
    private final StateRepository stateRepository;
    private final FestivalOccurrenceMapper mapper;

    @Override
    public FestivalOccurrenceResponse create(
            FestivalOccurrenceRequest request) {

        log.info("Creating festival occurrence");

        validateDates(request);

        if (occurrenceRepository.existsByFestival_IdAndState_IdAndYear(
                request.getFestivalId(),
                request.getStateId(),
                request.getYear())) {

            throw new DuplicateResourceException(
                    "Festival occurrence already exists."
            );
        }

        Festival festival = getFestival(request.getFestivalId());

        State state = getState(request.getStateId());

        FestivalOccurrence occurrence =
                mapper.toEntity(request, festival, state);

        FestivalOccurrence saved =
                occurrenceRepository.save(occurrence);

        log.info(
                "Festival occurrence created with id {}",
                saved.getId()
        );

        return mapper.toResponse(saved);
    }

    @Override
    public FestivalOccurrenceResponse update(
            Long id,
            FestivalOccurrenceRequest request) {

        log.info(
                "Updating festival occurrence {}",
                id
        );

        validateDates(request);

        FestivalOccurrence occurrence =
                getOccurrence(id);

        if (occurrenceRepository
                .existsByFestival_IdAndState_IdAndYearAndIdNot(
                        request.getFestivalId(),
                        request.getStateId(),
                        request.getYear(),
                        id)) {

            throw new DuplicateResourceException(
                    "Festival occurrence already exists."
            );
        }

        Festival festival =
                getFestival(request.getFestivalId());

        State state =
                getState(request.getStateId());

        mapper.updateEntity(
                occurrence,
                request,
                festival,
                state
        );

        FestivalOccurrence updated =
                occurrenceRepository.save(occurrence);

        log.info(
                "Festival occurrence updated {}",
                id
        );

        return mapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FestivalOccurrenceResponse getById(Long id) {

        FestivalOccurrence occurrence =
                occurrenceRepository
                        .findWithFestivalAndStateById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Festival occurrence not found with id: "
                                                + id
                                )
                        );

        return mapper.toResponse(occurrence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FestivalOccurrenceResponse> getAll(
            Long stateId,
            Integer year) {

        log.info(
                "Fetching festival occurrences with filters: " +
                        "stateId={}, year={}",
                stateId,
                year
        );

        Specification<FestivalOccurrence> specification =
                Specification
                        .where(
                                FestivalOccurrenceSpecification
                                        .hasStateId(stateId)
                        )
                        .and(
                                FestivalOccurrenceSpecification
                                        .hasYear(year)
                        );

        return occurrenceRepository
                .findAll(
                        specification,
                        Sort.by(
                                Sort.Order.asc("startDate"),
                                Sort.Order.asc("year")
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FestivalOccurrenceResponse> getUpcoming() {

        log.info("Fetching upcoming festival occurrences");

        Specification<FestivalOccurrence> specification =
                FestivalOccurrenceSpecification
                        .startsOnOrAfter(LocalDate.now());

        return occurrenceRepository
                .findAll(
                        specification,
                        Sort.by(
                                Sort.Order.asc("startDate")
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        FestivalOccurrence occurrence =
                getOccurrence(id);

        occurrenceRepository.delete(occurrence);

        log.info(
                "Festival occurrence deleted {}",
                id
        );
    }

    private Festival getFestival(Long id) {

        return festivalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Festival not found with id: " + id
                        )
                );
    }

    private State getState(Long id) {

        return stateRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "State not found with id: " + id
                        )
                );
    }

    private FestivalOccurrence getOccurrence(Long id) {

        return occurrenceRepository
                .findWithFestivalAndStateById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Festival occurrence not found with id: "
                                        + id
                        )
                );
    }

    private void validateDates(
            FestivalOccurrenceRequest request) {

        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }
    }
}