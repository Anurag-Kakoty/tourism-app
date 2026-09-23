package com.tourism.backend.ai.client;

import com.tourism.backend.ai.config.AiProperties;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class ProviderRoutingAiModelClient
        implements AiModelClient {

    private final AiProperties aiProperties;
    private final OpenAiModelClient openAiModelClient;
    private final GeminiModelClient geminiModelClient;
    private final OllamaModelClient ollamaModelClient;

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
                    generateWithGeminiWithFallback(
                            request,
                            candidates
                    );

            case OLLAMA ->
                    ollamaModelClient.generateItinerary(
                            request,
                            candidates
                    );
        };
    }

    private AiGeneratedItinerary generateWithGeminiWithFallback(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        try {

            return geminiModelClient.generateItinerary(
                    request,
                    candidates
            );

        } catch (RuntimeException exception) {

            if (!isTemporaryProviderFailure(exception)) {
                throw exception;
            }

            log.warn(
                    "Gemini temporarily unavailable. Falling back to Ollama."
            );

            return ollamaModelClient.generateItinerary(
                    request,
                    candidates
            );
        }
    }

    private boolean isTemporaryProviderFailure(
            RuntimeException exception) {

        Throwable current = exception;

        while (current != null) {

            String message = current.getMessage();

            if (message != null) {

                String normalizedMessage =
                        message.toLowerCase();

                if (normalizedMessage.contains("503")
                        || normalizedMessage.contains(
                        "service unavailable")
                        || normalizedMessage.contains(
                        "temporarily unavailable")
                        || normalizedMessage.contains(
                        "high demand")
                        || normalizedMessage.contains(
                        "connection refused")
                        || normalizedMessage.contains(
                        "connect timed out")
                        || normalizedMessage.contains(
                        "read timed out")) {

                    return true;
                }
            }

            current = current.getCause();
        }

        return false;
    }
}