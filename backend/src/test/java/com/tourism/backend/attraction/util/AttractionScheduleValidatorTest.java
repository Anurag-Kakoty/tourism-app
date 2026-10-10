package com.tourism.backend.attraction.util;

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

class AttractionScheduleValidatorTest {

    @Nested
    @DisplayName("Unknown or Unconstrained Schedules")
    class UnknownScheduleTests {

        @Test
        @DisplayName("Should not throw when operating hours and closed days are null")
        void shouldNotThrowWhenScheduleIsNull() {
            LocalDate date = LocalDate.of(2026, 10, 12); // Monday
            LocalTime time = LocalTime.of(14, 0);

            assertThatCode(() -> AttractionScheduleValidator.validateSchedule(
                    "Living Root Bridge",
                    date,
                    time,
                    null,
                    null,
                    null
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should not throw when closed days set is empty and hours are null")
        void shouldNotThrowWhenClosedDaysEmptyAndHoursNull() {
            LocalDate date = LocalDate.of(2026, 10, 12);
            LocalTime time = LocalTime.of(8, 0);

            assertThatCode(() -> AttractionScheduleValidator.validateSchedule(
                    "Brahmaputra Cruise",
                    date,
                    time,
                    Collections.emptySet(),
                    null,
                    null
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should not throw when activity time is null")
        void shouldNotThrowWhenActivityTimeIsNull() {
            LocalDate date = LocalDate.of(2026, 10, 13); // Tuesday (open)
            LocalTime open = LocalTime.of(9, 0);
            LocalTime close = LocalTime.of(17, 0);
            Set<DayOfWeek> closedDays = Set.of(DayOfWeek.MONDAY);

            assertThatCode(() -> AttractionScheduleValidator.validateSchedule(
                    "Kaziranga Safari",
                    date,
                    null,
                    closedDays,
                    open,
                    close
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should not throw when visit date is null")
        void shouldNotThrowWhenVisitDateIsNull() {
            LocalTime time = LocalTime.of(10, 0);
            LocalTime open = LocalTime.of(9, 0);
            LocalTime close = LocalTime.of(17, 0);
            Set<DayOfWeek> closedDays = Set.of(DayOfWeek.MONDAY);

            assertThatCode(() -> AttractionScheduleValidator.validateSchedule(
                    "Kaziranga Safari",
                    null,
                    time,
                    closedDays,
                    open,
                    close
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should consider 24-hour schedule (open == close) as valid for any time")
        void shouldAllowAnyTimeWhenOpenEqualsClose() {
            LocalTime open = LocalTime.of(0, 0);
            LocalTime close = LocalTime.of(0, 0);

            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(3, 15), open, close)).isTrue();
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(15, 45), open, close)).isTrue();
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(23, 59), open, close)).isTrue();
        }
    }

    @Nested
    @DisplayName("Closed Days Validation")
    class ClosedDaysTests {

        private final Set<DayOfWeek> closedDays = Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);

        @Test
        @DisplayName("Should detect closed day on Monday")
        void shouldDetectClosedDayOnMonday() {
            LocalDate monday = LocalDate.of(2026, 10, 12); // Monday
            assertThat(AttractionScheduleValidator.isClosedOn(monday, closedDays)).isTrue();
        }

        @Test
        @DisplayName("Should detect open day on Tuesday")
        void shouldDetectOpenDayOnTuesday() {
            LocalDate tuesday = LocalDate.of(2026, 10, 13); // Tuesday
            assertThat(AttractionScheduleValidator.isClosedOn(tuesday, closedDays)).isFalse();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when scheduled on closed day")
        void shouldThrowWhenScheduledOnClosedDay() {
            LocalDate monday = LocalDate.of(2026, 10, 12);
            LocalTime time = LocalTime.of(10, 0);

            assertThatThrownBy(() -> AttractionScheduleValidator.validateSchedule(
                    "Taj Mahal",
                    monday,
                    time,
                    Set.of(DayOfWeek.MONDAY),
                    LocalTime.of(9, 0),
                    LocalTime.of(17, 0)
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Cannot schedule 'Taj Mahal' on 2026-10-12")
              .hasMessageContaining("closed on MONDAYs");
        }
    }

    @Nested
    @DisplayName("Daytime Operating Hours Validation")
    class DaytimeHoursTests {

        private final LocalTime open = LocalTime.of(9, 0);
        private final LocalTime close = LocalTime.of(17, 0);

        @Test
        @DisplayName("Should allow start time equal to opening time")
        void shouldAllowOpeningTime() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(open, open, close)).isTrue();
        }

        @Test
        @DisplayName("Should allow start time during open hours")
        void shouldAllowMiddayTime() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(12, 30), open, close)).isTrue();
        }

        @Test
        @DisplayName("Should allow start time one second before closing time")
        void shouldAllowJustBeforeClosing() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(16, 59, 59), open, close)).isTrue();
        }

