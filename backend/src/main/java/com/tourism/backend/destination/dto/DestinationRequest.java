package com.tourism.backend.destination.dto;

import com.tourism.backend.destination.entity.DestinationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String tagline;

    @Size(max = 1000)
    private String description;

    @Size(max = 100)
    private String district;

    @NotNull
    private Long stateId;

    @NotNull
    private DestinationType type;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @Size(max = 500)
    private String thumbnailUrl;

    @NotNull
    private Boolean featured;

    @NotNull
    private Boolean popular;

    @NotNull
    private Integer displayOrder;

    @Size(max = 500)
    private String coverImageUrl;

    @Size(max = 100)
    private String timezone;

    @Size(max = 100)
    private String nearestAirport;

    @Size(max = 100)
    private String nearestRailwayStation;
}