package com.tourism.backend.accommodation.service;

import com.tourism.backend.accommodation.dto.AccommodationRequest;
import com.tourism.backend.accommodation.dto.AccommodationResponse;
import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.entity.AccommodationType;
import com.tourism.backend.accommodation.mapper.AccommodationMapper;
import com.tourism.backend.accommodation.repository.AccommodationRepository;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceScheduleValidationTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private AccommodationMapper mapper;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    @DisplayName("Should succeed create when check-in and check-out times are identical (e.g. 24-hour stay policy)")
    void shouldSucceedCreateWithIdenticalTimes() {
        AccommodationRequest request = new AccommodationRequest();
        request.setName("24-Hour Stay Resort");
        request.setType(AccommodationType.RESORT);
        request.setPricePerNight(BigDecimal.valueOf(3500));
        request.setAddress("City Center");
        request.setDestinationId(1L);
        request.setCheckInTime(LocalTime.of(12, 0));
        request.setCheckOutTime(LocalTime.of(12, 0));

        Destination destination = new Destination();
        destination.setId(1L);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(10L);

        when(accommodationRepository.existsByNameIgnoreCaseAndDestination_Id(request.getName(), 1L))
                .thenReturn(false);
        when(destinationRepository.findById(1L)).thenReturn(Optional.of(destination));
        when(mapper.toEntity(request, destination)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(mapper.toResponse(accommodation)).thenReturn(new AccommodationResponse());

        AccommodationResponse response = accommodationService.create(request);

        assertThat(response).isNotNull();
        verify(accommodationRepository).save(accommodation);
    }

    @Test
    @DisplayName("Should succeed create with check-in only schedule")
    void shouldSucceedCreateWithCheckInOnly() {
        AccommodationRequest request = new AccommodationRequest();
        request.setName("Check-in Only Homestay");
        request.setType(AccommodationType.HOMESTAY);
        request.setPricePerNight(BigDecimal.valueOf(1800));
        request.setAddress("Village Road");
        request.setDestinationId(1L);
        request.setCheckInTime(LocalTime.of(14, 0));
        request.setCheckOutTime(null);

        Destination destination = new Destination();
        destination.setId(1L);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(11L);

        when(accommodationRepository.existsByNameIgnoreCaseAndDestination_Id(request.getName(), 1L))
                .thenReturn(false);
        when(destinationRepository.findById(1L)).thenReturn(Optional.of(destination));
        when(mapper.toEntity(request, destination)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(mapper.toResponse(accommodation)).thenReturn(new AccommodationResponse());

        AccommodationResponse response = accommodationService.create(request);

        assertThat(response).isNotNull();
        verify(accommodationRepository).save(accommodation);
    }

    @Test
    @DisplayName("Should succeed create when check-in and check-out times are standard")
    void shouldSucceedCreateWithStandardTimes() {
        AccommodationRequest request = new AccommodationRequest();
        request.setName("Pine Hill Resort");
        request.setType(AccommodationType.RESORT);
        request.setPricePerNight(BigDecimal.valueOf(3500));
        request.setAddress("Laitumkhrah, Shillong");
        request.setDestinationId(1L);
        request.setCheckInTime(LocalTime.of(14, 0));
        request.setCheckOutTime(LocalTime.of(11, 0));

        Destination destination = new Destination();
        destination.setId(1L);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(12L);

        when(accommodationRepository.existsByNameIgnoreCaseAndDestination_Id(request.getName(), 1L))
                .thenReturn(false);
        when(destinationRepository.findById(1L)).thenReturn(Optional.of(destination));
        when(mapper.toEntity(request, destination)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(mapper.toResponse(accommodation)).thenReturn(new AccommodationResponse());

        AccommodationResponse response = accommodationService.create(request);

        assertThat(response).isNotNull();
        verify(accommodationRepository).save(accommodation);
    }
}
