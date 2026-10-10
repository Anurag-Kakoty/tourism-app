package com.tourism.backend.accommodation.util;

import java.time.LocalTime;

/**
 * Utility validator for accommodation schedule configurations.
 *
 * <p>Supports optional standard check-in ({@code checkInTime}) and check-out ({@code checkOutTime})
 * times for accommodation properties.
 *
 * <p>Both fields are nullable and unconstrained, allowing check-in-only, check-out-only,
 * or full check-in and check-out schedules without arbitrary restrictions.
 *
 * <p><strong>Note on Itinerary Scheduling:</strong> Unlike attraction operating hours or restaurant
 * meal sessions, accommodations host overnight stays where arrival (check-in) and departure (check-out)
 * occur on distinct days of a stay. Because the current {@code ItineraryItem} model associates only a
 * single point-in-time timestamp with an activity and does not distinguish arrival/check-in from
 * departure/check-out or room resting, check-in and check-out times are not enforced as blocking
 * boundaries against individual itinerary item timestamps.
 */
public final class AccommodationScheduleValidator {

    private AccommodationScheduleValidator() {
        // Utility class
    }

    /**
     * Validates that the supplied check-in and check-out times form a valid configuration.
     * All nullable combinations are permitted without arbitrary restrictions.
     *
     * @param checkInTime  standard check-in time (optional)
     * @param checkOutTime standard check-out time (optional)
     */
    public static void validateSchedule(LocalTime checkInTime, LocalTime checkOutTime) {
        // Permissive: check-in and check-out times are optional and unconstrained.
    }
}
