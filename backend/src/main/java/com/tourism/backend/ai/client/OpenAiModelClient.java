package com.tourism.backend.ai.client;

import com.tourism.backend.ai.config.AiProperties;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiModelClient implements AiModelClient {

    private static final String OPENAI_RESPONSES_URL =
            "https://api.openai.com/v1/responses";

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient.Builder restClientBuilder;

    @Override
    public AiGeneratedItinerary generateItinerary(
            AiItineraryRequest request,
            RecommendedCandidateContext candidates) {

        if (aiProperties.getOpenai().getApiKey() == null
                || aiProperties.getOpenai().getApiKey().isBlank()) {

            throw new IllegalStateException(
                    "OpenAI API key is not configured."
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
                            "model",
                            aiProperties.getOpenai().getModel(),

                            "input",
                            List.of(
                                    Map.of(
                                            "role",
                                            "system",
                                            "content",
                                            List.of(
                                                    Map.of(
                                                            "type",
                                                            "input_text",
                                                            "text",
                                                            buildSystemPrompt()
                                                    )
                                            )
                                    ),
                                    Map.of(
                                            "role",
                                            "user",
                                            "content",
                                            List.of(
                                                    Map.of(
                                                            "type",
                                                            "input_text",
                                                            "text",
                                                            prompt
                                                    )
                                            )
                                    )
                            ),

                            "text",
                            Map.of(
                                    "format",
                                    Map.of(
                                            "type",
                                            "json_schema",
                                            "name",
                                            "tourism_itinerary",
                                            "strict",
                                            true,
                                            "schema",
                                            buildItinerarySchema()
                                    )
                            )
                    );

            RestClient client =
                    restClientBuilder.build();

            String responseBody =
                    client.post()
                            .uri(OPENAI_RESPONSES_URL)
                            .header(
                                    "Authorization",
                                    "Bearer "
                                            + aiProperties
                                            .getOpenai()
                                            .getApiKey()
                            )
                            .contentType(
                                    org.springframework.http.MediaType.APPLICATION_JSON
                            )
                            .body(requestBody)
                            .retrieve()
                            .body(String.class);

            return parseResponse(responseBody);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "OpenAI itinerary generation failed.",
                    exception
            );
        }
    }

    private String buildSystemPrompt() {

        return """
                You are an AI tourism itinerary planner.

                Your task is to create a practical travel itinerary
                using ONLY the candidates supplied by the application.

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
                11. Do not return explanatory text outside the JSON
                    response.
                12. Do not invent prices, ratings, locations, or
                    other database information.
                """;
    }

    private String buildPrompt(
            AiItineraryRequest request,
            String candidateJson) {

        return """
                Create an itinerary using the following request.

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

                Use only the candidates above.

                The itinerary should contain useful activities
                distributed across the requested travel days.

                Accommodation may be included where appropriate.
                Restaurants may be included for meals.
                Guides and transport may be included when useful.
                Festivals should only be included when present in
                the supplied festival candidates.

                Return the itinerary in the required JSON structure.
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
                "type",
                "object",

                "additionalProperties",
                false,

                "properties",
                Map.of(

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
                                        "additionalProperties",
                                        false,
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
                                                        "string"
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

            JsonNode outputText =
                    findOutputText(root);

            if (outputText == null
                    || outputText.asText().isBlank()) {

                throw new IllegalStateException(
                        "OpenAI response did not contain itinerary JSON."
                );
            }

            JsonNode itinerary =
                    objectMapper.readTree(
                            outputText.asText()
                    );

            return mapGeneratedItinerary(itinerary);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to parse OpenAI itinerary response.",
                    exception
            );
        }
    }

    private JsonNode findOutputText(
            JsonNode root) {

        JsonNode output =
                root.get("output");

        if (output == null || !output.isArray()) {
            return null;
        }

        for (JsonNode outputItem : output) {

            JsonNode content =
                    outputItem.get("content");

            if (content == null || !content.isArray()) {
                continue;
            }

            for (JsonNode contentItem : content) {

                if ("output_text".equals(
                        contentItem.path("type").asText())) {

                    return contentItem.get("text");
                }
            }
        }

        return null;
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
                                        item.path("dayNumber")
                                                .asInt()
                                )
                                .activityOrder(
                                        item.path("activityOrder")
                                                .asInt()
                                )
                                .time(
                                        LocalTime.parse(
                                                item.path("time")
                                                        .asText()
                                        )
                                )
                                .activityType(
                                        com.tourism.backend.itinerary
                                                .entity.ActivityType
                                                .valueOf(
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
                                                .asText()
                                )
                                .build()
                );
            }
        }

        return AiGeneratedItinerary.builder()
                .title(
                        itinerary.path("title")
                                .asText()
                )
                .description(
                        itinerary.path("description")
                                .asText()
                )
                .startDate(
                        LocalDate.parse(
                                itinerary.path("startDate")
                                        .asText()
                        )
                )
                .endDate(
                        LocalDate.parse(
                                itinerary.path("endDate")
                                        .asText()
                        )
                )
                .numberOfTravelers(
                        itinerary.path("numberOfTravelers")
                                .asInt()
                )
                .estimatedBudget(
                        itinerary.path("estimatedBudget")
                                .decimalValue()
                )
                .destinationId(
                        itinerary.path("destinationId")
                                .asLong()
                )
                .items(items)
                .build();
    }
}