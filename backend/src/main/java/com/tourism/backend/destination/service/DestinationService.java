package com.tourism.backend.destination.service;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;

import java.util.List;

public interface DestinationService {

    DestinationResponse createDestination(DestinationRequest request);

    List<DestinationResponse> getAllDestinations(String state);

    DestinationResponse getDestinationById(Long id);

    DestinationResponse updateDestination(Long id, DestinationRequest request);

    void deleteDestination(Long id);

}