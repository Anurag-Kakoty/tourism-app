package com.tourism.backend.festivaloccurrence.service;

import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceRequest;
import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceResponse;

import java.util.List;

public interface FestivalOccurrenceService {

    FestivalOccurrenceResponse create(
            FestivalOccurrenceRequest request
    );

    FestivalOccurrenceResponse update(
            Long id,
            FestivalOccurrenceRequest request
    );

    FestivalOccurrenceResponse getById(Long id);

    List<FestivalOccurrenceResponse> getAll(
            Long stateId,
            Integer year
    );

    List<FestivalOccurrenceResponse> getUpcoming();

    void delete(Long id);
}