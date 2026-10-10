package com.tourism.backend.accommodation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatCode;

class AccommodationScheduleValidatorTest {

    @Test
    @DisplayName("Should accept unconfigured / null check-in and check-out times")
    void shouldAcceptNullSchedule() {
        assertThatCode(() -> AccommodationScheduleValidator.validateSchedule(null, null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept check-in time when check-out time is null")
    void shouldAcceptCheckInOnly() {
        assertThatCode(() -> AccommodationScheduleValidator.validateSchedule(LocalTime.of(14, 0), null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept check-out time when check-in time is null")
    void shouldAcceptCheckOutOnly() {
        assertThatCode(() -> AccommodationScheduleValidator.validateSchedule(null, LocalTime.of(11, 0)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept standard hotel schedule where check-in is afternoon and check-out is next morning")
    void shouldAcceptStandardHotelSchedule() {
        assertThatCode(() -> AccommodationScheduleValidator.validateSchedule(
                LocalTime.of(14, 0),
                LocalTime.of(11, 0)
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept day-use schedule where check-in is morning and check-out is evening")
    void shouldAcceptDayUseSchedule() {
        assertThatCode(() -> AccommodationScheduleValidator.validateSchedule(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept identical check-in and check-out times without arbitrary restriction")
    void shouldAcceptIdenticalCheckInAndCheckOut() {
        assertThatCode(() -> AccommodationScheduleValidator.validateSchedule(
                LocalTime.of(12, 0),
                LocalTime.of(12, 0)
        )).doesNotThrowAnyException();
    }
}

