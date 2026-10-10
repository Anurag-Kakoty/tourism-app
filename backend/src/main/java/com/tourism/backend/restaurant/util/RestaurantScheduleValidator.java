package com.tourism.backend.restaurant.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Utility validator for restaurant operating schedules.
 *
 * <p>Supports an optional primary operating window ({@code openingTime}, {@code closingTime})
 * and an optional second operating window ({@code secondOpeningTime}, {@code secondClosingTime})
 * for split sessions (e.g. lunch and dinner), along with weekly closed days.
 *
 * <p>Unknown or unverified schedules (e.g. null opening/closing times or empty closed days)
 * are treated as unconstrained and will not cause validation rejection.
 *
 * <p><strong>Limitation (Overnight Closed-Day Attribution):</strong> Closed days are evaluated
 * strictly against the calendar date of the scheduled activity ({@code visitDate = startDate + dayNumber - 1}).
 * For overnight shifts ({@code open > close}), post-midnight visits are checked against the calendar
 * day of that visit rather than the shift-origin day.
 */
public final class RestaurantScheduleValidator {

    private RestaurantScheduleValidator() {
        // Utility class
    }

    /**
     * Validates that the supplied operating windows form a sensible, non-contradictory configuration.
     *
     * @param openingTime        primary opening time
     * @param closingTime        primary closing time
     * @param secondOpeningTime  second opening time (optional)
     * @param secondClosingTime  second closing time (optional)
     * @throws IllegalArgumentException if windows are incomplete or contradictory
     */
    public static void validateTimeWindows(
            LocalTime openingTime,
            LocalTime closingTime,
            LocalTime secondOpeningTime,
            LocalTime secondClosingTime) {

        // Validate completeness of primary window
        if ((openingTime == null && closingTime != null) || (openingTime != null && closingTime == null)) {
            throw new IllegalArgumentException(
                    "Both openingTime and closingTime must be provided if either is specified."
            );
        }

        // Validate completeness of second window
        if ((secondOpeningTime == null && secondClosingTime != null)
                || (secondOpeningTime != null && secondClosingTime == null)) {
            throw new IllegalArgumentException(
                    "Both secondOpeningTime and secondClosingTime must be provided if either is specified."
            );
        }

        // Second window cannot exist without primary window
        if (secondOpeningTime != null && openingTime == null) {
            throw new IllegalArgumentException(
                    "Primary operating window must be defined before defining a second operating window."
            );
        }

        // If second window is not defined, primary window can be daytime, overnight, or 24h
        if (secondOpeningTime == null) {
            return;
        }

        // If second window is defined:
        if (openingTime.equals(closingTime)) {
            throw new IllegalArgumentException(
                    "Cannot define a second operating window when the primary window covers 24 hours."
            );
        }

        if (secondOpeningTime.equals(secondClosingTime)) {
            throw new IllegalArgumentException(
                    "Second operating window cannot have identical opening and closing times."
            );
        }

        // Primary window must be a daytime session when split sessions are used
        if (openingTime.isAfter(closingTime)) {
            throw new IllegalArgumentException(
                    "Primary operating window must be a daytime session when a second session is defined."
            );
        }

        // Second session must start strictly after the primary session closes
        if (!secondOpeningTime.isAfter(closingTime)) {
            throw new IllegalArgumentException(
                    "Second operating window must open after the primary operating window closes."
            );
        }

        // If second session is overnight, its closing time cannot spill into the primary session's opening time
        if (secondOpeningTime.isAfter(secondClosingTime)) {
            if (secondClosingTime.isAfter(openingTime)) {
                throw new IllegalArgumentException(
                        "Overnight second operating window cannot close after the primary opening time."
                );
            }
        }
    }

    /**
     * Checks if the restaurant is closed on the given visit date.
     *
     * @param visitDate  the date of the planned visit
     * @param closedDays set of days when the restaurant is closed
     * @return true if the restaurant is known to be closed on the visit date; false otherwise
     */
    public static boolean isClosedOn(LocalDate visitDate, Set<DayOfWeek> closedDays) {
        if (visitDate == null || closedDays == null || closedDays.isEmpty()) {
            return false;
        }
        return closedDays.contains(visitDate.getDayOfWeek());
    }

