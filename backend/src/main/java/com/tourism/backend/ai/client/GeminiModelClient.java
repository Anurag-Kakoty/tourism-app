package com.tourism.backend.ai.client;

import com.tourism.backend.ai.config.AiProperties;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiModelClient implements AiModelClient {

    private static final String GEMINI_BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    private static final DateTimeFormatter TIME_12_HOUR_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");

    private static final DateTimeFormatter TIME_12_HOUR_FORMATTER_NO_LEADING_ZERO =
            DateTimeFormatter.ofPattern("h:mm a");

    private static final DateTimeFormatter TIME_24_HOUR_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter TIME_24_HOUR_SECONDS_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient.Builder restClientBuilder;

    @Override
    public AiGeneratedItinerary generateItinerary(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        if (aiProperties.getGemini().getApiKey() == null
                || aiProperties.getGemini().getApiKey().isBlank()) {

            throw new IllegalStateException(
                    "Gemini API key is not configured."
            );
        }

        try {
            String candidateJson =
                    objectMapper.writeValueAsString(candidates);

            String prompt =
                    buildPrompt(
                            request,
                            candidateJson
                    );

            Map<String, Object> requestBody =
                    Map.of(
                            "contents",
                            List.of(
                                    Map.of(
                                            "parts",
                                            List.of(
                                                    Map.of(
                                                            "text",
                                                            prompt
                                                    )
                                            )
                                    )
                            ),
                            "generationConfig",
                            Map.of(
                                    "responseMimeType",
                                    "application/json",
                                    "responseSchema",
                                    buildItinerarySchema()
                            )
                    );

            RestClient client =
                    restClientBuilder.build();

            String url =
                    GEMINI_BASE_URL
                            + aiProperties.getGemini().getModel()
                            + ":generateContent";

            String responseBody =
                    client.post()
                            .uri(url)
                            .header(
                                    "x-goog-api-key",
                                    aiProperties
                                            .getGemini()
                                            .getApiKey()
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(requestBody)
                            .retrieve()
                            .body(String.class);

            return parseResponse(responseBody);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Gemini itinerary generation failed.",
                    exception
            );
        }
    }

    private String buildPrompt(
            AiItineraryRequest request,
            String candidateJson) {

        return """
                You are an AI tourism itinerary planner.

                Create a practical travel itinerary using ONLY
                the candidates supplied by the application.

                STRICT RULES:

                1. Never invent database IDs.
                2. Never create an attraction, accommodation,
                   restaurant, guide, transport provider, or festival
                   that is not present in the supplied candidates.
                3. Every referenceId must exactly match an ID supplied
                   in the candidate context.
                4. For FESTIVAL activities, referenceId must be the
                   festivalId supplied by the candidate.
                5. Respect the requested destination.
                6. Respect the requested start and end dates.
                7. Respect the requested number of travelers.
                8. Consider the requested budget.
                9. Consider the selected experiences.
                10. Create a realistic sequence of activities.
                11. Do not invent prices, ratings, locations, or
                    other database information.
                12. Return only the requested JSON structure.
                13. For every activity item, return the "time" field
                    strictly in 24-hour HH:mm:ss format.
                    Example: "08:00:00".
                14. Do not return AM/PM in the time field.

                TRAVEL REQUEST:

                Start date: %s
                End date: %s
                Number of travelers: %d
                Budget: %s
                Destination ID: %d
                Selected experience IDs: %s
                Festival ID: %s

                CANDIDATES:

                %s

                Create an itinerary distributed across the requested
                travel days.

                Accommodation may be included where appropriate.
                Restaurants may be included for meals.
                Guides and transport may be included when useful.
                Festivals should only be included when present in
                the supplied festival candidates.

                Use only the supplied candidates.
                Return the itinerary using the required JSON schema.
                """.formatted(
                request.getStartDate(),
                request.getEndDate(),
                request.getNumberOfTravelers(),
                request.getBudget(),
                request.getDestinationId(),
                request.getExperienceIds(),
                request.getFestivalId(),
                candidateJson
        );
    }

    private Map<String, Object> buildItinerarySchema() {

        return Map.of(
                "type", "object",

                "properties", Map.of(

                        "title",
                        Map.of(
                                "type",
                                "string"
                        ),

                        "description",
                        Map.of(
                                "type",
                                "string"
                        ),

                        "startDate",
                        Map.of(
                                "type",
                                "string"
                        ),

                        "endDate",
                        Map.of(
                                "type",
                                "string"
                        ),

                        "numberOfTravelers",
                        Map.of(
                                "type",
                                "integer"
                        ),

                        "estimatedBudget",
                        Map.of(
                                "type",
                                "number"
                        ),

                        "destinationId",
                        Map.of(
                                "type",
                                "integer"
                        ),

                        "items",
                        Map.of(
                                "type",
                                "array",

                                "items",
                                Map.of(
                                        "type",
                                        "object",

                                        "properties",
                                        Map.of(

                                                "dayNumber",
                                                Map.of(
                                                        "type",
                                                        "integer"
                                                ),

                                                "activityOrder",
                                                Map.of(
                                                        "type",
                                                        "integer"
                                                ),

                                                "time",
                                                Map.of(
                                                        "type",
                                                        "string",
                                                        "description",
                                                        "Time in 24-hour HH:mm:ss format."
                                                ),

                                                "activityType",
                                                Map.of(
                                                        "type",
                                                        "string",
                                                        "enum",
                                                        List.of(
                                                                "ATTRACTION",
                                                                "ACCOMMODATION",
                                                                "RESTAURANT",
                                                                "GUIDE",
                                                                "TRANSPORT",
                                                                "FESTIVAL"
                                                        )
                                                ),

                                                "referenceId",
                                                Map.of(
                                                        "type",
                                                        "integer"
                                                ),

                                                "notes",
                                                Map.of(
                                                        "type",
                                                        "string"
                                                )
                                        ),

                                        "required",
                                        List.of(
                                                "dayNumber",
                                                "activityOrder",
                                                "time",
                                                "activityType",
                                                "referenceId",
                                                "notes"
                                        )
                                )
                        )
                ),

                "required",
                List.of(
                        "title",
                        "description",
                        "startDate",
                        "endDate",
                        "numberOfTravelers",
                        "estimatedBudget",
                        "destinationId",
                        "items"
                )
        );
    }

    private AiGeneratedItinerary parseResponse(
            String responseBody) {

        try {
            JsonNode root =
                    objectMapper.readTree(responseBody);

            JsonNode candidates =
                    root.path("candidates");

            if (!candidates.isArray()
                    || candidates.isEmpty()) {

                throw new IllegalStateException(
                        "Gemini response did not contain candidates."
                );
            }

            JsonNode content =
                    candidates
                            .get(0)
                            .path("content");

            JsonNode parts =
                    content.path("parts");

            if (!parts.isArray()
                    || parts.isEmpty()) {

                throw new IllegalStateException(
                        "Gemini response did not contain content."
                );
            }

            JsonNode text =
                    parts
                            .get(0)
                            .path("text");

            if (text.isMissingNode()
                    || text.asText().isBlank()) {

                throw new IllegalStateException(
                        "Gemini response did not contain itinerary JSON."
                );
            }

            JsonNode itinerary =
                    objectMapper.readTree(
                            text.asText()
                    );

            return mapGeneratedItinerary(itinerary);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to parse Gemini itinerary response.",
                    exception
            );
        }
    }

    private AiGeneratedItinerary mapGeneratedItinerary(
            JsonNode itinerary) {

        List<AiGeneratedItinerary.GeneratedItem> items =
                new ArrayList<>();

        JsonNode itemArray =
                itinerary.path("items");

        if (itemArray.isArray()) {

            for (JsonNode item : itemArray) {

                items.add(
                        AiGeneratedItinerary.GeneratedItem
                                .builder()
                                .dayNumber(
                                        item
                                                .path("dayNumber")
                                                .asInt()
                                )
                                .activityOrder(
                                        item
                                                .path("activityOrder")
                                                .asInt()
                                )
                                .time(
                                        parseTime(
                                                item
                                                        .path("time")
                                                        .asText()
                                        )
                                )
                                .activityType(
                                        com.tourism.backend.itinerary.entity
                                                .ActivityType
                                                .valueOf(
                                                        item
                                                                .path(
                                                                        "activityType"
                                                                )
                                                                .asText()
                                                )
                                )
                                .referenceId(
                                        item
                                                .path("referenceId")
                                                .asLong()
                                )
                                .notes(
                                        item
                                                .path("notes")
                                                .asText()
                                )
                                .build()
                );
            }
        }

        return AiGeneratedItinerary.builder()
                .title(
                        itinerary
                                .path("title")
                                .asText()
                )
                .description(
                        itinerary
                                .path("description")
                                .asText()
                )
                .startDate(
                        LocalDate.parse(
                                itinerary
                                        .path("startDate")
                                        .asText()
                        )
                )
                .endDate(
                        LocalDate.parse(
                                itinerary
                                        .path("endDate")
                                        .asText()
                        )
                )
                .numberOfTravelers(
                        itinerary
                                .path("numberOfTravelers")
                                .asInt()
                )
                .estimatedBudget(
                        itinerary
                                .path("estimatedBudget")
                                .decimalValue()
                )
                .destinationId(
                        itinerary
                                .path("destinationId")
                                .asLong()
                )
                .items(items)
                .build();
    }

    private LocalTime parseTime(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Generated itinerary contains an empty time."
            );
        }

        String normalizedValue =
                value.trim();

        /*
         * Standard ISO time:
         *
         * 08:00:00
         */
        try {
            return LocalTime.parse(normalizedValue);
        } catch (DateTimeParseException ignored) {
            // Try the other supported formats below.
        }

        /*
         * 24-hour format without seconds:
         *
         * 08:00
         */
        try {
            return LocalTime.parse(
                    normalizedValue,
                    TIME_24_HOUR_FORMATTER
            );
        } catch (DateTimeParseException ignored) {
            // Try the 12-hour format below.
        }

        /*
         * 12-hour format:
         *
         * 08:00 AM
         */
        try {
            return LocalTime.parse(
                    normalizedValue.toUpperCase(),
                    TIME_12_HOUR_FORMATTER
            );
        } catch (DateTimeParseException ignored) {
            // Try without a leading zero.
        }

        /*
         * 12-hour format without leading zero:
         *
         * 8:00 AM
         */
        try {
            return LocalTime.parse(
                    normalizedValue.toUpperCase(),
                    TIME_12_HOUR_FORMATTER_NO_LEADING_ZERO
            );
        } catch (DateTimeParseException ignored) {
            // All supported formats failed.
        }

        throw new IllegalArgumentException(
                "Unsupported generated time format: "
                        + value
                        + ". Expected HH:mm:ss, HH:mm, or h:mm AM/PM."
        );
    }
}