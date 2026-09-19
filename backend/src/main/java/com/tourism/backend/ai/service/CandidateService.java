package com.tourism.backend.ai.service;

import com.tourism.backend.ai.dto.AiCandidateContext;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.candidate.*;

import java.util.List;

public interface CandidateService {

    AiCandidateContext getCandidates(
            AiItineraryRequest request
    );

    List<AttractionCandidate> getAttractions(
            AiItineraryRequest request
    );

    List<AccommodationCandidate> getAccommodations(
            AiItineraryRequest request
    );

    List<RestaurantCandidate> getRestaurants(
            AiItineraryRequest request
    );

    List<GuideCandidate> getGuides(
            AiItineraryRequest request
    );

    List<TransportCandidate> getTransport(
            AiItineraryRequest request
    );

    List<FestivalCandidate> getFestivals(
            AiItineraryRequest request
    );
}