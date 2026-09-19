package com.tourism.backend.ai.recommendation;

import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.dto.AiCandidateContext;

public interface RecommendationService {

    RecommendedCandidateContext recommend(
            AiItineraryRequest request,
            AiCandidateContext candidates
    );
}