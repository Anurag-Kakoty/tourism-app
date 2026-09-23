package com.tourism.backend.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.backend.ai.config.AiProperties;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.itinerary.entity.ActivityType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OllamaModelClient implements AiModelClient {

    private final ObjectMapper objectMapper;
    private final RestClient.Builder restClientBuilder;
    private final AiProperties aiProperties;

    @Override
    public AiGeneratedItinerary generateItinerary(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        AiProperties.Ollama ollama =
                aiProperties.getOllama();

        RestClient restClient =
                restClientBuilder
                        .baseUrl(ollama.getBaseUrl())
                        .build();

        String prompt = buildPrompt(request, candidates);

        Map<String, Object> requestBody = new LinkedHashMap<>();

        requestBody.put("model", ollama.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);
        requestBody.put("format", buildItinerarySchema());

        String response =
                restClient
                        .post()
                        .uri("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(String.class);

        try {
            JsonNode root =
                    objectMapper.readTree(response);

            JsonNode generatedResponse =
                    root.get("response");

            if (generatedResponse == null
                    || generatedResponse.isNull()
                    || generatedResponse.asText().isBlank()) {

                throw new IllegalStateException(
                        "Ollama returned an empty response."
                );
            }

            JsonNode itineraryJson =
                    objectMapper.readTree(
                            generatedResponse.asText()
                    );

            return mapGeneratedItinerary(itineraryJson);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to parse Ollama itinerary response.",
                    exception
            );
        }
    }

    private String buildPrompt(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        try {

            Map<String, Object> promptData =
                    new LinkedHashMap<>();

            promptData.put("request", request);
            promptData.put("candidates", candidates);

            String candidateJson =
                    objectMapper.writeValueAsString(promptData);

            return """
                    You are an itinerary planning assistant for an Indian tourism application.

                    Generate a practical travel itinerary using ONLY the candidates supplied below.

                    IMPORTANT RULES:

                    1. Do not invent attractions, accommodations, restaurants, guides,
                       transport providers, festivals, or IDs.

                    2. Every referenceId must exactly match an ID from the supplied candidates.

                    3. Every activityType must correspond to the correct candidate category.

                    4. Respect the requested destination.

                    5. Respect the requested start date and end date.

                    6. The itinerary should contain sensible activities distributed across
                       the requested number of days.

                    7. Consider the requested budget when selecting activities,
                       accommodation, restaurants, guides, and transport.

                    8. Use festival information when a relevant festival is available.

                    9. Avoid unnecessary repetition of the same attraction.

                    10. Times must ALWAYS use 24-hour HH:mm:ss format.
                        Do not use AM/PM.

                    11. Return ONLY valid JSON matching the supplied schema.
                        Do not include Markdown.
                        Do not include explanations outside the JSON.

                    USER REQUEST AND AVAILABLE CANDIDATES:

                    %s
                    """.formatted(candidateJson);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to build Ollama prompt.",
                    exception
            );
        }
    }

    private Map<String, Object> buildItinerarySchema() {

        Map<String, Object> itemProperties =
                new LinkedHashMap<>();

        itemProperties.put(
                "dayNumber",
                Map.of(
                        "type", "integer"
                )
        );

        itemProperties.put(
                "activityOrder",
                Map.of(
                        "type", "integer"
                )
        );

        itemProperties.put(
                "time",
                Map.of(
                        "type", "string"
                )
        );

        itemProperties.put(
                "activityType",
                Map.of(
                        "type", "string",
                        "enum", List.of(
                                "ATTRACTION",
                                "ACCOMMODATION",
                                "RESTAURANT",
                                "GUIDE",
                                "TRANSPORT",
                                "FESTIVAL"
                        )
                )
        );

        itemProperties.put(
                "referenceId",
                Map.of(
                        "type", "integer"
                )
        );

        itemProperties.put(
                "notes",
                Map.of(
                        "type", "string"
                )
        );

        Map<String, Object> itemSchema =
                new LinkedHashMap<>();

        itemSchema.put(
                "type",
                "object"
        );

        itemSchema.put(
                "properties",
                itemProperties
        );

        itemSchema.put(
                "required",
                List.of(
                        "dayNumber",
                        "activityOrder",
                        "time",
                        "activityType",
                        "referenceId",
                        "notes"
                )
        );

        Map<String, Object> itineraryProperties =
                new LinkedHashMap<>();

        itineraryProperties.put(
                "title",
                Map.of(
                        "type", "string"
                )
        );

        itineraryProperties.put(
                "description",
                Map.of(
                        "type", "string"
                )
        );

        itineraryProperties.put(
                "startDate",
                Map.of(
                        "type", "string"
                )
        );

        itineraryProperties.put(
                "endDate",
                Map.of(
                        "type", "string"
                )
        );

        itineraryProperties.put(
                "numberOfTravelers",
                Map.of(
                        "type", "integer"
                )
        );

        itineraryProperties.put(
                "estimatedBudget",
                Map.of(
                        "type", "number"
                )
        );

        itineraryProperties.put(
                "destinationId",
                Map.of(
                        "type", "integer"
                )
        );

        itineraryProperties.put(
                "items",
                Map.of(
                        "type", "array",
                        "items", itemSchema
                )
        );

        Map<String, Object> schema =
                new LinkedHashMap<>();

        schema.put(
                "type",
                "object"
        );

        schema.put(
                "properties",
                itineraryProperties
        );

        schema.put(
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

        return schema;
    }

    private AiGeneratedItinerary mapGeneratedItinerary(
            JsonNode json) {

        List<AiGeneratedItinerary.GeneratedItem> items =
                new ArrayList<>();

        JsonNode itemsNode =
                json.path("items");

        if (itemsNode.isArray()) {

            for (JsonNode item : itemsNode) {

                items.add(
                        AiGeneratedItinerary.GeneratedItem
                                .builder()
                                .dayNumber(
                                        item.path("dayNumber")
                                                .asInt()
                                )
                                .activityOrder(
                                        item.path("activityOrder")
                                                .asInt()
                                )
                                .time(
                                        parseTime(
                                                item.path("time")
                                                        .asText()
                                        )
                                )
                                .activityType(
                                        ActivityType.valueOf(
                                                item.path(
                                                        "activityType"
                                                ).asText()
                                        )
                                )
                                .referenceId(
                                        item.path("referenceId")
                                                .asLong()
                                )
                                .notes(
                                        item.path("notes")
                                                .asText(null)
                                )
                                .build()
                );
            }
        }

        return AiGeneratedItinerary.builder()
                .title(
                        json.path("title")
                                .asText()
                )
                .description(
                        json.path("description")
                                .asText()
                )
                .startDate(
                        LocalDate.parse(
                                json.path("startDate")
                                        .asText()
                        )
                )
                .endDate(
                        LocalDate.parse(
                                json.path("endDate")
                                        .asText()
                        )
                )
                .numberOfTravelers(
                        json.path("numberOfTravelers")
                                .asInt()
                )
                .estimatedBudget(
                        new BigDecimal(
                                json.path("estimatedBudget")
                                        .asText()
                        )
                )
                .destinationId(
                        json.path("destinationId")
                                .asLong()
                )
                .items(items)
                .build();
    }

    private LocalTime parseTime(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Generated itinerary contains an invalid time."
            );
        }

        List<DateTimeFormatter> formatters =
                List.of(
                        DateTimeFormatter.ofPattern("HH:mm:ss"),
                        DateTimeFormatter.ofPattern("HH:mm"),
                        DateTimeFormatter.ofPattern("hh:mm a"),
                        DateTimeFormatter.ofPattern("h:mm a")
                );

        for (DateTimeFormatter formatter : formatters) {

            try {
                return LocalTime.parse(
                        value.trim(),
                        formatter
                );
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        throw new IllegalArgumentException(
                "Unsupported itinerary time format: " + value
        );
    }
}