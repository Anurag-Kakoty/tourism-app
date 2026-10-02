package com.tourism.backend.ai.recommendation;

import com.tourism.backend.ai.dto.AiCandidateContext;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.dto.TripPreferences;
import com.tourism.backend.ai.dto.candidate.AccommodationCandidate;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import com.tourism.backend.ai.dto.candidate.FestivalCandidate;
import com.tourism.backend.ai.dto.candidate.GuideCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;
import com.tourism.backend.ai.dto.candidate.TransportCandidate;
import com.tourism.backend.geo.model.GeoPoint;
import com.tourism.backend.geo.service.RoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl
        implements RecommendationService {

    private static final int MAX_ATTRACTIONS = 15;
    private static final int MAX_ACCOMMODATIONS = 8;
    private static final int MAX_RESTAURANTS = 8;
    private static final int MAX_GUIDES = 5;
    private static final int MAX_TRANSPORT = 8;
    private static final int MAX_FESTIVALS = 5;

    private static final double MAX_PROXIMITY_SCORE = 3.0;

    private final PreferenceMatcher preferenceMatcher;
    private final RoutingService routingService;

    @Override
    public RecommendedCandidateContext recommend(
            AiItineraryRequest request,
            AiCandidateContext candidates) {

        return RecommendedCandidateContext.builder()
                .attractions(
                        rankAttractions(
                                request,
                                candidates
                        )
                )
                .accommodations(
                        rankAccommodations(
                                request,
                                candidates.getAccommodations()
                        )
                )
                .restaurants(
                        rankRestaurants(
                                request,
                                candidates.getRestaurants()
                        )
                )
                .guides(
                        rankGuides(
                                request,
                                candidates.getGuides()
                        )
                )
                .transport(
                        rankTransport(
                                candidates.getTransport()
                        )
                )
                .festivals(
                        rankFestivals(
                                candidates.getFestivals()
                        )
                )
                .build();
    }

    private List<AttractionCandidate> rankAttractions(
            AiItineraryRequest request,
            AiCandidateContext candidates) {

        List<AttractionCandidate> attractionCandidates =
                candidates.getAttractions();

        if (attractionCandidates == null
                || attractionCandidates.isEmpty()) {

            return List.of();
        }

        Set<Long> selectedExperienceIds =
                request.getExperienceIds() == null
                        ? Set.of()
                        : new HashSet<>(request.getExperienceIds());

        Double destinationLatitude =
                candidates.getDestinationLatitude();

        Double destinationLongitude =
                candidates.getDestinationLongitude();

        List<ScoredAttraction> scored =
                new ArrayList<>();

        for (AttractionCandidate candidate : attractionCandidates) {

            double score = 0.0;

            if (Boolean.TRUE.equals(candidate.getFeatured())) {
                score += 2.0;
            }

            if (candidate.getExperienceIds() != null
                    && !candidate.getExperienceIds().isEmpty()) {

                score += 1.0;
            }

            if (candidate.getTags() != null
                    && !candidate.getTags().isEmpty()) {

                score += 1.0;
            }

            BigDecimal entryFee = candidate.getEntryFee();

            if (entryFee != null) {

                if (entryFee.compareTo(request.getBudget()) <= 0) {
                    score += 1.0;
                }

                if (entryFee.compareTo(BigDecimal.ZERO) == 0) {
                    score += 1.0;
                }
            }

            if (!selectedExperienceIds.isEmpty()
                    && candidate.getExperienceIds() != null) {

                long matchingExperiences =
                        candidate.getExperienceIds()
                                .stream()
                                .filter(selectedExperienceIds::contains)
                                .count();

                score += matchingExperiences * 3.0;
            }

            double distanceKm = calculateDistanceFromDestination(
                    destinationLatitude,
                    destinationLongitude,
                    candidate
            );

            score += calculateProximityScore(distanceKm);

            scored.add(
                    new ScoredAttraction(
                            candidate,
                            score,
                            distanceKm
                    )
            );
        }

        return scored.stream()
                .sorted(
                        Comparator
                                .comparingDouble(
                                        ScoredAttraction::score
                                )
                                .reversed()
                                .thenComparing(
                                        ScoredAttraction::distanceKm
                                )
                                .thenComparing(
                                        item -> item.candidate().getName(),
                                        Comparator.nullsLast(
                                                String.CASE_INSENSITIVE_ORDER
                                        )
                                )
                )
                .limit(MAX_ATTRACTIONS)
                .map(ScoredAttraction::candidate)
                .toList();
    }

    private double calculateDistanceFromDestination(
            Double destinationLatitude,
            Double destinationLongitude,
            AttractionCandidate candidate) {

        if (destinationLatitude == null
                || destinationLongitude == null
                || candidate.getLatitude() == null
                || candidate.getLongitude() == null) {

            return Double.MAX_VALUE;
        }

        GeoPoint destinationPoint =
                new GeoPoint(
                        destinationLatitude,
                        destinationLongitude
                );

        GeoPoint attractionPoint =
                new GeoPoint(
                        candidate.getLatitude(),
                        candidate.getLongitude()
                );

        return routingService.calculateDistanceKm(
                destinationPoint,
                attractionPoint
        );
    }

    private double calculateProximityScore(
            double distanceKm) {

        if (distanceKm == Double.MAX_VALUE) {
            return 0.0;
        }

        if (distanceKm <= 5.0) {
            return MAX_PROXIMITY_SCORE;
        }

        if (distanceKm <= 15.0) {
            return 2.0;
        }

        if (distanceKm <= 30.0) {
            return 1.0;
        }

        return 0.0;
    }

    private List<AccommodationCandidate> rankAccommodations(
            AiItineraryRequest request,
            List<AccommodationCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .filter(candidate ->
                        candidate.getPricePerNight() != null
                                && candidate.getPricePerNight()
                                .compareTo(request.getBudget()) <= 0
                )
                .sorted(
                        Comparator
                                .comparing(
                                        AccommodationCandidate::getRating,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        AccommodationCandidate::getPricePerNight,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                )
                .limit(MAX_ACCOMMODATIONS)
                .toList();
    }

    private List<RestaurantCandidate> rankRestaurants(
            AiItineraryRequest request,
            List<RestaurantCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        TripPreferences preferences =
                request.getPreferences();

        return candidates.stream()
                .sorted(
                        Comparator
                                .comparingDouble(
                                        (RestaurantCandidate candidate) ->
                                                preferenceMatcher
                                                        .restaurantPreferenceScore(
                                                                candidate,
                                                                preferences
                                                        )
                                )
                                .reversed()
                                .thenComparing(
                                        RestaurantCandidate::getRating,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        RestaurantCandidate::getName,
                                        Comparator.nullsLast(
                                                String.CASE_INSENSITIVE_ORDER
                                        )
                                )
                )
                .limit(MAX_RESTAURANTS)
                .toList();
    }

    private List<GuideCandidate> rankGuides(
            AiItineraryRequest request,
            List<GuideCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        TripPreferences preferences =
                request.getPreferences();

        return candidates.stream()
                .filter(candidate ->
                        Boolean.TRUE.equals(candidate.getAvailable())
                )
                .sorted(
                        Comparator
                                .comparingDouble(
                                        (GuideCandidate candidate) ->
                                                preferenceMatcher
                                                        .guidePreferenceScore(
                                                                candidate,
                                                                preferences
                                                        )
                                )
                                .reversed()
                                .thenComparing(
                                        GuideCandidate::getRating,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        GuideCandidate::getYearsOfExperience,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                )
                .limit(MAX_GUIDES)
                .toList();
    }

    private List<TransportCandidate> rankTransport(
            List<TransportCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .filter(candidate ->
                        Boolean.TRUE.equals(candidate.getAvailable())
                )
                .sorted(
                        Comparator
                                .comparing(
                                        TransportCandidate::getEstimatedFare,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                                .thenComparing(
                                        TransportCandidate::getProviderName,
                                        Comparator.nullsLast(
                                                String.CASE_INSENSITIVE_ORDER
                                        )
                                )
                )
                .limit(MAX_TRANSPORT)
                .toList();
    }

    private List<FestivalCandidate> rankFestivals(
            List<FestivalCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .sorted(
                        Comparator
                                .comparing(
                                        FestivalCandidate::getConfirmed,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        FestivalCandidate::getStartDate,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                )
                .limit(MAX_FESTIVALS)
                .toList();
    }

    private record ScoredAttraction(
            AttractionCandidate candidate,
            double score,
            double distanceKm
    ) {
    }
}