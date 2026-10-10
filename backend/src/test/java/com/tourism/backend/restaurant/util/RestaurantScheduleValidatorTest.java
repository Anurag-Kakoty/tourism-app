package com.tourism.backend.restaurant.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantScheduleValidatorTest {

    @Nested
    @DisplayName("Time Windows Configuration Validation")
    class TimeWindowValidationTests {

        @Test
        @DisplayName("Should accept completely null / unconfigured schedule")
        void shouldAcceptNullSchedule() {
            assertThatCode(() -> RestaurantScheduleValidator.validateTimeWindows(
                    null, null, null, null
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should accept valid single daytime window")
        void shouldAcceptValidDaytimeWindow() {
            assertThatCode(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(11, 0), LocalTime.of(23, 0), null, null
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should accept valid single overnight window")
        void shouldAcceptValidOvernightWindow() {
            assertThatCode(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(18, 0), LocalTime.of(2, 0), null, null
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should accept valid split sessions (lunch and dinner)")
        void shouldAcceptValidSplitSessions() {
            assertThatCode(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(11, 30), LocalTime.of(15, 30),
                    LocalTime.of(18, 30), LocalTime.of(23, 0)
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should accept valid split sessions with overnight dinner")
        void shouldAcceptValidSplitSessionsWithOvernightDinner() {
            assertThatCode(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(12, 0), LocalTime.of(15, 0),
                    LocalTime.of(19, 0), LocalTime.of(2, 0)
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should reject incomplete primary window")
        void shouldRejectIncompletePrimaryWindow() {
            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(11, 0), null, null, null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Both openingTime and closingTime must be provided");

            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    null, LocalTime.of(23, 0), null, null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Both openingTime and closingTime must be provided");
        }

        @Test
        @DisplayName("Should reject incomplete second window")
        void shouldRejectIncompleteSecondWindow() {
            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(11, 0), LocalTime.of(15, 0),
                    LocalTime.of(18, 0), null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Both secondOpeningTime and secondClosingTime must be provided");

            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(11, 0), LocalTime.of(15, 0),
                    null, LocalTime.of(23, 0)
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Both secondOpeningTime and secondClosingTime must be provided");
        }

        @Test
        @DisplayName("Should reject second window without primary window")
        void shouldRejectSecondWindowWithoutPrimaryWindow() {
            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    null, null,
                    LocalTime.of(18, 0), LocalTime.of(23, 0)
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Primary operating window must be defined before defining a second");
        }

        @Test
        @DisplayName("Should reject overlapping sessions where second session starts before primary closes")
        void shouldRejectOverlappingSessions() {
            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(12, 0), LocalTime.of(16, 0),
                    LocalTime.of(15, 0), LocalTime.of(22, 0)
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Second operating window must open after the primary operating window closes");
        }

        @Test
        @DisplayName("Should reject split sessions when primary session is overnight")
        void shouldRejectSplitSessionsWhenPrimaryIsOvernight() {
            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(20, 0), LocalTime.of(4, 0),
                    LocalTime.of(12, 0), LocalTime.of(16, 0)
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Primary operating window must be a daytime session when a second session is defined");
        }

        @Test
        @DisplayName("Should reject overnight second session that spills into primary session opening time")
        void shouldRejectOvernightSecondSessionSpillover() {
            assertThatThrownBy(() -> RestaurantScheduleValidator.validateTimeWindows(
                    LocalTime.of(10, 0), LocalTime.of(14, 0),
                    LocalTime.of(18, 0), LocalTime.of(11, 0)
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Overnight second operating window cannot close after the primary opening time");
        }
    }

    @Nested
    @DisplayName("Closed Days Validation")
    class ClosedDaysTests {

        private final Set<DayOfWeek> closedDays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);

        @Test
        @DisplayName("Should detect closed day")
        void shouldDetectClosedDay() {
            LocalDate monday = LocalDate.of(2026, 10, 12); // Monday
            assertThat(RestaurantScheduleValidator.isClosedOn(monday, closedDays)).isTrue();
        }

        @Test
        @DisplayName("Should detect open day")
        void shouldDetectOpenDay() {
            LocalDate wednesday = LocalDate.of(2026, 10, 14); // Wednesday
            assertThat(RestaurantScheduleValidator.isClosedOn(wednesday, closedDays)).isFalse();
        }

        @Test
        @DisplayName("Should return false when closed days set is null or empty")
        void shouldReturnFalseWhenNoClosedDays() {
            LocalDate monday = LocalDate.of(2026, 10, 12);
            assertThat(RestaurantScheduleValidator.isClosedOn(monday, null)).isFalse();
            assertThat(RestaurantScheduleValidator.isClosedOn(monday, Collections.emptySet())).isFalse();
        }
    }

    @Nested
    @DisplayName("Operating Hours Validation")
    class OperatingHoursTests {

        @Test
        @DisplayName("Should consider unknown schedule as within operating hours")
        void shouldAllowUnknownSchedule() {
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(
                    LocalTime.of(14, 0), null, null, null, null
            )).isTrue();
        }

        @Test
        @DisplayName("Should validate single daytime session correctly")
        void shouldValidateSingleDaytimeSession() {
            LocalTime open = LocalTime.of(11, 0);
            LocalTime close = LocalTime.of(22, 0);

            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(open, open, close, null, null)).isTrue();
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(15, 0), open, close, null, null)).isTrue();
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(21, 59, 59), open, close, null, null)).isTrue();

            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(10, 59, 59), open, close, null, null)).isFalse();
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(close, open, close, null, null)).isFalse();
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(23, 0), open, close, null, null)).isFalse();
        }

        @Test
        @DisplayName("Should validate split sessions correctly (lunch and dinner)")
        void shouldValidateSplitSessions() {
            LocalTime open1 = LocalTime.of(12, 0);
            LocalTime close1 = LocalTime.of(15, 0);
            LocalTime open2 = LocalTime.of(19, 0);
            LocalTime close2 = LocalTime.of(23, 0);

            // During lunch
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(12, 0), open1, close1, open2, close2)).isTrue();
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(13, 30), open1, close1, open2, close2)).isTrue();
            // End of lunch (closing boundary)
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(close1, open1, close1, open2, close2)).isFalse();

            // Between lunch and dinner (afternoon break)
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(16, 0), open1, close1, open2, close2)).isFalse();
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(18, 59), open1, close1, open2, close2)).isFalse();

            // During dinner
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(19, 0), open1, close1, open2, close2)).isTrue();
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(21, 30), open1, close1, open2, close2)).isTrue();
            // End of dinner
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(close2, open1, close1, open2, close2)).isFalse();
            // Late night after dinner
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(23, 30), open1, close1, open2, close2)).isFalse();
        }

        @Test
        @DisplayName("Should validate split sessions with overnight dinner session")
        void shouldValidateSplitSessionsWithOvernightDinner() {
            LocalTime open1 = LocalTime.of(12, 0);
            LocalTime close1 = LocalTime.of(15, 0);
            LocalTime open2 = LocalTime.of(19, 0);
            LocalTime close2 = LocalTime.of(2, 0); // 2 AM

            // Lunch
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(13, 0), open1, close1, open2, close2)).isTrue();
            // Dinner evening
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(20, 0), open1, close1, open2, close2)).isTrue();
            // Dinner post-midnight
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(1, 0), open1, close1, open2, close2)).isTrue();
            // Dinner closing boundary
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(close2, open1, close1, open2, close2)).isFalse();
            // Closed early morning
            assertThat(RestaurantScheduleValidator.isWithinOperatingHours(LocalTime.of(5, 0), open1, close1, open2, close2)).isFalse();
        }
    }

    @Nested
    @DisplayName("Full Schedule Validation with Exception Throwing")
    class FullScheduleValidationTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when scheduled on closed day")
        void shouldThrowOnClosedDay() {
            LocalDate monday = LocalDate.of(2026, 10, 12);
            LocalTime time = LocalTime.of(13, 0);

            assertThatThrownBy(() -> RestaurantScheduleValidator.validateSchedule(
                    "Spice Garden",
                    monday,
                    time,
                    Set.of(DayOfWeek.MONDAY),
                    LocalTime.of(11, 0), LocalTime.of(22, 0),
                    null, null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Cannot schedule 'Spice Garden' on 2026-10-12")
              .hasMessageContaining("closed on MONDAYs");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when between lunch and dinner sessions")
        void shouldThrowBetweenSessions() {
            LocalDate date = LocalDate.of(2026, 10, 13);
            LocalTime teatime = LocalTime.of(16, 30);

            assertThatThrownBy(() -> RestaurantScheduleValidator.validateSchedule(
                    "Grand Thali",
                    date,
                    teatime,
                    Collections.emptySet(),
                    LocalTime.of(11, 30), LocalTime.of(15, 0),
                    LocalTime.of(18, 30), LocalTime.of(23, 0)
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Cannot schedule 'Grand Thali' at 16:30")
              .hasMessageContaining("outside operating hours (11:30 - 15:00, 18:30 - 23:00)");
        }

        @Test
        @DisplayName("Should not throw when scheduled in second session")
        void shouldNotThrowInSecondSession() {
            LocalDate date = LocalDate.of(2026, 10, 13);
            LocalTime dinnerTime = LocalTime.of(20, 0);

            assertThatCode(() -> RestaurantScheduleValidator.validateSchedule(
                    "Grand Thali",
                    date,
                    dinnerTime,
                    Collections.emptySet(),
                    LocalTime.of(11, 30), LocalTime.of(15, 0),
                    LocalTime.of(18, 30), LocalTime.of(23, 0)
            )).doesNotThrowAnyException();
        }
    }
}
