package com.tourism.backend.recommendation.service;

import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.attraction.mapper.AttractionMapper;
import com.tourism.backend.attraction.repository.AttractionRepository;
import com.tourism.backend.attraction.specification.AttractionSpecification;
import com.tourism.backend.recommendation.dto.RecommendedAttractionResponse;
import com.tourism.backend.tag.entity.Tag;
import com.tourism.backend.user.entity.User;
import com.tourism.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttractionRecommendationServiceImpl implements AttractionRecommendationService {

    private final AttractionRepository attractionRepository;
    private final UserRepository userRepository;
    private final AttractionMapper attractionMapper;

    @Override
    public List<RecommendedAttractionResponse> getRecommendations(
            Long stateId,
            Long destinationId,
            Integer limit) {

        log.info("Computing attraction recommendations for stateId={}, destinationId={}, limit={}",
                stateId, destinationId, limit);

        Set<Tag> preferredTags = resolveUserPreferredTags();
        Set<Long> preferredTagIds = preferredTags.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());

        Specification<Attraction> specification = Specification
                .where(AttractionSpecification.hasStateId(stateId))
                .and(AttractionSpecification.hasDestinationId(destinationId));

        List<Attraction> attractions = attractionRepository.findAll(specification);

        if (attractions == null || attractions.isEmpty()) {
            return Collections.emptyList();
        }

        List<ScoredAttraction> scoredList = new ArrayList<>();

        for (Attraction attraction : attractions) {
            Set<Tag> attractionTags = attraction.getTags() != null
                    ? attraction.getTags()
                    : Collections.emptySet();

            List<String> matchedTagNames = attractionTags.stream()
                    .filter(tag -> preferredTagIds.contains(tag.getId()))
                    .map(Tag::getName)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();

            int matchCount = matchedTagNames.size();
            boolean hasMatch = matchCount > 0;
            boolean featured = Boolean.TRUE.equals(attraction.getFeatured());
            int displayOrder = attraction.getDisplayOrder() != null ? attraction.getDisplayOrder() : 0;

            // Deterministic, explainable scoring formula:
            // - Each matched interest tag: 10.0 points
            // - Featured popularity tie-breaker: 1.0 point
            // - Display order secondary tie-breaker: -0.01 * displayOrder
            double rawScore = (matchCount * 10.0) + (featured ? 1.0 : 0.0) - (displayOrder * 0.01);
            double roundedScore = Math.round(rawScore * 100.0) / 100.0;

            boolean isFallback = !hasMatch;

            String locationName = "";
            if (attraction.getDestination() != null && attraction.getDestination().getName() != null) {
                locationName = attraction.getDestination().getName();
            } else if (attraction.getDestination() != null
                    && attraction.getDestination().getState() != null
                    && attraction.getDestination().getState().getName() != null) {
                locationName = attraction.getDestination().getState().getName();
            }

            String explanation;
            if (hasMatch) {
                explanation = "Matches your interest in " + String.join(", ", matchedTagNames);
            } else if (featured) {
                explanation = locationName.isBlank()
                        ? "Popular featured attraction"
                        : "Popular featured attraction in " + locationName;
            } else {
                explanation = locationName.isBlank()
                        ? "Popular attraction"
                        : "Popular attraction in " + locationName;
            }

            scoredList.add(new ScoredAttraction(
                    attraction,
                    roundedScore,
                    matchedTagNames,
                    explanation,
                    isFallback
            ));
        }

        Comparator<ScoredAttraction> comparator = Comparator
                .comparingDouble(ScoredAttraction::score).reversed()
                .thenComparing(s -> Boolean.TRUE.equals(s.attraction().getFeatured()), Comparator.reverseOrder())
                .thenComparing(
                        s -> s.attraction().getDisplayOrder(),
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
                .thenComparing(
                        s -> s.attraction().getName(),
                        String.CASE_INSENSITIVE_ORDER
                )
                .thenComparing(s -> s.attraction().getId());

        Stream<RecommendedAttractionResponse> responseStream = scoredList.stream()
                .sorted(comparator)
                .map(s -> RecommendedAttractionResponse.builder()
                        .attraction(attractionMapper.toResponse(s.attraction()))
                        .matchScore(s.score())
                        .matchedTags(s.matchedTags())
                        .explanation(s.explanation())
                        .isFallback(s.isFallback())
                        .build()
                );

        if (limit != null && limit > 0) {
            responseStream = responseStream.limit(limit);
        }

        return responseStream.toList();
    }

    private Set<Tag> resolveUserPreferredTags() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())
                || "anonymousUser".equalsIgnoreCase(authentication.getName())) {
            return Collections.emptySet();
        }

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            return Collections.emptySet();
        }

        Optional<User> userOptional = userRepository.findWithInterestsByEmailIgnoreCase(email);
        if (userOptional.isEmpty() || userOptional.get().getInterests() == null) {
            return Collections.emptySet();
        }

        return userOptional.get().getInterests();
    }

    private record ScoredAttraction(
            Attraction attraction,
            double score,
            List<String> matchedTags,
            String explanation,
            boolean isFallback
    ) {}
}
