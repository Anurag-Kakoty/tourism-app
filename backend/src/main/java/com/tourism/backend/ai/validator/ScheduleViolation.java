package com.tourism.backend.ai.validator;

import com.tourism.backend.itinerary.entity.ActivityType;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a single classified schedule or structural violation encountered during AI itinerary validation.
 */
public record ScheduleViolation(
        ScheduleViolationType type,
        Integer dayNumber,
        LocalDate date,
        LocalTime scheduledTime,
        ActivityType activityType,
        Long referenceId,
        String entityName,
        String message,
        boolean autoCorrected
) {

    public static ScheduleViolation uncorrected(
            ScheduleViolationType type,
            Integer dayNumber,
            LocalDate date,
            LocalTime scheduledTime,
            ActivityType activityType,
            Long referenceId,
            String entityName,
            String message) {

        return new ScheduleViolation(
                type,
                dayNumber,
                date,
                scheduledTime,
                activityType,
                referenceId,
                entityName,
                message,
                false
        );
    }

    public static ScheduleViolation corrected(
            ScheduleViolationType type,
            Integer dayNumber,
            LocalDate date,
            LocalTime scheduledTime,
            ActivityType activityType,
            Long referenceId,
            String entityName,
            String message) {

        return new ScheduleViolation(
                type,
                dayNumber,
                date,
                scheduledTime,
                activityType,
                referenceId,
                entityName,
                message,
                true
        );
    }
}

