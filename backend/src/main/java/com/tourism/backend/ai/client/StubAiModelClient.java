package com.tourism.backend.ai.client;

import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class StubAiModelClient implements AiModelClient {

    @Override
    public AiGeneratedItinerary generateItinerary(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        return AiGeneratedItinerary.builder()
                .title("AI Generated Itinerary")
                .description(
                        "Temporary development response. "
                                + "The real AI model will be connected next."
                )
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfTravelers(
                        request.getNumberOfTravelers()
                )
                .estimatedBudget(request.getBudget())
                .destinationId(request.getDestinationId())
                .items(List.of())
                .build();
    }
}