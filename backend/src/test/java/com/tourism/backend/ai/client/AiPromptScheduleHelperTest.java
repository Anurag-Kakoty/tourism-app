package com.tourism.backend.ai.client;

import com.tourism.backend.ai.dto.TripDayCalendarEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiPromptScheduleHelperTest {

    @Nested
    @DisplayName("Trip Calendar Builder Tests")
    class TripCalendarBuilderTests {

        @Test
        @DisplayName("Should build single-day calendar mapping day 1 to start date and day of week")
        void shouldBuildSingleDayCalendar() {
            LocalDate date = LocalDate.of(2026, 10, 12); // Monday

            List<TripDayCalendarEntry> calendar = AiPromptScheduleHelper.buildTripCalendar(date, date);

            assertThat(calendar).hasSize(1);
            TripDayCalendarEntry day1 = calendar.getFirst();
            assertThat(day1.dayNumber()).isEqualTo(1);
            assertThat(day1.date()).isEqualTo(date);
            assertThat(day1.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
            assertThat(day1.toDisplayString()).isEqualTo("Day 1: 2026-10-12 (MONDAY)");
        }

        @Test
        @DisplayName("Should build multi-day calendar across weekend with correct day numbers and days of week")
        void shouldBuildMultiDayCalendar() {
            LocalDate startDate = LocalDate.of(2026, 10, 16); // Friday
            LocalDate endDate = LocalDate.of(2026, 10, 19);   // Monday

            List<TripDayCalendarEntry> calendar = AiPromptScheduleHelper.buildTripCalendar(startDate, endDate);

            assertThat(calendar).hasSize(4);

            assertThat(calendar.get(0).dayNumber()).isEqualTo(1);
            assertThat(calendar.get(0).date()).isEqualTo(LocalDate.of(2026, 10, 16));
            assertThat(calendar.get(0).dayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);

            assertThat(calendar.get(1).dayNumber()).isEqualTo(2);
            assertThat(calendar.get(1).date()).isEqualTo(LocalDate.of(2026, 10, 17));
            assertThat(calendar.get(1).dayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);

            assertThat(calendar.get(2).dayNumber()).isEqualTo(3);
            assertThat(calendar.get(2).date()).isEqualTo(LocalDate.of(2026, 10, 18));
            assertThat(calendar.get(2).dayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);

            assertThat(calendar.get(3).dayNumber()).isEqualTo(4);
            assertThat(calendar.get(3).date()).isEqualTo(LocalDate.of(2026, 10, 19));
            assertThat(calendar.get(3).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        }

        @Test
        @DisplayName("Should build calendar across month and year boundary")
        void shouldBuildCalendarAcrossYearBoundary() {
            LocalDate startDate = LocalDate.of(2026, 12, 30); // Wednesday
            LocalDate endDate = LocalDate.of(2027, 1, 2);     // Saturday

            List<TripDayCalendarEntry> calendar = AiPromptScheduleHelper.buildTripCalendar(startDate, endDate);

            assertThat(calendar).hasSize(4);
            assertThat(calendar.get(0).date()).isEqualTo(LocalDate.of(2026, 12, 30));
            assertThat(calendar.get(0).dayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
            assertThat(calendar.get(3).date()).isEqualTo(LocalDate.of(2027, 1, 2));
            assertThat(calendar.get(3).dayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);
        }

        @Test
        @DisplayName("Should return empty list when start date or end date is null")
        void shouldReturnEmptyListWhenDatesAreNull() {
            assertThat(AiPromptScheduleHelper.buildTripCalendar(null, LocalDate.now())).isEmpty();
            assertThat(AiPromptScheduleHelper.buildTripCalendar(LocalDate.now(), null)).isEmpty();
            assertThat(AiPromptScheduleHelper.buildTripCalendar(null, null)).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when end date is before start date")
        void shouldReturnEmptyListWhenEndDateBeforeStartDate() {
            LocalDate startDate = LocalDate.of(2026, 10, 15);
            LocalDate endDate = LocalDate.of(2026, 10, 10);

            assertThat(AiPromptScheduleHelper.buildTripCalendar(startDate, endDate)).isEmpty();
        }

        @Test
        @DisplayName("Should return unmodifiable list")
        void shouldReturnUnmodifiableList() {
            LocalDate date = LocalDate.of(2026, 10, 12);
            List<TripDayCalendarEntry> calendar = AiPromptScheduleHelper.buildTripCalendar(date, date);

            assertThatThrownBy(() -> calendar.add(new TripDayCalendarEntry(2, date, DayOfWeek.TUESDAY)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Trip Calendar Formatter Tests")
    class TripCalendarFormatterTests {

        @Test
        @DisplayName("Should format calendar entries into clean multi-line display string")
        void shouldFormatCalendarEntries() {
            List<TripDayCalendarEntry> calendar = List.of(
                    new TripDayCalendarEntry(1, LocalDate.of(2026, 10, 12), DayOfWeek.MONDAY),
                    new TripDayCalendarEntry(2, LocalDate.of(2026, 10, 13), DayOfWeek.TUESDAY)
            );

            String formatted = AiPromptScheduleHelper.formatTripCalendarText(calendar);

            assertThat(formatted).isEqualTo(
                    "Day 1: 2026-10-12 (MONDAY)\nDay 2: 2026-10-13 (TUESDAY)"
            );
        }

        @Test
        @DisplayName("Should handle empty or null calendar gracefully")
        void shouldHandleEmptyOrNullCalendar() {
            assertThat(AiPromptScheduleHelper.formatTripCalendarText(List.of())).isEqualTo("No trip days specified.");
            assertThat(AiPromptScheduleHelper.formatTripCalendarText(null)).isEqualTo("No trip days specified.");
        }
    }

    @Nested
    @DisplayName("Schedule Rules Content Tests")
    class ScheduleRulesContentTests {

        @Test
        @DisplayName("Schedule rules string should contain all required constraints")
        void scheduleRulesShouldContainAllRequiredConstraints() {
            String rules = AiPromptScheduleHelper.SCHEDULE_RULES;

            // Closed days instruction
            assertThat(rules).contains("Closed Days:");
            assertThat(rules).contains("NEVER schedule an attraction on a day of the week when it is closed");

            // Operating hours instructions
            assertThat(rules).contains("Standard daytime hours");
            assertThat(rules).contains("openingTime <= time < closingTime");

            // Overnight and 24-hour schedules
            assertThat(rules).contains("Overnight hours");
            assertThat(rules).contains("24-hour access");

            // Unknown hours constraint (Requirement 4)
            assertThat(rules).contains("Unknown operating hours");
            assertThat(rules).contains("unverified and unconstrained");
            assertThat(rules).contains("do NOT invent or assume arbitrary opening/closing hours");

            // Restaurant instructions
            assertThat(rules).contains("Restaurants:");
        }
    }
}

