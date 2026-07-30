package com.tourism.backend.destination.dto;

import com.tourism.backend.destination.entity.DestinationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationResponse {

    private Long id;

    private String name;

    private String tagline;

    private String description;

    private String district;

    private Long stateId;

    private String stateName;

    private DestinationType type;

    private Double latitude;

    private Double longitude;

    private String thumbnailUrl;

    private Boolean featured;

    private Boolean popular;

    private Integer displayOrder;

    private String coverImageUrl;

    private String timezone;

    private String nearestAirport;

    private String nearestRailwayStation;
}