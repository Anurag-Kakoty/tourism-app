package com.tourism.backend.accommodation.service;

import com.tourism.backend.accommodation.dto.AccommodationRequest;
import com.tourism.backend.accommodation.dto.AccommodationResponse;
import com.tourism.backend.accommodation.entity.AccommodationType;

import java.util.List;

public interface AccommodationService {

    AccommodationResponse create(AccommodationRequest request);

    AccommodationResponse update(
            Long id,
            AccommodationRequest request
    );

    AccommodationResponse getById(Long id);

    List<AccommodationResponse> getAll();

    List<AccommodationResponse> getByDestination(Long destinationId);

    List<AccommodationResponse> getByType(AccommodationType type);

    List<AccommodationResponse> getByAvailable(Boolean available);

    List<AccommodationResponse> getByDestinationAndType(
            Long destinationId,
            AccommodationType type
    );

    void delete(Long id);
}