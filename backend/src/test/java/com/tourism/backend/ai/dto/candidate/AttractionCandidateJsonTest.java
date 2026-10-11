package com.tourism.backend.ai.dto.candidate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.backend.ai.dto.TripDayCalendarEntry;
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

class AttractionCandidateJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    @DisplayName("AttractionCandidate should serialize openingTime and closingTime as HH:mm:ss strings")
    void shouldSerializeOperatingHoursAndClosedDays() throws Exception {
        AttractionCandidate candidate = AttractionCandidate.builder()
                .id(10L)
                .name("Living Root Bridge")
                .openingTime(LocalTime.of(9, 0, 0))
                .closingTime(LocalTime.of(17, 30, 0))
                .closedDays(Set.of(DayOfWeek.MONDAY))
                .entryFee(BigDecimal.valueOf(50))
                .featured(true)
                .tags(List.of("Nature"))
                .build();

        String json = objectMapper.writeValueAsString(candidate);

        assertThat(json).contains("\"openingTime\":\"09:00:00\"");
        assertThat(json).contains("\"closingTime\":\"17:30:00\"");
        assertThat(json).contains("\"closedDays\":[\"MONDAY\"]");
    }

    @Test
    @DisplayName("AttractionCandidate should serialize null operating hours and empty closed days cleanly")
    void shouldSerializeNullOperatingHours() throws Exception {
        AttractionCandidate candidate = AttractionCandidate.builder()
                .id(11L)
                .name("Unconstrained Viewpoint")
                .openingTime(null)
                .closingTime(null)
                .closedDays(Set.of())
                .build();

        String json = objectMapper.writeValueAsString(candidate);

        assertThat(json).contains("\"openingTime\":null");
        assertThat(json).contains("\"closingTime\":null");
        assertThat(json).contains("\"closedDays\":[]");
    }

    @Test
    @DisplayName("TripDayCalendarEntry should serialize date as ISO-8601 string and dayOfWeek as enum string")
    void shouldSerializeTripDayCalendarEntry() throws Exception {
        TripDayCalendarEntry entry = new TripDayCalendarEntry(
                1,
                LocalDate.of(2026, 10, 12),
                DayOfWeek.MONDAY
        );

        String json = objectMapper.writeValueAsString(entry);

        assertThat(json).contains("\"dayNumber\":1");
        assertThat(json).contains("\"date\":\"2026-10-12\"");
        assertThat(json).contains("\"dayOfWeek\":\"MONDAY\"");
    }
}

