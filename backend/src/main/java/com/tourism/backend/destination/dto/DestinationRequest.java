package com.tourism.backend.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request object for creating or updating a destination")
public class DestinationRequest {

    @Schema(example = "Living Root Bridge")
    @NotBlank(message = "Destination name is required")
    @Size(max = 150)
    private String name;

    @Schema(example = "A natural bridge formed by living tree roots.")
    @Size(max = 2000)
    private String description;

    @Schema(example = "East Khasi Hills")
    @NotBlank(message = "District is required")
    @Size(max = 100)
    private String district;

    @Schema(example = "25.286")
    @NotNull(message = "Latitude is required")
    private Double latitude;

    @Schema(example = "91.726")
    @NotNull(message = "Longitude is required")
    private Double longitude;

    @Schema(example = "October-April")
    @NotBlank(message = "Best season is required")
    @Size(max = 150)
    private String bestSeason;

    @Schema(example = "50.00")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal entryFee;

    @Schema(example = "https://example.com/rootbridge.jpg")
    @Size(max = 500)
    private String thumbnailUrl;

    @Schema(example = "2")
    @NotNull(message = "State ID is required")
    private Long stateId;
}