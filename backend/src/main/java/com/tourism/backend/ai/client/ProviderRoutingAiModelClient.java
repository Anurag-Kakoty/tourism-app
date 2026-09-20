package com.tourism.backend.ai.client;

import com.tourism.backend.ai.config.AiProperties;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class ProviderRoutingAiModelClient
        implements AiModelClient {

    private final AiProperties aiProperties;
    private final OpenAiModelClient openAiModelClient;
    private final GeminiModelClient geminiModelClient;

    @Override
    public AiGeneratedItinerary generateItinerary(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        return switch (aiProperties.getProvider()) {

            case OPENAI ->
                    openAiModelClient.generateItinerary(
                            request,
                            candidates
                    );

            case GEMINI ->
                    geminiModelClient.generateItinerary(
                            request,
                            candidates
                    );
        };
    }
}