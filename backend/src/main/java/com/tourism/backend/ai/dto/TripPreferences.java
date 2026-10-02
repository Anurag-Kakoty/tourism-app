package com.tourism.backend.ai.dto;

import com.tourism.backend.guide.entity.Language;
import com.tourism.backend.restaurant.entity.Cuisine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class TripPreferences {

    private List<Cuisine> preferredCuisines;

    private Boolean vegetarian;

    private Set<Language> preferredGuideLanguages;

    private List<Long> preferredExperienceIds;

    private List<String> preferredAccommodationTypes;

    private TravelPace travelPace;
}