    /**
     * Checks whether an activity time falls within a specific operating window [open, close).
     *
     * @param time  activity time
     * @param open  window opening time
     * @param close window closing time
     * @return true if time falls within [open, close)
     */
    public static boolean isWithinWindow(LocalTime time, LocalTime open, LocalTime close) {
        if (time == null || open == null || close == null) {
            return false;
        }
        if (open.equals(close)) {
            return true;
        }
        if (open.isBefore(close)) {
            // Daytime window
            return !time.isBefore(open) && time.isBefore(close);
        } else {
            // Overnight window
            return !time.isBefore(open) || time.isBefore(close);
        }
    }

    /**
     * Checks whether an activity time falls within any of the restaurant's known operating sessions.
     *
     * <p>If operating hours are not configured (null), the restaurant is treated as unrestricted/unknown
     * and returns true.
     *
     * @param activityTime       planned start time
     * @param openingTime        primary opening time
     * @param closingTime        primary closing time
     * @param secondOpeningTime  second opening time (optional)
     * @param secondClosingTime  second closing time (optional)
     * @return true if within hours or unknown; false if strictly outside operating hours
     */
    public static boolean isWithinOperatingHours(
            LocalTime activityTime,
            LocalTime openingTime,
            LocalTime closingTime,
            LocalTime secondOpeningTime,
            LocalTime secondClosingTime) {

        if (activityTime == null || openingTime == null || closingTime == null) {
            return true;
        }

        boolean inPrimary = isWithinWindow(activityTime, openingTime, closingTime);
        if (inPrimary) {
            return true;
        }

        if (secondOpeningTime != null && secondClosingTime != null) {
            return isWithinWindow(activityTime, secondOpeningTime, secondClosingTime);
        }

        return false;
    }

    /**
     * Validates that the planned activity date and time respect the restaurant's known closed days
     * and operating sessions.
     *
     * @param restaurantName     name of the restaurant (for descriptive error messages)
     * @param visitDate          planned date of the visit
     * @param activityTime       planned start time of the activity
     * @param closedDays         known closed days of the week
     * @param openingTime        primary daily opening time
     * @param closingTime        primary daily closing time
     * @param secondOpeningTime  second daily opening time (optional)
     * @param secondClosingTime  second daily closing time (optional)
     * @throws IllegalArgumentException if the restaurant is closed on that day or outside operating hours
     */
    public static void validateSchedule(
            String restaurantName,
            LocalDate visitDate,
            LocalTime activityTime,
            Set<DayOfWeek> closedDays,
            LocalTime openingTime,
            LocalTime closingTime,
            LocalTime secondOpeningTime,
            LocalTime secondClosingTime) {

        String name = (restaurantName != null && !restaurantName.isBlank()) ? restaurantName : "Restaurant";

        if (isClosedOn(visitDate, closedDays)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot schedule '%s' on %s because it is closed on %ss.",
                    name,
                    visitDate,
                    visitDate.getDayOfWeek()
            ));
        }

        if (!isWithinOperatingHours(activityTime, openingTime, closingTime, secondOpeningTime, secondClosingTime)) {
            if (secondOpeningTime != null && secondClosingTime != null) {
                throw new IllegalArgumentException(String.format(
                        "Cannot schedule '%s' at %s because it is outside operating hours (%s - %s, %s - %s).",
                        name,
                        activityTime,
                        openingTime,
                        closingTime,
                        secondOpeningTime,
                        secondClosingTime
                ));
            } else {
                throw new IllegalArgumentException(String.format(
                        "Cannot schedule '%s' at %s because it is outside operating hours (%s - %s).",
                        name,
                        activityTime,
                        openingTime,
                        closingTime
                ));
            }
        }
    }
}
