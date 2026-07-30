package com.tourism.backend.attraction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request object for creating or updating an attraction")
public class AttractionRequest {

    @NotBlank(message = "Attraction name is required.")
    @Size(max = 150, message = "Attraction name cannot exceed 150 characters.")
    @Schema(example = "Living Root Bridge")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters.")
    @Schema(example = "A naturally formed bridge created from the roots of rubber trees.")
    private String description;

    @NotNull(message = "Latitude is required.")
    @Schema(example = "25.2866")
    private Double latitude;

    @NotNull(message = "Longitude is required.")
    @Schema(example = "91.7326")
    private Double longitude;

    @NotBlank(message = "Best season is required.")
    @Size(max = 150, message = "Best season cannot exceed 150 characters.")
    @Schema(example = "October to April")
    private String bestSeason;

    @DecimalMin(value = "0.0", inclusive = true, message = "Entry fee cannot be negative.")
    @Schema(example = "50.00")
    private BigDecimal entryFee;

    @Size(max = 500, message = "Thumbnail URL cannot exceed 500 characters.")
    @Schema(example = "https://example.com/living-root-bridge.jpg")
    private String thumbnailUrl;

    @NotNull(message = "Destination ID is required.")
    @Schema(example = "1")
    private Long destinationId;

    @NotNull(message = "Featured is required.")
    @Schema(example = "true")
    private Boolean featured;

    @NotNull(message = "Display order is required.")
    @Min(value = 0, message = "Display order cannot be negative.")
    @Schema(example = "1")
    private Integer displayOrder;

    @Schema(
            description = "IDs of tags associated with the attraction",
            example = "[1,2]"
    )
    private Set<Long> tagIds = new HashSet<>();

    @Schema(
            description = "IDs of experiences associated with the attraction",
            example = "[1,2]"
    )
    private Set<Long> experienceIds = new HashSet<>();
}