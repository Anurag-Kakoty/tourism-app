package com.tourism.backend.ai.client;

import com.tourism.backend.ai.dto.TripDayCalendarEntry;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper utility for AI model clients to construct calendar day breakdowns
 * and provide consistent schedule and operating-hours prompt instructions.
 */
public final class AiPromptScheduleHelper {

    /**
     * Standardized prompt instructions for attraction operating hours and closed-day constraints.
     * Aligned with {@link com.tourism.backend.attraction.util.AttractionScheduleValidator}.
     */
    public static final String SCHEDULE_RULES = """
            - Calendar Mapping: Each dayNumber corresponds to a specific calendar date and day of the week shown in the TRIP CALENDAR.
            - Closed Days: Check each attraction's "closedDays" list. NEVER schedule an attraction on a day of the week when it is closed (e.g. if closedDays contains "MONDAY", you must never schedule that attraction on a Monday). An empty or null closedDays list indicates no known weekly closed days.
            - Operating Hours: For attractions specifying "openingTime" and "closingTime" (in 24-hour HH:mm:ss format), the activity start "time" MUST strictly satisfy the attraction's operating hours:
              * Standard daytime hours (openingTime < closingTime): activity time must be at or after openingTime and strictly before closingTime (openingTime <= time < closingTime). Do not schedule before opening or at/after closing.
              * Overnight hours (openingTime > closingTime): activity time must be at or after openingTime OR strictly before closingTime (time >= openingTime || time < closingTime).
              * 24-hour access (openingTime equals closingTime, e.g. 00:00:00 and 00:00:00): any time is permitted.
              * Unknown operating hours (openingTime or closingTime is null): operating hours are unverified and unconstrained; do NOT invent or assume arbitrary opening/closing hours for that attraction. Schedule activities during typical daytime travel hours without claiming unverified hours are verified.
            - Restaurants: For restaurants with specified closedDays or operating time windows (openingTime/closingTime or secondOpeningTime/secondClosingTime), respect their hours similarly. If null, hours are unconstrained.""";

    private AiPromptScheduleHelper() {
        // Utility class
    }

    /**
     * Builds an ordered list of calendar day entries from the trip start date to end date (inclusive).
     *
     * @param startDate the trip start date
     * @param endDate   the trip end date
     * @return an unmodifiable list of {@link TripDayCalendarEntry}, or empty list if dates are invalid
     */
    public static List<TripDayCalendarEntry> buildTripCalendar(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return List.of();
        }

        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<TripDayCalendarEntry> calendar = new ArrayList<>((int) numberOfDays);

        for (int day = 1; day <= numberOfDays; day++) {
            LocalDate date = startDate.plusDays(day - 1);
            calendar.add(new TripDayCalendarEntry(day, date, date.getDayOfWeek()));
        }

        return Collections.unmodifiableList(calendar);
    }

    /**
     * Formats the trip calendar into human-readable lines for LLM prompt injection.
     * Example:
     * Day 1: 2026-10-12 (MONDAY)
     * Day 2: 2026-10-13 (TUESDAY)
     *
     * @param calendar list of calendar entries
     * @return formatted string
     */
    public static String formatTripCalendarText(List<TripDayCalendarEntry> calendar) {
        if (calendar == null || calendar.isEmpty()) {
            return "No trip days specified.";
        }

        return calendar.stream()
                .map(TripDayCalendarEntry::toDisplayString)
                .collect(Collectors.joining("\n"));
    }
}

