package com.tourism.backend.itinerary.service;

import com.tourism.backend.itinerary.dto.ItineraryItemRequest;
import com.tourism.backend.itinerary.dto.ItineraryResponse;
import com.tourism.backend.itinerary.dto.ItineraryRequest;

import java.util.List;

public interface ItineraryService {

    ItineraryResponse createItinerary(
            ItineraryRequest request);

    ItineraryResponse updateItinerary(
            Long id,
            ItineraryRequest request);

    ItineraryResponse getItineraryById(Long id);

    List<ItineraryResponse> getAllItineraries();

    List<ItineraryResponse> getItinerariesByDestination(
            Long destinationId);

    void deleteItinerary(Long id);

    ItineraryResponse addItem(
            Long itineraryId,
            ItineraryItemRequest request);

    ItineraryResponse updateItem(
            Long itineraryId,
            Long itemId,
            ItineraryItemRequest request);

    ItineraryResponse deleteItem(
            Long itineraryId,
            Long itemId);
}