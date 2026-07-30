package com.tourism.backend.guide.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuideResponse {

    private Long id;

    private String name;

    private String bio;

    private String phone;

    private String email;

    private String languages;

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