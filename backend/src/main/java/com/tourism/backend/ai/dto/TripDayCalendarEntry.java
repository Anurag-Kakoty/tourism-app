package com.tourism.backend.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Represents a calendar day in an itinerary trip, mapping the sequential day number (1-based)
 * to its calendar date and day of the week.
 */
public record TripDayCalendarEntry(
        int dayNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        DayOfWeek dayOfWeek
) {

    /**
     * Formats this calendar entry into a clear, human-readable display string for AI prompts.
     * Example: "Day 1: 2026-10-12 (MONDAY)"
     */
    public String toDisplayString() {
        return String.format("Day %d: %s (%s)", dayNumber, date, dayOfWeek);
    }
}