        @Test
        @DisplayName("Should reject start time strictly before opening time")
        void shouldRejectBeforeOpening() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(8, 59, 59), open, close)).isFalse();
        }

        @Test
        @DisplayName("Should reject start time exactly at closing time")
        void shouldRejectAtClosingTime() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(close, open, close)).isFalse();
        }

        @Test
        @DisplayName("Should reject start time after closing time")
        void shouldRejectAfterClosingTime() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(17, 1), open, close)).isFalse();
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(21, 0), open, close)).isFalse();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException with descriptive error when outside daytime hours")
        void shouldThrowWhenOutsideDaytimeHours() {
            LocalDate date = LocalDate.of(2026, 10, 13); // Tuesday (open)
            LocalTime earlyTime = LocalTime.of(7, 30);

            assertThatThrownBy(() -> AttractionScheduleValidator.validateSchedule(
                    "Red Fort",
                    date,
                    earlyTime,
                    Collections.emptySet(),
                    open,
                    close
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Cannot schedule 'Red Fort' at 07:30")
              .hasMessageContaining("outside operating hours (09:00 - 17:00)");
        }
    }

    @Nested
    @DisplayName("Overnight Operating Hours Validation")
    class OvernightHoursTests {

        private final LocalTime open = LocalTime.of(20, 0); // 8 PM
        private final LocalTime close = LocalTime.of(4, 0);  // 4 AM

        @Test
        @DisplayName("Should allow start time equal to evening opening time")
        void shouldAllowEveningOpeningTime() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(open, open, close)).isTrue();
        }

        @Test
        @DisplayName("Should allow start time late in the night")
        void shouldAllowLateNight() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(23, 30), open, close)).isTrue();
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(0, 0), open, close)).isTrue();
        }

        @Test
        @DisplayName("Should allow start time in the early morning before closing")
        void shouldAllowEarlyMorningBeforeClosing() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(3, 59, 59), open, close)).isTrue();
        }

        @Test
        @DisplayName("Should reject start time exactly at morning closing time")
        void shouldRejectAtMorningClosingTime() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(close, open, close)).isFalse();
        }

        @Test
        @DisplayName("Should reject start time during closed daytime hours")
        void shouldRejectDuringDaytime() {
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(12, 0), open, close)).isFalse();
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(19, 59, 59), open, close)).isFalse();
            assertThat(AttractionScheduleValidator.isWithinOperatingHours(LocalTime.of(4, 30), open, close)).isFalse();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when outside overnight hours")
        void shouldThrowWhenOutsideOvernightHours() {
            LocalDate date = LocalDate.of(2026, 10, 13);
            LocalTime noon = LocalTime.of(12, 0);

            assertThatThrownBy(() -> AttractionScheduleValidator.validateSchedule(
                    "Night Safari",
                    date,
                    noon,
                    Collections.emptySet(),
                    open,
                    close
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("outside operating hours (20:00 - 04:00)");
        }
    }

    @Nested
    @DisplayName("Overnight Operating Hours and Closed Days Interaction")
    class OvernightWithClosedDaysTests {

        private final LocalTime open = LocalTime.of(20, 0); // 8 PM
        private final LocalTime close = LocalTime.of(4, 0);  // 4 AM
        private final Set<DayOfWeek> closedMondayOnly = Set.of(DayOfWeek.MONDAY);
        private final Set<DayOfWeek> closedTuesdayOnly = Set.of(DayOfWeek.TUESDAY);

        @Test
        @DisplayName("Should reject Monday evening visit when Monday is a closed day")
        void shouldRejectMondayEveningWhenMondayIsClosed() {
            LocalDate monday = LocalDate.of(2026, 10, 12); // Monday
            LocalTime eveningTime = LocalTime.of(21, 0);

            assertThatThrownBy(() -> AttractionScheduleValidator.validateSchedule(
                    "Night Observatory",
                    monday,
                    eveningTime,
                    closedMondayOnly,
                    open,
                    close
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("closed on MONDAYs");
        }

        @Test
        @DisplayName("Should evaluate Tuesday 02:00 against Tuesday's open status under calendar-day model")
        void shouldAllowTuesdayEarlyMorningWhenTuesdayIsOpen() {
            LocalDate tuesday = LocalDate.of(2026, 10, 13); // Tuesday (open)
            LocalTime postMidnight = LocalTime.of(2, 0);

            // In the v1 calendar-day model, Tuesday 02:00 is validated against Tuesday (which is open)
            assertThatCode(() -> AttractionScheduleValidator.validateSchedule(
                    "Night Observatory",
                    tuesday,
                    postMidnight,
                    closedMondayOnly,
                    open,
                    close
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should reject Tuesday 02:00 when Tuesday is a closed day under calendar-day model")
        void shouldRejectTuesdayEarlyMorningWhenTuesdayIsClosed() {
            LocalDate tuesday = LocalDate.of(2026, 10, 13); // Tuesday (closed)
            LocalTime postMidnight = LocalTime.of(2, 0);

            // In the v1 calendar-day model, visitDate=Tuesday evaluates against closedDays containing TUESDAY
            assertThatThrownBy(() -> AttractionScheduleValidator.validateSchedule(
                    "Night Observatory",
                    tuesday,
                    postMidnight,
                    closedTuesdayOnly,
                    open,
                    close
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("closed on TUESDAYs");
        }
    }
}

