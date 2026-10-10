package com.tourism.backend.restaurant.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should deserialize full time format 'HH:mm:ss' and closedDays from JSON")
    void shouldDeserializeFullFormat() throws Exception {
        String json = """
                {
                    "name": "Brahmaputra Feast",
                    "cuisine": "ASSAMESE",
                    "vegetarian": false,
                    "priceRange": "MID_RANGE",
                    "destinationId": 1,
                    "openingHours": "12:00-15:00, 19:00-23:00",
                    "openingTime": "12:00:00",
                    "closingTime": "15:00:00",
                    "secondOpeningTime": "19:00:00",
                    "secondClosingTime": "23:00:00",
                    "closedDays": ["MONDAY", "WEDNESDAY"]
                }
                """;

        RestaurantRequest request = objectMapper.readValue(json, RestaurantRequest.class);

        assertThat(request.getName()).isEqualTo("Brahmaputra Feast");
        assertThat(request.getOpeningTime()).isEqualTo(LocalTime.of(12, 0, 0));
        assertThat(request.getClosingTime()).isEqualTo(LocalTime.of(15, 0, 0));
        assertThat(request.getSecondOpeningTime()).isEqualTo(LocalTime.of(19, 0, 0));
        assertThat(request.getSecondClosingTime()).isEqualTo(LocalTime.of(23, 0, 0));
        assertThat(request.getClosedDays()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
    }

    @Test
    @DisplayName("Should deserialize short time format 'HH:mm' without seconds")
    void shouldDeserializeShortTimeFormat() throws Exception {
        String json = """
                {
                    "name": "Quick Bites",
                    "cuisine": "CAFE",
                    "vegetarian": true,
                    "priceRange": "BUDGET",
                    "destinationId": 1,
                    "openingTime": "08:30",
                    "closingTime": "14:00",
                    "secondOpeningTime": "17:30",
                    "secondClosingTime": "22:00"
                }
                """;

        RestaurantRequest request = objectMapper.readValue(json, RestaurantRequest.class);

        assertThat(request.getOpeningTime()).isEqualTo(LocalTime.of(8, 30));
        assertThat(request.getClosingTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(request.getSecondOpeningTime()).isEqualTo(LocalTime.of(17, 30));
        assertThat(request.getSecondClosingTime()).isEqualTo(LocalTime.of(22, 0));
    }

    @Test
    @DisplayName("Should serialize RestaurantResponse with ISO-8601 time strings and string enum days")
    void shouldSerializeRestaurantResponse() throws Exception {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(20L);
        response.setName("Heritage Thali");
        response.setOpeningTime(LocalTime.of(11, 30, 0));
        response.setClosingTime(LocalTime.of(15, 0, 0));
        response.setSecondOpeningTime(LocalTime.of(19, 0, 0));
        response.setSecondClosingTime(LocalTime.of(23, 0, 0));
        response.setClosedDays(Set.of(DayOfWeek.SUNDAY));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"openingTime\":\"11:30:00\"");
        assertThat(json).contains("\"closingTime\":\"15:00:00\"");
        assertThat(json).contains("\"secondOpeningTime\":\"19:00:00\"");
        assertThat(json).contains("\"secondClosingTime\":\"23:00:00\"");
        assertThat(json).contains("\"closedDays\":[\"SUNDAY\"]");
    }
}
