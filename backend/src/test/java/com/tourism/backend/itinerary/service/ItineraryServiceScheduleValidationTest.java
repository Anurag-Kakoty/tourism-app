package com.tourism.backend.itinerary.service;

import com.tourism.backend.accommodation.repository.AccommodationRepository;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.attraction.repository.AttractionRepository;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.festival.repository.FestivalRepository;
import com.tourism.backend.guide.repository.GuideRepository;
import com.tourism.backend.itinerary.dto.ItineraryItemRequest;
import com.tourism.backend.itinerary.dto.ItineraryResponse;
import com.tourism.backend.itinerary.entity.ActivityType;
import com.tourism.backend.itinerary.entity.Itinerary;
import com.tourism.backend.itinerary.entity.ItineraryItem;
import com.tourism.backend.itinerary.mapper.ItineraryItemMapper;
import com.tourism.backend.itinerary.mapper.ItineraryMapper;
import com.tourism.backend.itinerary.repository.ItineraryItemRepository;
import com.tourism.backend.itinerary.repository.ItineraryRepository;
import com.tourism.backend.restaurant.repository.RestaurantRepository;
import com.tourism.backend.transport.repository.TransportRepository;
import com.tourism.backend.user.entity.User;
import com.tourism.backend.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItineraryServiceScheduleValidationTest {

    @Mock
    private ItineraryRepository itineraryRepository;
    @Mock
    private ItineraryItemRepository itineraryItemRepository;
    @Mock
    private DestinationRepository destinationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItineraryMapper itineraryMapper;
    @Mock
    private ItineraryItemMapper itineraryItemMapper;
    @Mock
    private AttractionRepository attractionRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private GuideRepository guideRepository;
    @Mock
    private TransportRepository transportRepository;
    @Mock
    private FestivalRepository festivalRepository;

    @InjectMocks
    private ItineraryServiceImpl itineraryService;

    private User testUser;
    private Itinerary testItinerary;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(100L);
        testUser.setEmail("traveler@example.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("traveler@example.com", "password")
        );

        testItinerary = new Itinerary();
        testItinerary.setId(1L);
        testItinerary.setUser(testUser);
        testItinerary.setStartDate(LocalDate.of(2026, 10, 12)); // Monday
        testItinerary.setEndDate(LocalDate.of(2026, 10, 16));   // Friday
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should successfully add attraction activity when operating schedule is completely unknown")
    void shouldAllowAddingActivityWhenScheduleIsUnknown() {
        Attraction attraction = new Attraction();
        attraction.setId(50L);
        attraction.setName("Unrestricted Viewpoint");
        attraction.setOpeningTime(null);
        attraction.setClosingTime(null);
        attraction.setClosedDays(Collections.emptySet());

        ItineraryItemRequest request = new ItineraryItemRequest();
        request.setDayNumber(1);
        request.setActivityOrder(1);
        request.setTime(LocalTime.of(22, 0));
        request.setActivityType(ActivityType.ATTRACTION);
        request.setReferenceId(50L);

        when(userRepository.findByEmailIgnoreCase("traveler@example.com")).thenReturn(Optional.of(testUser));
        when(itineraryRepository.findWithItemsByIdAndUser_Id(1L, 100L)).thenReturn(Optional.of(testItinerary));
        when(attractionRepository.findById(50L)).thenReturn(Optional.of(attraction));
        when(itineraryItemMapper.toEntity(any(), any())).thenReturn(new ItineraryItem());
        when(itineraryMapper.toResponse(any())).thenReturn(new ItineraryResponse());

        ItineraryResponse response = itineraryService.addItem(1L, request);

        assertThat(response).isNotNull();
        verify(itineraryItemRepository).save(any(ItineraryItem.class));
    }

    @Test
    @DisplayName("Should reject adding activity when scheduled date falls on a known closed day")
    void shouldRejectAddingActivityOnClosedDay() {
        Attraction attraction = new Attraction();
        attraction.setId(51L);
        attraction.setName("State Museum");
        attraction.setOpeningTime(LocalTime.of(10, 0));
        attraction.setClosingTime(LocalTime.of(17, 0));
        attraction.setClosedDays(Set.of(DayOfWeek.MONDAY));

        ItineraryItemRequest request = new ItineraryItemRequest();
        request.setDayNumber(1); // Day 1 = 2026-10-12 (Monday)
        request.setActivityOrder(1);
        request.setTime(LocalTime.of(11, 0));
        request.setActivityType(ActivityType.ATTRACTION);
        request.setReferenceId(51L);

        when(userRepository.findByEmailIgnoreCase("traveler@example.com")).thenReturn(Optional.of(testUser));
        when(itineraryRepository.findWithItemsByIdAndUser_Id(1L, 100L)).thenReturn(Optional.of(testItinerary));
        when(attractionRepository.findById(51L)).thenReturn(Optional.of(attraction));

        assertThatThrownBy(() -> itineraryService.addItem(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot schedule 'State Museum' on 2026-10-12")
                .hasMessageContaining("closed on MONDAYs");
    }

    @Test
    @DisplayName("Should reject adding activity when scheduled time is outside known operating hours")
    void shouldRejectAddingActivityOutsideHours() {
        Attraction attraction = new Attraction();
        attraction.setId(52L);
        attraction.setName("Wildlife Sanctuary");
        attraction.setOpeningTime(LocalTime.of(9, 0));
        attraction.setClosingTime(LocalTime.of(17, 0));
        attraction.setClosedDays(Collections.emptySet());

        ItineraryItemRequest request = new ItineraryItemRequest();
        request.setDayNumber(2); // Day 2 = 2026-10-13 (Tuesday)
        request.setActivityOrder(1);
        request.setTime(LocalTime.of(7, 30)); // 7:30 AM is before 9:00 AM opening
        request.setActivityType(ActivityType.ATTRACTION);
        request.setReferenceId(52L);

        when(userRepository.findByEmailIgnoreCase("traveler@example.com")).thenReturn(Optional.of(testUser));
        when(itineraryRepository.findWithItemsByIdAndUser_Id(1L, 100L)).thenReturn(Optional.of(testItinerary));
        when(attractionRepository.findById(52L)).thenReturn(Optional.of(attraction));

        assertThatThrownBy(() -> itineraryService.addItem(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot schedule 'Wildlife Sanctuary' at 07:30")
                .hasMessageContaining("outside operating hours (09:00 - 17:00)");
    }

    @Test
    @DisplayName("Should accept adding activity when within operating hours on an open day")
    void shouldAcceptAddingActivityWithinOperatingHours() {
        Attraction attraction = new Attraction();
        attraction.setId(52L);
        attraction.setName("Wildlife Sanctuary");
        attraction.setOpeningTime(LocalTime.of(9, 0));
        attraction.setClosingTime(LocalTime.of(17, 0));
        attraction.setClosedDays(Set.of(DayOfWeek.MONDAY));

        ItineraryItemRequest request = new ItineraryItemRequest();
        request.setDayNumber(2); // Day 2 = Tuesday (open)
        request.setActivityOrder(1);
        request.setTime(LocalTime.of(10, 0)); // 10:00 AM is within 09:00 - 17:00
        request.setActivityType(ActivityType.ATTRACTION);
        request.setReferenceId(52L);

        when(userRepository.findByEmailIgnoreCase("traveler@example.com")).thenReturn(Optional.of(testUser));
        when(itineraryRepository.findWithItemsByIdAndUser_Id(1L, 100L)).thenReturn(Optional.of(testItinerary));
        when(attractionRepository.findById(52L)).thenReturn(Optional.of(attraction));
        when(itineraryItemMapper.toEntity(any(), any())).thenReturn(new ItineraryItem());
        when(itineraryMapper.toResponse(any())).thenReturn(new ItineraryResponse());

        ItineraryResponse response = itineraryService.addItem(1L, request);

        assertThat(response).isNotNull();
        verify(itineraryItemRepository).save(any(ItineraryItem.class));
    }
}

