package com.tourism.backend.ai.dto.candidate;

import com.tourism.backend.guide.entity.Language;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class GuideCandidate {

    private Long id;
    private String name;
    private String bio;
    private Set<Language> languages;
    private Integer yearsOfExperience;
    private Double pricePerDay;
    private Double rating;
    private Boolean available;
    private Boolean providesTransport;
}