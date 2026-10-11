package com.tourism.backend.ai.validator;

import com.tourism.backend.ai.client.AiGeneratedItinerary;
import com.tourism.backend.ai.client.AiGeneratedItinerary.GeneratedItem;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.dto.candidate.AccommodationCandidate;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import com.tourism.backend.ai.dto.candidate.FestivalCandidate;
import com.tourism.backend.ai.dto.candidate.GuideCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;
import com.tourism.backend.ai.dto.candidate.TransportCandidate;
import com.tourism.backend.attraction.util.AttractionScheduleValidator;
import com.tourism.backend.itinerary.entity.ActivityType;
import com.tourism.backend.restaurant.util.RestaurantScheduleValidator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates AI-generated itineraries against structural, candidate, and schedule constraints.
 * Classifies violations and safely corrects minor time deviations when all safety preconditions are met.
 */
@Slf4j
public final class AiItineraryScheduleValidator {

    /**
     * Maximum early arrival deviation in minutes that can be safely adjusted to opening time.
     * Early arrivals up to 60 minutes before gates open can be safely moved forward to opening time,
     * provided the attraction is open on that day, hours are verified, and no time collision occurs.
     */
    public static final int MAX_MINOR_DEVIATION_MINUTES = 60;

    private AiItineraryScheduleValidator() {
        // Utility class
    }

