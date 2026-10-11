package com.tourism.backend.ai.validator;

import com.tourism.backend.ai.client.AiGeneratedItinerary;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates the outcome of validating and safely adjusting an AI-generated itinerary draft.
 */
public record ItineraryScheduleValidationResult(
        boolean valid,
        AiGeneratedItinerary itinerary,
        List<ScheduleViolation> violations
) {

    public static ItineraryScheduleValidationResult success(
            AiGeneratedItinerary itinerary,
            List<ScheduleViolation> violations) {

        return new ItineraryScheduleValidationResult(
                true,
                itinerary,
                Collections.unmodifiableList(violations)
        );
    }

    public static ItineraryScheduleValidationResult failure(
            AiGeneratedItinerary itinerary,
            List<ScheduleViolation> violations) {

        return new ItineraryScheduleValidationResult(
                false,
                itinerary,
                Collections.unmodifiableList(violations)
        );
    }

    /**
     * Produces a concise, semi-colon separated feedback summary of all uncorrected violations
     * suitable for injection into an LLM retry prompt or exception message.
     */
    public String getFeedbackSummary() {
        if (violations == null || violations.isEmpty()) {
            return "No validation errors.";
        }

        return violations.stream()
                .filter(v -> !v.autoCorrected())
                .map(ScheduleViolation::message)
                .collect(Collectors.joining("; "));
    }

    /**
     * Checks if any minor time corrections were successfully applied to the itinerary.
     */
    public boolean hasAutoCorrections() {
        return violations != null && violations.stream().anyMatch(ScheduleViolation::autoCorrected);
    }
}

