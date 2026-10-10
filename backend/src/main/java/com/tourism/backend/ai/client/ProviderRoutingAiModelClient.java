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

    /**
     * Try Gemini first.
     *
     * If Gemini fails for any reason, fall back to Ollama.
     */
    private AiGeneratedItinerary generateWithGeminiWithFallback(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        try {

            log.info(
                    "Attempting AI itinerary generation using Gemini."
            );

            return geminiModelClient.generateItinerary(
                    request,
                    candidates
            );

        } catch (RuntimeException geminiException) {

            log.warn(
                    "Gemini itinerary generation failed. "
                            + "Falling back to Ollama.",
                    geminiException
            );

            try {

                log.info(
                        "Attempting AI itinerary generation using "
                                + "Ollama fallback."
                );

                AiGeneratedItinerary ollamaResult =
                        ollamaModelClient.generateItinerary(
                                request,
                                candidates
                        );

                log.info(
                        "Ollama fallback successfully generated "
                                + "the itinerary."
                );

                return ollamaResult;

            } catch (RuntimeException ollamaException) {

                log.error(
                        "Ollama fallback failed after Gemini "
                                + "itinerary generation failed.",
                        ollamaException
                );

                log.error(
                        "Original Gemini failure:",
                        geminiException
                );

                IllegalStateException combinedException =
                        new IllegalStateException(
                                "Both Gemini and Ollama itinerary "
                                        + "generation failed. "
                                        + "Gemini error: "
                                        + geminiException.getMessage()
                                        + "; Ollama error: "
                                        + ollamaException.getMessage(),
                                ollamaException
                        );

                combinedException.addSuppressed(
                        geminiException
                );

                throw combinedException;
            }
        }
    }
}