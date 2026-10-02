package com.tourism.backend.ai.client;

import com.tourism.backend.ai.config.AiProperties;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

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

                log.error(
                        "Gemini itinerary generation failed and is not considered temporary.",
                        exception
                );

                throw exception;
            }

            log.warn(
                    "Gemini temporarily unavailable. Falling back to Ollama.",
                    exception
            );

            try {

                return ollamaModelClient.generateItinerary(
                        request,
                        candidates
                );

            } catch (RuntimeException ollamaException) {

                log.error(
                        "Ollama fallback also failed.",
                        ollamaException
                );

                throw new IllegalStateException(
                        "Both Gemini and Ollama itinerary generation failed.",
                        ollamaException
                );
            }
        }
    }

    private boolean isTemporaryProviderFailure(
            RuntimeException exception) {

        Throwable current = exception;

        while (current != null) {

            /*
             * Spring's HTTP exception gives us the actual
             * HTTP status instead of forcing us to inspect
             * the exception message.
             */
            if (current instanceof HttpStatusCodeException httpException) {

                int status =
                        httpException
                                .getStatusCode()
                                .value();

                if (status == 429
                        || status == 500
                        || status == 502
                        || status == 503
                        || status == 504) {

                    return true;
                }
            }

            /*
             * Network-level failures are also appropriate
             * candidates for provider fallback.
             */
            if (current instanceof ResourceAccessException) {

                return true;
            }

            /*
             * Keep message-based detection as a secondary
             * fallback because GeminiModelClient currently
             * wraps the original exception.
             */
            String message =
                    current.getMessage();

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
                        "too many requests")
                        || normalizedMessage.contains(
                        "rate limit")
                        || normalizedMessage.contains(
                        "connection refused")
                        || normalizedMessage.contains(
                        "connect timed out")
                        || normalizedMessage.contains(
                        "read timed out")
                        || normalizedMessage.contains(
                        "socket timeout")) {

                    return true;
                }
            }

            current =
                    current.getCause();
        }

        return false;
    }
}