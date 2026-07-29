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
import com.tourism.backend.state.entity.State;
import com.tourism.backend.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

        log.info("Festival occurrence created with id {}", saved.getId());

        return mapper.toResponse(saved);
    }

    @Override
    public FestivalOccurrenceResponse update(
            Long id,
            FestivalOccurrenceRequest request) {

        log.info("Updating festival occurrence {}", id);

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

        Festival festival = getFestival(request.getFestivalId());

        State state = getState(request.getStateId());

        mapper.updateEntity(
                occurrence,
                request,
                festival,
                state);

        FestivalOccurrence updated =
                occurrenceRepository.save(occurrence);

        log.info("Festival occurrence updated {}", id);

        return mapper.toResponse(updated);
    }

    @Override
    public FestivalOccurrenceResponse getById(Long id) {

        return mapper.toResponse(getOccurrence(id));
    }

    @Override
    public List<FestivalOccurrenceResponse> getAll() {

        return occurrenceRepository.findAllBy()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<FestivalOccurrenceResponse> getByState(Long stateId) {

        return occurrenceRepository.findAllByState_Id(stateId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<FestivalOccurrenceResponse> getByYear(Integer year) {

        return occurrenceRepository.findAllByYear(year)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<FestivalOccurrenceResponse> getUpcoming() {

        return occurrenceRepository
                .findAllByStartDateGreaterThanEqualOrderByStartDate(
                        LocalDate.now())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        occurrenceRepository.delete(getOccurrence(id));

        log.info("Festival occurrence deleted {}", id);
    }

    @Override
    public List<FestivalOccurrenceResponse> getByStateAndYear(
            Long stateId,
            Integer year) {

        return occurrenceRepository
                .findAllByState_IdAndYear(stateId, year)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private Festival getFestival(Long id) {

        return festivalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Festival not found with id: " + id));
    }

    private State getState(Long id) {

        return stateRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "State not found with id: " + id));
    }

    private FestivalOccurrence getOccurrence(Long id) {

        return occurrenceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Festival occurrence not found with id: " + id));
    }

    private void validateDates(
            FestivalOccurrenceRequest request) {

        if (request.getEndDate().isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date.");
        }
    }
}