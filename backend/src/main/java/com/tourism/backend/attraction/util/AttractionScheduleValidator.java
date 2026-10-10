package com.tourism.backend.attraction.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Utility validator for attraction schedules, checking visit dates against known closed days
 * and activity start times against known operating hours.
 *
 * <p>Unknown or unverified schedules (e.g. null opening/closing times or empty closed days)
 * are treated as unconstrained and will not cause validation rejection.
 *
 * <p>Assumes uniform operating hours across all open days of the week.
 *
 * <p><strong>Limitation (Overnight Closed-Day Attribution):</strong> Closed days are evaluated
 * strictly against the calendar date of the scheduled activity ({@code visitDate = startDate + dayNumber - 1}).
 * For attractions with overnight operating hours ({@code openingTime > closingTime}), post-midnight visits
 * (e.g. 02:00) are checked against the calendar day of that post-midnight visit rather than the shift-origin day.
 * This is an intentional simplification for v1; do not expand unless verified data requires overnight shifts.
 */
public final class AttractionScheduleValidator {

    private AttractionScheduleValidator() {
        // Utility class
    }

    /**
     * Checks if the attraction is closed on the given visit date.
     *
     * @param visitDate  the date of the planned visit
     * @param closedDays set of days when the attraction is closed
     * @return true if the attraction is known to be closed on the visit date; false otherwise
     */
    public static boolean isClosedOn(LocalDate visitDate, Set<DayOfWeek> closedDays) {
        if (visitDate == null || closedDays == null || closedDays.isEmpty()) {
            return false;
        }
        return closedDays.contains(visitDate.getDayOfWeek());
    }

    /**
     * Checks whether an activity time falls within the attraction's operating hours.
     *
     * <p>Rules:
     * <ul>
     *   <li>If activityTime, openingTime, or closingTime is null, the schedule is unconstrained -> returns true.</li>
     *   <li>If openingTime == closingTime, 24-hour access is assumed -> returns true.</li>
     *   <li>If openingTime &lt; closingTime (standard daytime): time must be &gt;= openingTime and &lt; closingTime.</li>
     *   <li>If openingTime &gt; closingTime (overnight): time must be &gt;= openingTime or &lt; closingTime.</li>
     * </ul>
     *
     * @param activityTime planned start time of the activity
     * @param openingTime  daily opening time
     * @param closingTime  daily closing time
     * @return true if valid or unknown; false if strictly outside operating hours
     */
    public static boolean isWithinOperatingHours(
            LocalTime activityTime,
            LocalTime openingTime,
            LocalTime closingTime) {

        if (activityTime == null || openingTime == null || closingTime == null) {
            return true;
        }

        if (openingTime.equals(closingTime)) {
            return true;
        }

        if (openingTime.isBefore(closingTime)) {
            // Daytime schedule
            return !activityTime.isBefore(openingTime) && activityTime.isBefore(closingTime);
        } else {
            // Overnight schedule
            return !activityTime.isBefore(openingTime) || activityTime.isBefore(closingTime);
        }
    }

    /**
     * Validates that the planned activity date and time respect the attraction's known closed days
     * and operating hours.
     *
     * @param attractionName name of the attraction (for descriptive error messages)
     * @param visitDate      planned date of the visit
     * @param activityTime   planned start time of the activity
     * @param closedDays     known closed days of the week
     * @param openingTime    known daily opening time
     * @param closingTime    known daily closing time
     * @throws IllegalArgumentException if the attraction is closed on that day or outside operating hours
     */
    public static void validateSchedule(
            String attractionName,
            LocalDate visitDate,
            LocalTime activityTime,
            Set<DayOfWeek> closedDays,
            LocalTime openingTime,
            LocalTime closingTime) {

        String name = (attractionName != null && !attractionName.isBlank()) ? attractionName : "Attraction";

        if (isClosedOn(visitDate, closedDays)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot schedule '%s' on %s because it is closed on %ss.",
                    name,
                    visitDate,
                    visitDate.getDayOfWeek()
            ));
        }

        if (!isWithinOperatingHours(activityTime, openingTime, closingTime)) {
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

