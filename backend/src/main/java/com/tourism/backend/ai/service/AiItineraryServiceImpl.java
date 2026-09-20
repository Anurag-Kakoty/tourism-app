package com.tourism.backend.ai.service;

import com.tourism.backend.ai.client.AiGeneratedItinerary;
import com.tourism.backend.ai.client.AiModelClient;
import com.tourism.backend.ai.dto.AiCandidateContext;
import com.tourism.backend.ai.dto.AiItineraryItemResponse;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.AiItineraryResponse;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.dto.candidate.AccommodationCandidate;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import com.tourism.backend.ai.dto.candidate.FestivalCandidate;
import com.tourism.backend.ai.dto.candidate.GuideCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;
import com.tourism.backend.ai.dto.candidate.TransportCandidate;
import com.tourism.backend.ai.recommendation.RecommendationService;
import com.tourism.backend.itinerary.entity.ActivityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiItineraryServiceImpl
        implements AiItineraryService {

    private final CandidateService candidateService;
    private final RecommendationService recommendationService;
    private final AiModelClient aiModelClient;

    @Override
    public AiItineraryResponse generateItinerary(
            AiItineraryRequest request) {

        validateRequestDates(request);

        /*
         * Step 1:
         * Retrieve only candidates relevant to the requested
         * destination, dates and preferences.
         */
        AiCandidateContext candidates =
                candidateService.getCandidates(request);

        /*
         * Step 2:
         * Apply deterministic recommendation logic before
         * sending candidates to the AI model.
         */
        RecommendedCandidateContext recommendations =
                recommendationService.recommend(
                        request,
                        candidates
                );

        /*
         * Step 3:
         * Ask the configured AI model to generate a draft.
         */
        AiGeneratedItinerary generatedItinerary =
                aiModelClient.generateItinerary(
                        request,
                        recommendations
                );

        /*
         * Step 4:
         * Validate the AI response against the request and
         * the candidates that were actually supplied to it.
         */
        validateGeneratedItinerary(
                request,
                generatedItinerary,
                recommendations
        );

        /*
         * Step 5:
         * Convert the validated AI result into the API response.
         */
        return buildResponse(
                request,
                generatedItinerary,
                recommendations
        );
    }

    private void validateRequestDates(
            AiItineraryRequest request) {

        if (request.getStartDate() == null
                || request.getEndDate() == null) {

            throw new IllegalArgumentException(
                    "Start date and end date are required."
            );
        }

        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }
    }

    private void validateGeneratedItinerary(
            AiItineraryRequest request,
            AiGeneratedItinerary generatedItinerary,
            RecommendedCandidateContext candidates) {

        if (generatedItinerary == null) {
            throw new IllegalArgumentException(
                    "AI model returned no itinerary."
            );
        }

        /*
         * The AI must not change the destination requested
         * by the user.
         */
        if (generatedItinerary.getDestinationId() == null
                || !generatedItinerary.getDestinationId()
                .equals(request.getDestinationId())) {

            throw new IllegalArgumentException(
                    "AI itinerary contains an invalid destination."
            );
        }

        /*
         * The AI must respect the requested travel dates.
         */
        if (!request.getStartDate()
                .equals(generatedItinerary.getStartDate())
                || !request.getEndDate()
                .equals(generatedItinerary.getEndDate())) {

            throw new IllegalArgumentException(
                    "AI itinerary dates do not match the requested dates."
            );
        }

        /*
         * The number of travelers is controlled by the user's
         * request and must not be changed by the AI.
         */
        if (generatedItinerary.getNumberOfTravelers() == null
                || !generatedItinerary.getNumberOfTravelers()
                .equals(request.getNumberOfTravelers())) {

            throw new IllegalArgumentException(
                    "AI itinerary contains an invalid number of travelers."
            );
        }

        /*
         * Estimated budget must be present and non-negative.
         *
         * We do not require it to be <= the user's budget because
         * the AI may legitimately determine that the requested
         * itinerary cannot fit within the supplied budget.
         */
        if (generatedItinerary.getEstimatedBudget() == null
                || generatedItinerary.getEstimatedBudget()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "AI itinerary contains an invalid estimated budget."
            );
        }

        if (generatedItinerary.getItems() == null) {
            throw new IllegalArgumentException(
                    "AI itinerary contains no items."
            );
        }

        long numberOfDays =
                ChronoUnit.DAYS.between(
                        request.getStartDate(),
                        request.getEndDate()
                ) + 1;

        for (AiGeneratedItinerary.GeneratedItem item
                : generatedItinerary.getItems()) {

            validateGeneratedItem(
                    item,
                    numberOfDays,
                    candidates
            );
        }
    }

    private void validateGeneratedItem(
            AiGeneratedItinerary.GeneratedItem item,
            long numberOfDays,
            RecommendedCandidateContext candidates) {

        if (item == null) {
            throw new IllegalArgumentException(
                    "AI itinerary contains an invalid item."
            );
        }

        if (item.getDayNumber() == null
                || item.getDayNumber() < 1
                || item.getDayNumber() > numberOfDays) {

            throw new IllegalArgumentException(
                    "AI itinerary contains an invalid day number."
            );
        }

        if (item.getActivityOrder() == null
                || item.getActivityOrder() < 1) {

            throw new IllegalArgumentException(
                    "AI itinerary contains an invalid activity order."
            );
        }

        if (item.getTime() == null) {
            throw new IllegalArgumentException(
                    "AI itinerary item time is required."
            );
        }

        if (item.getActivityType() == null) {
            throw new IllegalArgumentException(
                    "AI itinerary item activity type is required."
            );
        }

        if (item.getReferenceId() == null) {
            throw new IllegalArgumentException(
                    "AI itinerary item reference ID is required."
            );
        }

        /*
         * Most importantly, the reference must exist in the
         * candidate set supplied to the AI.
         */
        if (!isValidReference(
                item.getActivityType(),
                item.getReferenceId(),
                candidates)) {

            throw new IllegalArgumentException(
                    "AI itinerary contains an invalid reference for activity type "
                            + item.getActivityType()
                            + ": "
                            + item.getReferenceId()
            );
        }
    }

    private boolean isValidReference(
            ActivityType activityType,
            Long referenceId,
            RecommendedCandidateContext candidates) {

        return switch (activityType) {

            case ATTRACTION ->
                    candidates.getAttractions()
                            .stream()
                            .anyMatch(candidate ->
                                    candidate.getId()
                                            .equals(referenceId)
                            );

            case ACCOMMODATION ->
                    candidates.getAccommodations()
                            .stream()
                            .anyMatch(candidate ->
                                    candidate.getId()
                                            .equals(referenceId)
                            );

            case RESTAURANT ->
                    candidates.getRestaurants()
                            .stream()
                            .anyMatch(candidate ->
                                    candidate.getId()
                                            .equals(referenceId)
                            );

            case GUIDE ->
                    candidates.getGuides()
                            .stream()
                            .anyMatch(candidate ->
                                    candidate.getId()
                                            .equals(referenceId)
                            );

            case TRANSPORT ->
                    candidates.getTransport()
                            .stream()
                            .anyMatch(candidate ->
                                    candidate.getId()
                                            .equals(referenceId)
                            );

            case FESTIVAL ->
                    candidates.getFestivals()
                            .stream()
                            .anyMatch(candidate ->
                                    candidate.getFestivalId()
                                            .equals(referenceId)
                            );
        };
    }

    private AiItineraryResponse buildResponse(
            AiItineraryRequest request,
            AiGeneratedItinerary generatedItinerary,
            RecommendedCandidateContext candidates) {

        Map<String, String> referenceNames =
                buildReferenceNameMap(candidates);

        List<AiItineraryItemResponse> items =
                generatedItinerary.getItems()
                        .stream()
                        .map(item -> AiItineraryItemResponse.builder()
                                .dayNumber(item.getDayNumber())
                                .activityOrder(item.getActivityOrder())
                                .time(item.getTime())
                                .activityType(item.getActivityType())
                                .referenceId(item.getReferenceId())
                                .referenceName(
                                        getReferenceName(
                                                item,
                                                referenceNames
                                        )
                                )
                                .notes(item.getNotes())
                                .build())
                        .toList();

        return AiItineraryResponse.builder()
                .title(generatedItinerary.getTitle())
                .description(generatedItinerary.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfTravelers(request.getNumberOfTravelers())
                .estimatedBudget(
                        generatedItinerary.getEstimatedBudget()
                )
                .destinationId(request.getDestinationId())
                .items(items)
                .build();
    }

    private Map<String, String> buildReferenceNameMap(
            RecommendedCandidateContext candidates) {

        Map<String, String> names = new HashMap<>();

        for (AttractionCandidate candidate :
                candidates.getAttractions()) {

            names.put(
                    createKey(
                            ActivityType.ATTRACTION,
                            candidate.getId()
                    ),
                    candidate.getName()
            );
        }

        for (AccommodationCandidate candidate :
                candidates.getAccommodations()) {

            names.put(
                    createKey(
                            ActivityType.ACCOMMODATION,
                            candidate.getId()
                    ),
                    candidate.getName()
            );
        }

        for (RestaurantCandidate candidate :
                candidates.getRestaurants()) {

            names.put(
                    createKey(
                            ActivityType.RESTAURANT,
                            candidate.getId()
                    ),
                    candidate.getName()
            );
        }

        for (GuideCandidate candidate :
                candidates.getGuides()) {

            names.put(
                    createKey(
                            ActivityType.GUIDE,
                            candidate.getId()
                    ),
                    candidate.getName()
            );
        }

        for (TransportCandidate candidate :
                candidates.getTransport()) {

            names.put(
                    createKey(
                            ActivityType.TRANSPORT,
                            candidate.getId()
                    ),
                    candidate.getProviderName()
            );
        }

        for (FestivalCandidate candidate :
                candidates.getFestivals()) {

            names.put(
                    createKey(
                            ActivityType.FESTIVAL,
                            candidate.getFestivalId()
                    ),
                    candidate.getFestivalName()
            );
        }

        return names;
    }

    private String getReferenceName(
            AiGeneratedItinerary.GeneratedItem item,
            Map<String, String> referenceNames) {

        String key =
                createKey(
                        item.getActivityType(),
                        item.getReferenceId()
                );

        String referenceName =
                referenceNames.get(key);

        if (referenceName == null) {
            throw new IllegalArgumentException(
                    "Reference name could not be resolved for activity type "
                            + item.getActivityType()
                            + " and reference ID "
                            + item.getReferenceId()
            );
        }

        return referenceName;
    }

    private String createKey(
            ActivityType activityType,
            Long referenceId) {

        return activityType.name()
                + ":"
                + referenceId;
    }
}