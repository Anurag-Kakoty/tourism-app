package com.tourism.backend.ai.dto;

import com.tourism.backend.ai.dto.candidate.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AiCandidateResponse {

    private Long destinationId;

    private List<AttractionCandidate> attractions;

    private List<AccommodationCandidate> accommodations;

    private List<RestaurantCandidate> restaurants;

    private List<GuideCandidate> guides;

    private List<TransportCandidate> transport;

    private List<FestivalCandidate> festivals;
}