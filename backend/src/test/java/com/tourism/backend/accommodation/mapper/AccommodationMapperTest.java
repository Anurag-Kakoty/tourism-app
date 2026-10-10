package com.tourism.backend.accommodation.mapper;

import com.tourism.backend.accommodation.dto.AccommodationRequest;
import com.tourism.backend.accommodation.dto.AccommodationResponse;
import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.entity.AccommodationType;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.state.entity.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccommodationMapperTest {

    private AccommodationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AccommodationMapper();
    }

    @Test
    @DisplayName("Should map check-in and check-out times from request to entity")
    void shouldMapScheduleToEntity() {
        Destination destination = new Destination();
        destination.setId(1L);

        AccommodationRequest request = new AccommodationRequest();
        request.setName("Pine Hill Resort");
        request.setType(AccommodationType.RESORT);
        request.setPricePerNight(BigDecimal.valueOf(3500));
        request.setAddress("Laitumkhrah, Shillong");
        request.setCheckInTime(LocalTime.of(14, 0));
        request.setCheckOutTime(LocalTime.of(11, 0));

        Accommodation accommodation = mapper.toEntity(request, destination);

        assertThat(accommodation.getName()).isEqualTo("Pine Hill Resort");
        assertThat(accommodation.getCheckInTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(accommodation.getCheckOutTime()).isEqualTo(LocalTime.of(11, 0));
    }

    @Test
    @DisplayName("Should update entity with check-in and check-out times")
    void shouldUpdateEntityWithSchedule() {
        Destination destination = new Destination();
        destination.setId(1L);

        Accommodation accommodation = new Accommodation();
        accommodation.setName("Old Name");
        accommodation.setCheckInTime(LocalTime.of(12, 0));
        accommodation.setCheckOutTime(LocalTime.of(10, 0));

        AccommodationRequest request = new AccommodationRequest();
        request.setName("Updated Resort");
        request.setType(AccommodationType.RESORT);
        request.setPricePerNight(BigDecimal.valueOf(4000));
        request.setAddress("New Address");
        request.setCheckInTime(LocalTime.of(15, 0));
        request.setCheckOutTime(LocalTime.of(12, 0));

        mapper.updateEntity(accommodation, request, destination);

        assertThat(accommodation.getName()).isEqualTo("Updated Resort");
        assertThat(accommodation.getCheckInTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(accommodation.getCheckOutTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    @DisplayName("Should map check-in and check-out times from entity to response")
    void shouldMapScheduleToResponse() {
        State state = new State();
        state.setId(10L);
        state.setName("Meghalaya");

        Destination destination = new Destination();
        destination.setId(1L);
        destination.setName("Shillong");
        destination.setState(state);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(5L);
        accommodation.setName("Cloud View Resort");
        accommodation.setType(AccommodationType.RESORT);
        accommodation.setPricePerNight(BigDecimal.valueOf(4200));
        accommodation.setDestination(destination);
        accommodation.setCheckInTime(LocalTime.of(14, 0));
        accommodation.setCheckOutTime(LocalTime.of(11, 0));

        AccommodationResponse response = mapper.toResponse(accommodation);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getName()).isEqualTo("Cloud View Resort");
        assertThat(response.getCheckInTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(response.getCheckOutTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(response.getDestinationId()).isEqualTo(1L);
        assertThat(response.getStateName()).isEqualTo("Meghalaya");
    }

    @Test
    @DisplayName("Should handle null check-in and check-out times gracefully")
    void shouldHandleNullSchedule() {
        Destination destination = new Destination();
        destination.setId(1L);

        AccommodationRequest request = new AccommodationRequest();
        request.setName("Homestay Simple");
        request.setType(AccommodationType.HOMESTAY);
        request.setPricePerNight(BigDecimal.valueOf(1500));
        request.setAddress("Village Road");

        Accommodation accommodation = mapper.toEntity(request, destination);

        assertThat(accommodation.getCheckInTime()).isNull();
        assertThat(accommodation.getCheckOutTime()).isNull();
    }
}

