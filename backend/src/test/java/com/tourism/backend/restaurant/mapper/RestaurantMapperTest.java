package com.tourism.backend.restaurant.mapper;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.restaurant.dto.RestaurantRequest;
import com.tourism.backend.restaurant.dto.RestaurantResponse;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import com.tourism.backend.restaurant.entity.Restaurant;
import com.tourism.backend.state.entity.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantMapperTest {

    private RestaurantMapper restaurantMapper;

    @BeforeEach
    void setUp() {
        restaurantMapper = new RestaurantMapper();
    }

    @Test
    @DisplayName("Should map structured schedule fields from request to entity")
    void shouldMapScheduleToEntity() {
        Destination destination = new Destination();
        destination.setId(1L);

        RestaurantRequest request = new RestaurantRequest();
        request.setName("Assam Kitchen");
        request.setCuisine(Cuisine.ASSAMESE);
        request.setVegetarian(false);
        request.setPriceRange(PriceRange.MID_RANGE);
        request.setOpeningHours("11:30-15:00, 18:30-22:30");
        request.setOpeningTime(LocalTime.of(11, 30));
        request.setClosingTime(LocalTime.of(15, 0));
        request.setSecondOpeningTime(LocalTime.of(18, 30));
        request.setSecondClosingTime(LocalTime.of(22, 30));
        request.setClosedDays(Set.of(DayOfWeek.TUESDAY));

        Restaurant restaurant = restaurantMapper.toEntity(request, destination);

        assertThat(restaurant.getOpeningHours()).isEqualTo("11:30-15:00, 18:30-22:30");
        assertThat(restaurant.getOpeningTime()).isEqualTo(LocalTime.of(11, 30));
        assertThat(restaurant.getClosingTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(restaurant.getSecondOpeningTime()).isEqualTo(LocalTime.of(18, 30));
        assertThat(restaurant.getSecondClosingTime()).isEqualTo(LocalTime.of(22, 30));
        assertThat(restaurant.getClosedDays()).containsExactly(DayOfWeek.TUESDAY);
    }

    @Test
    @DisplayName("Should map structured schedule fields from entity to response")
    void shouldMapScheduleToResponse() {
        State state = new State();
        state.setId(10L);
        state.setName("Assam");

        Destination destination = new Destination();
        destination.setId(1L);
        destination.setName("Guwahati");
        destination.setState(state);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(5L);
        restaurant.setName("Assam Kitchen");
        restaurant.setDestination(destination);
        restaurant.setOpeningHours("12:00-22:00");
        restaurant.setOpeningTime(LocalTime.of(12, 0));
        restaurant.setClosingTime(LocalTime.of(22, 0));
        restaurant.setSecondOpeningTime(null);
        restaurant.setSecondClosingTime(null);
        restaurant.setClosedDays(Set.of(DayOfWeek.MONDAY));

        RestaurantResponse response = restaurantMapper.toResponse(restaurant);

        assertThat(response.getOpeningHours()).isEqualTo("12:00-22:00");
        assertThat(response.getOpeningTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(response.getClosingTime()).isEqualTo(LocalTime.of(22, 0));
        assertThat(response.getSecondOpeningTime()).isNull();
        assertThat(response.getSecondClosingTime()).isNull();
        assertThat(response.getClosedDays()).containsExactly(DayOfWeek.MONDAY);
    }

    @Test
    @DisplayName("Should handle null structured schedule gracefully")
    void shouldHandleNullSchedule() {
        Destination destination = new Destination();
        destination.setId(1L);

        RestaurantRequest request = new RestaurantRequest();
        request.setName("Legacy Diner");
        request.setCuisine(Cuisine.MULTI_CUISINE);
        request.setVegetarian(true);
        request.setPriceRange(PriceRange.BUDGET);
        request.setOpeningHours("08:00-22:00");

        Restaurant restaurant = restaurantMapper.toEntity(request, destination);

        assertThat(restaurant.getOpeningHours()).isEqualTo("08:00-22:00");
        assertThat(restaurant.getOpeningTime()).isNull();
        assertThat(restaurant.getClosingTime()).isNull();
        assertThat(restaurant.getSecondOpeningTime()).isNull();
        assertThat(restaurant.getSecondClosingTime()).isNull();
        assertThat(restaurant.getClosedDays()).isNotNull().isEmpty();
    }
}
