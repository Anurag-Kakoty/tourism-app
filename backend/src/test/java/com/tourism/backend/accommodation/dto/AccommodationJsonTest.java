package com.tourism.backend.accommodation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tourism.backend.accommodation.entity.AccommodationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccommodationJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should deserialize full time format 'HH:mm:ss' for check-in and check-out from JSON")
    void shouldDeserializeFullFormat() throws Exception {
        String json = """
                {
                    "name": "Pine Hill Resort",
                    "type": "RESORT",
                    "pricePerNight": 3500.00,
                    "address": "Laitumkhrah, Shillong",
                    "destinationId": 1,
                    "checkInTime": "14:00:00",
                    "checkOutTime": "11:00:00"
                }
                """;

        AccommodationRequest request = objectMapper.readValue(json, AccommodationRequest.class);

        assertThat(request.getName()).isEqualTo("Pine Hill Resort");
        assertThat(request.getType()).isEqualTo(AccommodationType.RESORT);
        assertThat(request.getPricePerNight()).isEqualByComparingTo("3500.00");
        assertThat(request.getCheckInTime()).isEqualTo(LocalTime.of(14, 0, 0));
        assertThat(request.getCheckOutTime()).isEqualTo(LocalTime.of(11, 0, 0));
    }

    @Test
    @DisplayName("Should deserialize short time format 'HH:mm' without seconds")
    void shouldDeserializeShortTimeFormat() throws Exception {
        String json = """
                {
                    "name": "Shillong Homestay",
                    "type": "HOMESTAY",
                    "pricePerNight": 1800.00,
                    "address": "Upper Shillong",
                    "destinationId": 1,
                    "checkInTime": "15:00",
                    "checkOutTime": "10:30"
                }
                """;

        AccommodationRequest request = objectMapper.readValue(json, AccommodationRequest.class);

        assertThat(request.getCheckInTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(request.getCheckOutTime()).isEqualTo(LocalTime.of(10, 30));
    }

    @Test
    @DisplayName("Should serialize AccommodationResponse with formatted ISO-8601 time strings")
    void shouldSerializeAccommodationResponse() throws Exception {
        AccommodationResponse response = new AccommodationResponse();
        response.setId(10L);
        response.setName("Grand Palace Hotel");
        response.setCheckInTime(LocalTime.of(14, 0, 0));
        response.setCheckOutTime(LocalTime.of(11, 0, 0));

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"checkInTime\":\"14:00:00\"");
        assertThat(json).contains("\"checkOutTime\":\"11:00:00\"");
    }

    @Test
    @DisplayName("Should handle null check-in and check-out times gracefully")
    void shouldHandleNullTimes() throws Exception {
        String json = """
                {
                    "name": "Rustic Lodge",
                    "type": "LODGE",
                    "pricePerNight": 1200.00,
                    "address": "Forest Area",
                    "destinationId": 1
                }
                """;

        AccommodationRequest request = objectMapper.readValue(json, AccommodationRequest.class);
        assertThat(request.getCheckInTime()).isNull();
        assertThat(request.getCheckOutTime()).isNull();

        AccommodationResponse response = new AccommodationResponse();
        response.setId(11L);
        response.setName("Rustic Lodge");
        response.setCheckInTime(null);
        response.setCheckOutTime(null);

        String responseJson = objectMapper.writeValueAsString(response);
        assertThat(responseJson).contains("\"checkInTime\":null");
        assertThat(responseJson).contains("\"checkOutTime\":null");
    }
}

