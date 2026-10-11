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
import com.tourism.backend.ai.validator.AiItineraryScheduleValidator;
import com.tourism.backend.ai.validator.ItineraryScheduleValidationResult;
import com.tourism.backend.itinerary.entity.ActivityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
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
         * Auto-correct minor early arrivals if safe, or retry once with feedback.
         */
        generatedItinerary =
                validateAndRecoverItinerary(
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

    private AiGeneratedItinerary validateAndRecoverItinerary(
            AiItineraryRequest request,
            AiGeneratedItinerary generatedItinerary,
            RecommendedCandidateContext recommendations) {

        ItineraryScheduleValidationResult validationResult =
                AiItineraryScheduleValidator.validateAndCorrect(
                        request,
                        generatedItinerary,
                        recommendations
                );

        if (!validationResult.valid()) {
            String feedback = validationResult.getFeedbackSummary();
            log.warn(
                    "AI itinerary failed schedule validation on initial attempt: {}. Retrying once with feedback...",
                    feedback
            );

            AiGeneratedItinerary retryItinerary =
                    aiModelClient.generateItinerary(
                            request,
                            recommendations,
                            feedback
                    );

            validationResult =
                    AiItineraryScheduleValidator.validateAndCorrect(
                            request,
                            retryItinerary,
                            recommendations
                    );

            if (!validationResult.valid()) {
                String retryFeedback = validationResult.getFeedbackSummary();
                log.error(
                        "AI itinerary failed schedule validation after retry: {}",
                        retryFeedback
                );
                throw new IllegalArgumentException(
                        "AI itinerary schedule validation failed: " + retryFeedback
                );
            }
        }

        return validationResult.itinerary();
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