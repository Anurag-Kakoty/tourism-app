package com.tourism.backend.ai.validator;

/**
 * Classifies categories of itinerary schedule and structural violations.
 */
public enum ScheduleViolationType {
    /**
     * An attraction or restaurant was scheduled on a day of the week when it is closed.
     */
    CLOSED_DAY,

    /**
     * An attraction or restaurant was scheduled outside its known operating hours (daytime or overnight).
     */
    OUTSIDE_OPERATING_HOURS,

    /**
     * Multiple activities on the same day were scheduled with the exact same start time.
     */
    TIME_COLLISION,

    /**
     * Structural or reference error (e.g. invalid day number, missing time, unknown candidate ID).
     */
    INVALID_STRUCTURE
}

