package com.tourism.backend.destination.service;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.DestinationType;

import java.util.List;

public interface DestinationService {

    DestinationResponse create(DestinationRequest request);

    DestinationResponse update(Long id, DestinationRequest request);

    DestinationResponse getById(Long id);

    List<DestinationResponse> getAll();

    List<DestinationResponse> getByState(Long stateId);

    List<DestinationResponse> getByType(DestinationType type);

    List<DestinationResponse> getFeatured();

    List<DestinationResponse> getPopular();

    void delete(Long id);
}