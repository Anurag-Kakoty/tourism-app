package com.tourism.backend.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "Response object representing a destination")
public class DestinationResponse {

    private Long id;

    private String name;

    private String description;

    private String district;

    private Double latitude;

    private Double longitude;

    private String bestSeason;

    private BigDecimal entryFee;

    private String thumbnailUrl;

    private Long stateId;

    private String stateName;
}