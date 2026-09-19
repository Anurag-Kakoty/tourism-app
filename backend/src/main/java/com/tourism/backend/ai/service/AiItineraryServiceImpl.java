package com.tourism.backend.ai.service;

import com.tourism.backend.ai.client.AiModelClient;
import com.tourism.backend.ai.dto.AiCandidateContext;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.AiItineraryResponse;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.recommendation.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiItineraryServiceImpl
        implements AiItineraryService {

    private final CandidateService candidateService;

    private final RecommendationService recommendationService;

    private final AiModelClient aiModelClient;

    @Override
    public AiItineraryResponse generateItinerary(
            AiItineraryRequest request) {

        AiCandidateContext candidates =
                candidateService.getCandidates(request);

        RecommendedCandidateContext recommendations =
                recommendationService.recommend(
                        request,
                        candidates
                );

        /*
         * The AI model will receive:
         *
         * 1. User preferences
         * 2. A bounded set of recommended candidates
         *
         * It will return a structured AiGeneratedItinerary.
         */

        var generatedItinerary =
                aiModelClient.generateItinerary(
                        request,
                        recommendations
                );

        /*
         * Validation and conversion from the AI-generated
         * itinerary to AiItineraryResponse will be added next.
         */

        throw new UnsupportedOperationException(
                "AI itinerary validation is not implemented yet."
        );
    }
}