package com.tourism.backend.attraction.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AttractionJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should deserialize LocalTime and DayOfWeek from standard JSON payload (Swagger / Postman format)")
    void shouldDeserializeAttractionRequest() throws Exception {
        String json = """
                {
                    "name": "Kaziranga National Park Safari",
                    "description": "Wildlife safari",
                    "latitude": 26.5775,
                    "longitude": 93.1711,
                    "bestSeason": "November to April",
                    "entryFee": 1200.00,
                    "destinationId": 1,
                    "featured": true,
                    "displayOrder": 1,
                    "openingTime": "09:00:00",
                    "closingTime": "17:30:00",
                    "closedDays": ["MONDAY", "WEDNESDAY"]
                }
                """;

        AttractionRequest request = objectMapper.readValue(json, AttractionRequest.class);

        assertThat(request.getName()).isEqualTo("Kaziranga National Park Safari");
        assertThat(request.getOpeningTime()).isEqualTo(LocalTime.of(9, 0, 0));
        assertThat(request.getClosingTime()).isEqualTo(LocalTime.of(17, 30, 0));
        assertThat(request.getClosedDays()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
    }

    @Test
    @DisplayName("Should deserialize short time format '09:00' without seconds")
    void shouldDeserializeShortTimeFormat() throws Exception {
        String json = """
                {
                    "name": "Quick Time Attraction",
                    "openingTime": "08:30",
                    "closingTime": "16:45"
                }
                """;

        AttractionRequest request = objectMapper.readValue(json, AttractionRequest.class);

        assertThat(request.getOpeningTime()).isEqualTo(LocalTime.of(8, 30));
        assertThat(request.getClosingTime()).isEqualTo(LocalTime.of(16, 45));
    }

    @Test
    @DisplayName("Should deserialize null or omitted operating hours and closed days gracefully")
    void shouldDeserializeNullOrOmittedSchedule() throws Exception {
        String json = """
                {
                    "name": "Unrestricted Monument",
                    "destinationId": 1,
                    "featured": false,
                    "displayOrder": 0
                }
                """;

        AttractionRequest request = objectMapper.readValue(json, AttractionRequest.class);

        assertThat(request.getOpeningTime()).isNull();
        assertThat(request.getClosingTime()).isNull();
        assertThat(request.getClosedDays()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should serialize AttractionResponse with ISO-8601 time strings and enum day strings")
    void shouldSerializeAttractionResponse() throws Exception {
        AttractionResponse response = new AttractionResponse();
        response.setId(10L);
        response.setName("Living Root Bridge");
        response.setOpeningTime(LocalTime.of(6, 0, 0));
        response.setClosingTime(LocalTime.of(18, 0, 0));
        response.setClosedDays(Set.of(DayOfWeek.SUNDAY));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"openingTime\":\"06:00:00\"");
        assertThat(json).contains("\"closingTime\":\"18:00:00\"");
        assertThat(json).contains("\"closedDays\":[\"SUNDAY\"]");
    }
}