    /**
     * Validates the AI itinerary draft against the user request, supplied candidates, and schedule rules.
     * Applies safe minor time corrections if eligible.
     *
     * @param request            the user request
     * @param generatedItinerary the itinerary draft produced by the AI model
     * @param candidates         the candidate context supplied to the AI model
     * @return {@link ItineraryScheduleValidationResult} indicating success or failure, with adjusted items and violations
     */
    public static ItineraryScheduleValidationResult validateAndCorrect(
            AiItineraryRequest request,
            AiGeneratedItinerary generatedItinerary,
            RecommendedCandidateContext candidates) {

        List<ScheduleViolation> violations = new ArrayList<>();

        if (generatedItinerary == null) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    null, null, null, null, null, "Itinerary",
                    "AI model returned no itinerary."
            ));
            return ItineraryScheduleValidationResult.failure(null, violations);
        }

        // Structural header checks
        validateHeaderFields(request, generatedItinerary, violations);
        if (!violations.isEmpty()) {
            return ItineraryScheduleValidationResult.failure(generatedItinerary, violations);
        }

        long numberOfDays = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        ) + 1;

        List<GeneratedItem> rawItems = generatedItinerary.getItems();
        List<GeneratedItem> adjustedItems = new ArrayList<>(rawItems.size());

        // Validate each item individually
        for (int i = 0; i < rawItems.size(); i++) {
            GeneratedItem item = rawItems.get(i);
            GeneratedItem validatedOrAdjusted = validateAndOptionallyCorrectItem(
                    item,
                    rawItems,
                    numberOfDays,
                    request.getStartDate(),
                    candidates,
                    violations
            );
            adjustedItems.add(validatedOrAdjusted != null ? validatedOrAdjusted : item);
        }

        // Validate distinct start times per day (overlap / time collision detection)
        validateDayTimeCollisions(adjustedItems, violations);

        boolean hasUncorrected = violations.stream().anyMatch(v -> !v.autoCorrected());

        AiGeneratedItinerary finalizedItinerary = generatedItinerary.toBuilder()
                .items(adjustedItems)
                .build();

        if (hasUncorrected) {
            return ItineraryScheduleValidationResult.failure(finalizedItinerary, violations);
        }

        return ItineraryScheduleValidationResult.success(finalizedItinerary, violations);
    }

    private static void validateHeaderFields(
            AiItineraryRequest request,
            AiGeneratedItinerary generatedItinerary,
            List<ScheduleViolation> violations) {

        if (generatedItinerary.getDestinationId() == null
                || !generatedItinerary.getDestinationId().equals(request.getDestinationId())) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    null, null, null, null, null, "Destination",
                    String.format("AI itinerary destination ID (%s) does not match requested destination ID (%s).",
                            generatedItinerary.getDestinationId(), request.getDestinationId())
            ));
        }

        if (!Objects.equals(request.getStartDate(), generatedItinerary.getStartDate())
                || !Objects.equals(request.getEndDate(), generatedItinerary.getEndDate())) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    null, null, null, null, null, "Dates",
                    String.format("AI itinerary dates (%s to %s) do not match requested dates (%s to %s).",
                            generatedItinerary.getStartDate(), generatedItinerary.getEndDate(),
                            request.getStartDate(), request.getEndDate())
            ));
        }

        if (generatedItinerary.getNumberOfTravelers() == null
                || !generatedItinerary.getNumberOfTravelers().equals(request.getNumberOfTravelers())) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    null, null, null, null, null, "Travelers",
                    String.format("AI itinerary travelers (%s) does not match requested travelers (%s).",
                            generatedItinerary.getNumberOfTravelers(), request.getNumberOfTravelers())
            ));
        }

        if (generatedItinerary.getEstimatedBudget() == null
                || generatedItinerary.getEstimatedBudget().compareTo(BigDecimal.ZERO) < 0) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    null, null, null, null, null, "Budget",
                    "AI itinerary estimated budget must be non-negative."
            ));
        }

        if (generatedItinerary.getItems() == null) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    null, null, null, null, null, "Items",
                    "AI itinerary contains no items."
            ));
        }
    }

    private static GeneratedItem validateAndOptionallyCorrectItem(
            GeneratedItem item,
            List<GeneratedItem> allItems,
            long numberOfDays,
            LocalDate startDate,
            RecommendedCandidateContext candidates,
            List<ScheduleViolation> violations) {

        if (item == null) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    null, null, null, null, null, "Item",
                    "AI itinerary contains a null item."
            ));
            return null;
        }

        if (item.getDayNumber() == null || item.getDayNumber() < 1 || item.getDayNumber() > numberOfDays) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), null, item.getTime(), item.getActivityType(), item.getReferenceId(), "Item",
                    String.format("AI itinerary item has invalid dayNumber (%s) for a %d-day trip.",
                            item.getDayNumber(), numberOfDays)
            ));
            return item;
        }

        if (item.getActivityOrder() == null || item.getActivityOrder() < 1) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), null, item.getTime(), item.getActivityType(), item.getReferenceId(), "Item",
                    String.format("AI itinerary item on Day %s has invalid activityOrder (%s).",
                            item.getDayNumber(), item.getActivityOrder())
            ));
            return item;
        }

        if (item.getTime() == null) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), null, null, item.getActivityType(), item.getReferenceId(), "Item",
                    String.format("AI itinerary item on Day %s is missing required start time.", item.getDayNumber())
            ));
            return item;
        }

        if (item.getActivityType() == null) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), null, item.getTime(), null, item.getReferenceId(), "Item",
                    String.format("AI itinerary item on Day %s is missing required activityType.", item.getDayNumber())
            ));
            return item;
        }

        if (item.getReferenceId() == null) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), null, item.getTime(), item.getActivityType(), null, "Item",
                    String.format("AI itinerary item on Day %s is missing required referenceId.", item.getDayNumber())
            ));
            return item;
        }

        LocalDate visitDate = startDate.plusDays(item.getDayNumber() - 1);

        if (item.getActivityType() == ActivityType.ATTRACTION) {
            return validateAndOptionallyCorrectAttraction(item, allItems, visitDate, candidates, violations);
        }

        if (item.getActivityType() == ActivityType.RESTAURANT) {
            return validateAndOptionallyCorrectRestaurant(item, allItems, visitDate, candidates, violations);
        }

        // Other activity types: verify reference existence
        if (!isCandidatePresent(item.getActivityType(), item.getReferenceId(), candidates)) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                    item.getActivityType().name(),
                    String.format("Reference ID %d not found in supplied %s candidates.",
                            item.getReferenceId(), item.getActivityType())
            ));
        }

        return item;
    }

    private static GeneratedItem validateAndOptionallyCorrectAttraction(
            GeneratedItem item,
            List<GeneratedItem> allItems,
            LocalDate visitDate,
            RecommendedCandidateContext candidates,
            List<ScheduleViolation> violations) {

        Optional<AttractionCandidate> opt = candidates.getAttractions().stream()
                .filter(c -> c.getId().equals(item.getReferenceId()))
                .findFirst();

        if (opt.isEmpty()) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                    "Attraction",
                    String.format("Attraction ID %d not found in supplied candidates.", item.getReferenceId())
            ));
            return item;
        }

        AttractionCandidate attraction = opt.get();

        // 1. Closed day check
        if (AttractionScheduleValidator.isClosedOn(visitDate, attraction.getClosedDays())) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.CLOSED_DAY,
                    item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                    attraction.getName(),
                    String.format("Day %d (%s, %s): '%s' is closed on %ss. Do not schedule on closed days.",
                            item.getDayNumber(), visitDate, visitDate.getDayOfWeek(), attraction.getName(), visitDate.getDayOfWeek())
            ));
            return item;
        }

        // 2. Operating hours check
        boolean withinHours = AttractionScheduleValidator.isWithinOperatingHours(
                item.getTime(),
                attraction.getOpeningTime(),
                attraction.getClosingTime()
        );

        if (withinHours) {
            return item;
        }

        // 3. Attempt safe minor time correction
        LocalTime opening = attraction.getOpeningTime();
        LocalTime closing = attraction.getClosingTime();

        if (opening != null && closing != null && item.getTime().isBefore(opening)) {
            long minutesEarly = ChronoUnit.MINUTES.between(item.getTime(), opening);

            if (minutesEarly > 0 && minutesEarly <= MAX_MINOR_DEVIATION_MINUTES) {
                boolean collision = wouldCauseTimeCollision(item, allItems, opening);
                boolean orderInverted = wouldInvertChronologicalOrder(item, allItems, opening);

                if (!collision && !orderInverted) {
                    log.info("Safely corrected attraction '{}' on Day {} from {} to opening time {} ({} mins early).",
                            attraction.getName(), item.getDayNumber(), item.getTime(), opening, minutesEarly);

                    violations.add(ScheduleViolation.corrected(
                            ScheduleViolationType.OUTSIDE_OPERATING_HOURS,
                            item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                            attraction.getName(),
                            String.format("Day %d: Adjusted start time of '%s' from %s to opening time %s (%d mins early).",
                                    item.getDayNumber(), attraction.getName(), item.getTime(), opening, minutesEarly)
                    ));

                    return item.toBuilder().time(opening).build();
                }
            }
        }

        // Cannot be safely corrected
        violations.add(ScheduleViolation.uncorrected(
                ScheduleViolationType.OUTSIDE_OPERATING_HOURS,
                item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                attraction.getName(),
                String.format("Day %d: '%s' scheduled at %s is outside operating hours (%s - %s).",
                        item.getDayNumber(), attraction.getName(), item.getTime(), opening, closing)
        ));

        return item;
    }

    private static GeneratedItem validateAndOptionallyCorrectRestaurant(
            GeneratedItem item,
            List<GeneratedItem> allItems,
            LocalDate visitDate,
            RecommendedCandidateContext candidates,
            List<ScheduleViolation> violations) {

        Optional<RestaurantCandidate> opt = candidates.getRestaurants().stream()
                .filter(c -> c.getId().equals(item.getReferenceId()))
                .findFirst();

        if (opt.isEmpty()) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.INVALID_STRUCTURE,
                    item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                    "Restaurant",
                    String.format("Restaurant ID %d not found in supplied candidates.", item.getReferenceId())
            ));
            return item;
        }

        RestaurantCandidate restaurant = opt.get();

        if (RestaurantScheduleValidator.isClosedOn(visitDate, restaurant.getClosedDays())) {
            violations.add(ScheduleViolation.uncorrected(
                    ScheduleViolationType.CLOSED_DAY,
                    item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                    restaurant.getName(),
                    String.format("Day %d (%s, %s): Restaurant '%s' is closed on %ss.",
                            item.getDayNumber(), visitDate, visitDate.getDayOfWeek(), restaurant.getName(), visitDate.getDayOfWeek())
            ));
            return item;
        }

        boolean withinHours = RestaurantScheduleValidator.isWithinOperatingHours(
                item.getTime(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.getSecondOpeningTime(),
                restaurant.getSecondClosingTime()
        );

        if (withinHours) {
            return item;
        }

        // Attempt minor correction for restaurant session 1
        LocalTime open1 = restaurant.getOpeningTime();
        if (open1 != null && restaurant.getClosingTime() != null && item.getTime().isBefore(open1)) {
            long minutesEarly = ChronoUnit.MINUTES.between(item.getTime(), open1);
            if (minutesEarly > 0 && minutesEarly <= MAX_MINOR_DEVIATION_MINUTES) {
                boolean collision = wouldCauseTimeCollision(item, allItems, open1);
                boolean orderInverted = wouldInvertChronologicalOrder(item, allItems, open1);

                if (!collision && !orderInverted) {
                    log.info("Safely corrected restaurant '{}' on Day {} from {} to opening time {} ({} mins early).",
                            restaurant.getName(), item.getDayNumber(), item.getTime(), open1, minutesEarly);

                    violations.add(ScheduleViolation.corrected(
                            ScheduleViolationType.OUTSIDE_OPERATING_HOURS,
                            item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                            restaurant.getName(),
                            String.format("Day %d: Adjusted start time of restaurant '%s' from %s to opening time %s (%d mins early).",
                                    item.getDayNumber(), restaurant.getName(), item.getTime(), open1, minutesEarly)
                    ));
                    return item.toBuilder().time(open1).build();
                }
            }
        }

        violations.add(ScheduleViolation.uncorrected(
                ScheduleViolationType.OUTSIDE_OPERATING_HOURS,
                item.getDayNumber(), visitDate, item.getTime(), item.getActivityType(), item.getReferenceId(),
                restaurant.getName(),
                String.format("Day %d: Restaurant '%s' scheduled at %s is outside operating hours.",
                        item.getDayNumber(), restaurant.getName(), item.getTime())
        ));

        return item;
    }

    private static void validateDayTimeCollisions(
            List<GeneratedItem> items,
            List<ScheduleViolation> violations) {

        Map<Integer, List<GeneratedItem>> itemsByDay = items.stream()
                .filter(i -> i != null && i.getDayNumber() != null && i.getTime() != null)
                .collect(Collectors.groupingBy(GeneratedItem::getDayNumber));

        for (Map.Entry<Integer, List<GeneratedItem>> entry : itemsByDay.entrySet()) {
            Integer dayNumber = entry.getKey();
            List<GeneratedItem> dayItems = entry.getValue();

            Set<LocalTime> seenTimes = new HashSet<>();
            Set<LocalTime> duplicateTimes = new HashSet<>();

            for (GeneratedItem item : dayItems) {
                if (!seenTimes.add(item.getTime())) {
                    duplicateTimes.add(item.getTime());
                }
            }

            for (LocalTime dupTime : duplicateTimes) {
                violations.add(ScheduleViolation.uncorrected(
                        ScheduleViolationType.TIME_COLLISION,
                        dayNumber, null, dupTime, null, null, "Activities",
                        String.format("Day %d: Multiple activities have the exact same start time (%s). Activities must have distinct start times.",
                                dayNumber, dupTime)
                ));
            }
        }
    }

    /**
     * Checks if setting the activity time to proposedTime causes an identical start time
     * conflict with another activity on the same day.
     */
    private static boolean wouldCauseTimeCollision(
            GeneratedItem item,
            List<GeneratedItem> allItems,
            LocalTime proposedTime) {

        if (item == null || proposedTime == null) {
            return false;
        }

        return allItems.stream()
                .filter(other -> other != null
                        && other != item
                        && Objects.equals(other.getDayNumber(), item.getDayNumber())
                        && other.getTime() != null)
                .anyMatch(other -> proposedTime.equals(other.getTime()));
    }

    /**
     * Checks if moving an activity on the same day to the proposed opening time would invert
     * chronological order with respect to any other activity on that day.
     *
     * <p>Chronological order is inverted if:
     * <ul>
     *   <li>An earlier activity (lower activityOrder) starts after the proposed opening time.</li>
     *   <li>A subsequent activity (higher activityOrder) starts before the proposed opening time.</li>
     * </ul>
     */
    private static boolean wouldInvertChronologicalOrder(
            GeneratedItem item,
            List<GeneratedItem> allItems,
            LocalTime proposedTime) {

        if (item == null || item.getActivityOrder() == null || proposedTime == null) {
            return false;
        }

        return allItems.stream()
                .filter(other -> other != null
                        && other != item
                        && Objects.equals(other.getDayNumber(), item.getDayNumber())
                        && other.getActivityOrder() != null
                        && other.getTime() != null)
                .anyMatch(other -> {
                    if (other.getActivityOrder() < item.getActivityOrder()) {
                        // Preceding activity cannot start after the proposed time
                        return other.getTime().isAfter(proposedTime);
                    } else if (other.getActivityOrder() > item.getActivityOrder()) {
                        // Subsequent activity cannot start before the proposed time
                        return other.getTime().isBefore(proposedTime);
                    }
                    return false;
                });
    }

    private static boolean isCandidatePresent(
            ActivityType activityType,
            Long referenceId,
            RecommendedCandidateContext candidates) {

        return switch (activityType) {
            case ATTRACTION -> candidates.getAttractions().stream().anyMatch(c -> c.getId().equals(referenceId));
            case ACCOMMODATION -> candidates.getAccommodations().stream().anyMatch(c -> c.getId().equals(referenceId));
            case RESTAURANT -> candidates.getRestaurants().stream().anyMatch(c -> c.getId().equals(referenceId));
            case GUIDE -> candidates.getGuides().stream().anyMatch(c -> c.getId().equals(referenceId));
            case TRANSPORT -> candidates.getTransport().stream().anyMatch(c -> c.getId().equals(referenceId));
            case FESTIVAL -> candidates.getFestivals().stream().anyMatch(c -> c.getFestivalId().equals(referenceId));
        };
    }
}

