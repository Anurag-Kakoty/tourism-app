package com.tourism.backend.attraction.mapper;

import com.tourism.backend.attraction.dto.AttractionRequest;
import com.tourism.backend.attraction.dto.AttractionResponse;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.state.entity.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AttractionMapperTest {

    private AttractionMapper attractionMapper;

    @BeforeEach
    void setUp() {
        attractionMapper = new AttractionMapper();
    }

    @Test
    @DisplayName("Should map openingTime, closingTime, and closedDays from request to entity")
    void shouldMapOperatingHoursToEntity() {
        Destination destination = new Destination();
        destination.setId(10L);

        AttractionRequest request = new AttractionRequest();
        request.setName("Kaziranga National Park");
        request.setDescription("National park in Assam");
        request.setLatitude(26.5775);
        request.setLongitude(93.1711);
        request.setBestSeason("November to April");
        request.setEntryFee(BigDecimal.valueOf(1200));
        request.setThumbnailUrl("https://example.com/kaziranga.jpg");
        request.setFeatured(true);
        request.setDisplayOrder(1);
        request.setOpeningTime(LocalTime.of(8, 0));
        request.setClosingTime(LocalTime.of(16, 30));
        request.setClosedDays(Set.of(DayOfWeek.WEDNESDAY));

        Attraction attraction = attractionMapper.toEntity(
                request,
                destination,
                Collections.emptySet(),
                Collections.emptySet()
        );

        assertThat(attraction.getOpeningTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(attraction.getClosingTime()).isEqualTo(LocalTime.of(16, 30));
        assertThat(attraction.getClosedDays()).containsExactly(DayOfWeek.WEDNESDAY);
    }

    @Test
    @DisplayName("Should map operating hours and closed days to response")
    void shouldMapOperatingHoursToResponse() {
        State state = new State();
        state.setId(1L);
        state.setName("Assam");

        Destination destination = new Destination();
        destination.setId(10L);
        destination.setName("Kaziranga");
        destination.setState(state);

        Attraction attraction = new Attraction();
        attraction.setId(5L);
        attraction.setName("Kaziranga National Park");
        attraction.setDestination(destination);
        attraction.setOpeningTime(LocalTime.of(9, 0));
        attraction.setClosingTime(LocalTime.of(17, 0));
        attraction.setClosedDays(Set.of(DayOfWeek.MONDAY));

        AttractionResponse response = attractionMapper.toResponse(attraction);

        assertThat(response.getOpeningTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(response.getClosingTime()).isEqualTo(LocalTime.of(17, 0));
        assertThat(response.getClosedDays()).containsExactly(DayOfWeek.MONDAY);
    }

    @Test
    @DisplayName("Should handle null closedDays and unknown hours gracefully")
    void shouldHandleNullScheduleGracefully() {
        Destination destination = new Destination();
        destination.setId(10L);

        AttractionRequest request = new AttractionRequest();
        request.setName("Unrestricted Bridge");
        request.setOpeningTime(null);
        request.setClosingTime(null);
        request.setClosedDays(null);

        Attraction attraction = attractionMapper.toEntity(
                request,
                destination,
                Collections.emptySet(),
                Collections.emptySet()
        );

        assertThat(attraction.getOpeningTime()).isNull();
        assertThat(attraction.getClosingTime()).isNull();
        assertThat(attraction.getClosedDays()).isNotNull().isEmpty();
    }
}

