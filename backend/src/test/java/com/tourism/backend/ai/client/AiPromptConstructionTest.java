package com.tourism.backend.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AiPromptConstructionTest {

    private ObjectMapper objectMapper;
    private OllamaModelClient ollamaModelClient;
    private GeminiModelClient geminiModelClient;
    private OpenAiModelClient openAiModelClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        ollamaModelClient = new OllamaModelClient(objectMapper, null, null);
        geminiModelClient = new GeminiModelClient(null, null, null);
        openAiModelClient = new OpenAiModelClient(null, null, null);
    }

    private AiItineraryRequest createSampleRequest(LocalDate startDate, LocalDate endDate) {
        AiItineraryRequest request = new AiItineraryRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setNumberOfTravelers(2);
        request.setBudget(BigDecimal.valueOf(15000));
        request.setDestinationId(1L);
        request.setExperienceIds(List.of(10L, 20L));
        return request;
    }

    private RecommendedCandidateContext createSampleCandidates() {
        AttractionCandidate candidate = AttractionCandidate.builder()
                .id(1L)
                .name("Living Root Bridge")
                .description("Natural root bridge")
                .bestSeason("October to April")
                .entryFee(BigDecimal.valueOf(50))
                .featured(true)
                .latitude(25.2866)
                .longitude(91.7326)
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(17, 0))
                .closedDays(Set.of(DayOfWeek.SUNDAY))
                .tags(List.of("Nature", "Trekking"))
                .build();

        return RecommendedCandidateContext.builder()
                .attractions(List.of(candidate))
                .accommodations(List.of())
                .restaurants(List.of())
                .guides(List.of())
                .transport(List.of())
                .festivals(List.of())
                .build();
    }

    @Test
    @DisplayName("Ollama prompt should include calendar breakdown, schedule rules, and structured tripCalendar JSON")
    void shouldBuildOllamaPromptWithCalendarAndScheduleRules() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 10, 12); // Monday
        LocalDate endDate = LocalDate.of(2026, 10, 14);   // Wednesday
        AiItineraryRequest request = createSampleRequest(startDate, endDate);
        RecommendedCandidateContext candidates = createSampleCandidates();

        String prompt = ollamaModelClient.buildPrompt(request, candidates);

        // Verify human-readable TRIP CALENDAR is present
        assertThat(prompt).contains("TRIP CALENDAR:");
        assertThat(prompt).contains("Day 1: 2026-10-12 (MONDAY)");
        assertThat(prompt).contains("Day 2: 2026-10-13 (TUESDAY)");
        assertThat(prompt).contains("Day 3: 2026-10-14 (WEDNESDAY)");

        // Verify schedule rules are present
        assertThat(prompt).contains("Operating hours and schedule rules (CRITICAL):");
        assertThat(prompt).contains("NEVER schedule an attraction on a day of the week when it is closed");
        assertThat(prompt).contains("openingTime <= time < closingTime");

        // Verify embedded JSON payload contains structured tripCalendar
        int jsonStartIndex = prompt.indexOf("USER REQUEST AND AVAILABLE CANDIDATES:\n\n") +
                "USER REQUEST AND AVAILABLE CANDIDATES:\n\n".length();
        String jsonPayload = prompt.substring(jsonStartIndex).trim();

        JsonNode root = objectMapper.readTree(jsonPayload);
        assertThat(root.has("tripCalendar")).isTrue();

        JsonNode tripCalendarNode = root.get("tripCalendar");
        assertThat(tripCalendarNode.isArray()).isTrue();
        assertThat(tripCalendarNode).hasSize(3);

        JsonNode day1Node = tripCalendarNode.get(0);
        assertThat(day1Node.get("dayNumber").asInt()).isEqualTo(1);
        assertThat(day1Node.get("date").asText()).isEqualTo("2026-10-12");
        assertThat(day1Node.get("dayOfWeek").asText()).isEqualTo("MONDAY");
    }

    @Test
    @DisplayName("Gemini prompt should include calendar breakdown and schedule rules")
    void shouldBuildGeminiPromptWithCalendarAndScheduleRules() {
        LocalDate startDate = LocalDate.of(2026, 10, 16); // Friday
        LocalDate endDate = LocalDate.of(2026, 10, 17);   // Saturday
        AiItineraryRequest request = createSampleRequest(startDate, endDate);

        String prompt = geminiModelClient.buildPrompt(request, "{\"candidates\":[]}");

        // Verify calendar
        assertThat(prompt).contains("TRIP CALENDAR:");
        assertThat(prompt).contains("Day 1: 2026-10-16 (FRIDAY)");
        assertThat(prompt).contains("Day 2: 2026-10-17 (SATURDAY)");

        // Verify schedule rules
        assertThat(prompt).contains("Operating hours and schedule rules (CRITICAL):");
        assertThat(prompt).contains("NEVER schedule an attraction on a day of the week when it is closed");
        assertThat(prompt).contains("Standard daytime hours");
        assertThat(prompt).contains("Unknown operating hours");
    }

    @Test
    @DisplayName("OpenAI system prompt and user prompt should include schedule rules and calendar breakdown")
    void shouldBuildOpenAiPromptsWithCalendarAndScheduleRules() {
        LocalDate startDate = LocalDate.of(2026, 10, 18); // Sunday
        LocalDate endDate = LocalDate.of(2026, 10, 19);   // Monday
        AiItineraryRequest request = createSampleRequest(startDate, endDate);

        String systemPrompt = openAiModelClient.buildSystemPrompt();
        String userPrompt = openAiModelClient.buildPrompt(request, "{\"candidates\":[]}");

        // System prompt contains schedule rules
        assertThat(systemPrompt).contains("Operating hours and schedule rules (CRITICAL):");
        assertThat(systemPrompt).contains("NEVER schedule an attraction on a day of the week when it is closed");
        assertThat(systemPrompt).contains("openingTime <= time < closingTime");

        // User prompt contains calendar breakdown
        assertThat(userPrompt).contains("TRIP CALENDAR:");
        assertThat(userPrompt).contains("Day 1: 2026-10-18 (SUNDAY)");
        assertThat(userPrompt).contains("Day 2: 2026-10-19 (MONDAY)");
    }
}

