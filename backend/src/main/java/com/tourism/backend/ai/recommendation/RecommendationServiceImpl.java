package com.tourism.backend.ai.recommendation;

import com.tourism.backend.ai.dto.AiCandidateContext;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.dto.candidate.AccommodationCandidate;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import com.tourism.backend.ai.dto.candidate.FestivalCandidate;
import com.tourism.backend.ai.dto.candidate.GuideCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;
import com.tourism.backend.ai.dto.candidate.TransportCandidate;
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

    @Override
    public RecommendedCandidateContext recommend(
            AiItineraryRequest request,
            AiCandidateContext candidates) {

        return RecommendedCandidateContext.builder()
                .attractions(
                        rankAttractions(
                                request,
                                candidates.getAttractions()
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
                                candidates.getRestaurants()
                        )
                )
                .guides(
                        rankGuides(
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
            List<AttractionCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        Set<Long> selectedExperienceIds =
                request.getExperienceIds() == null
                        ? Set.of()
                        : new HashSet<>(
                        request.getExperienceIds()
                );

        /*
         * The candidate DTO contains experience names rather than
         * experience IDs. Therefore, at this stage we can only use
         * experience matching when the request is extended with
         * experience names or when we introduce an experience
         * candidate lookup.
         *
         * For now, ranking uses fields that are directly available
         * in the candidate.
         */

        List<ScoredAttraction> scored =
                new ArrayList<>();

        for (AttractionCandidate candidate : candidates) {

            double score = 0.0;

            if (Boolean.TRUE.equals(candidate.getFeatured())) {
                score += 2.0;
            }

            if (candidate.getExperiences() != null
                    && !candidate.getExperiences().isEmpty()) {
                score += 1.0;
            }

            if (candidate.getTags() != null
                    && !candidate.getTags().isEmpty()) {
                score += 1.0;
            }

            BigDecimal entryFee =
                    candidate.getEntryFee();

            if (entryFee != null) {

                if (entryFee.compareTo(
                        request.getBudget()
                ) <= 0) {

                    score += 1.0;
                }

                if (entryFee.compareTo(
                        BigDecimal.ZERO
                ) == 0) {

                    score += 1.0;
                }
            }

            scored.add(
                    new ScoredAttraction(
                            candidate,
                            score
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
                                        item ->
                                                item.candidate()
                                                        .getName(),
                                        Comparator.nullsLast(
                                                String.CASE_INSENSITIVE_ORDER
                                        )
                                )
                )
                .limit(MAX_ATTRACTIONS)
                .map(ScoredAttraction::candidate)
                .toList();
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
                                && candidate
                                .getPricePerNight()
                                .compareTo(request.getBudget())
                                <= 0
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
                                        AccommodationCandidate
                                                ::getPricePerNight,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                )
                .limit(MAX_ACCOMMODATIONS)
                .toList();
    }

    private List<RestaurantCandidate> rankRestaurants(
            List<RestaurantCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .sorted(
                        Comparator
                                .comparing(
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
            List<GuideCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .filter(candidate ->
                        Boolean.TRUE.equals(
                                candidate.getAvailable()
                        )
                )
                .sorted(
                        Comparator
                                .comparing(
                                        GuideCandidate::getRating,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        GuideCandidate
                                                ::getYearsOfExperience,
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
                        Boolean.TRUE.equals(
                                candidate.getAvailable()
                        )
                )
                .sorted(
                        Comparator
                                .comparing(
                                        TransportCandidate
                                                ::getEstimatedFare,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                                .thenComparing(
                                        TransportCandidate
                                                ::getProviderName,
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
                                        FestivalCandidate
                                                ::getStartDate,
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
            double score
    ) {
    }
}