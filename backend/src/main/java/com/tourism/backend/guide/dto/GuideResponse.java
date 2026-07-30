package com.tourism.backend.guide.dto;

import com.tourism.backend.guide.entity.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class GuideResponse {

    private Long id;

    private String name;

    private String bio;

    private String phone;

    private String email;

    private Set<Language> languages;

    private Integer yearsOfExperience;

    private Double pricePerDay;

    private Double rating;

    private Boolean available;

    private String licenseNumber;

    private Boolean providesTransport;

    private String imageUrl;

    private Long destinationId;

    private String destinationName;

    private Long stateId;

    private String stateName;
}