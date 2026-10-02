package com.tourism.backend.ai.recommendation;
import org.springframework.stereotype.Component;
import com.tourism.backend.ai.dto.TripPreferences;
import com.tourism.backend.ai.dto.candidate.GuideCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;

@Component
public class PreferenceMatcher {

    private static final double CUISINE_MATCH_SCORE = 5.0;
    private static final double VEGETARIAN_MATCH_SCORE = 3.0;
    private static final double GUIDE_LANGUAGE_MATCH_SCORE = 5.0;

    public double restaurantPreferenceScore(
            RestaurantCandidate candidate,
            TripPreferences preferences) {

        if (candidate == null || preferences == null) {
            return 0.0;
        }

        double score = 0.0;

        if (preferences.getPreferredCuisines() != null
                && !preferences.getPreferredCuisines().isEmpty()
                && preferences.getPreferredCuisines()
                .contains(candidate.getCuisine())) {

            score += CUISINE_MATCH_SCORE;
        }

        if (Boolean.TRUE.equals(preferences.getVegetarian())
                && Boolean.TRUE.equals(candidate.getVegetarian())) {

            score += VEGETARIAN_MATCH_SCORE;
        }

        return score;
    }

    public double guidePreferenceScore(
            GuideCandidate candidate,
            TripPreferences preferences) {

        if (candidate == null || preferences == null) {
            return 0.0;
        }

        if (preferences.getPreferredGuideLanguages() == null
                || preferences.getPreferredGuideLanguages().isEmpty()
                || candidate.getLanguages() == null
                || candidate.getLanguages().isEmpty()) {

            return 0.0;
        }

        boolean languageMatch =
                candidate.getLanguages()
                        .stream()
                        .anyMatch(
                                preferences.getPreferredGuideLanguages()::contains
                        );

        return languageMatch
                ? GUIDE_LANGUAGE_MATCH_SCORE
                : 0.0;
    }
}