package com.tourism.backend.ai.dto;

import com.tourism.backend.ai.dto.candidate.AccommodationCandidate;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import com.tourism.backend.ai.dto.candidate.FestivalCandidate;
import com.tourism.backend.ai.dto.candidate.GuideCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;
import com.tourism.backend.ai.dto.candidate.TransportCandidate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AiCandidateContext {

    private List<AttractionCandidate> attractions;

    private List<AccommodationCandidate> accommodations;

    private List<RestaurantCandidate> restaurants;

    private List<GuideCandidate> guides;

    private List<TransportCandidate> transport;

    private List<FestivalCandidate> festivals;
}