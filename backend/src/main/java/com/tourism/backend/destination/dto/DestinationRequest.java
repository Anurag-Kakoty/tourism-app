package com.tourism.backend.destination.dto;

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
@Schema(description = "Request object for creating or updating a destination")
public class DestinationRequest {

    @NotBlank(message = "Destination name is required.")
    @Size(max = 150, message = "Destination name cannot exceed 150 characters.")
    @Schema(example = "Kaziranga National Park")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters.")
    @Schema(example = "A UNESCO World Heritage Site famous for the one-horned rhinoceros.")
    private String description;

    @NotBlank(message = "District is required.")
    @Size(max = 100, message = "District cannot exceed 100 characters.")
    @Schema(example = "Golaghat")
    private String district;

    @NotNull(message = "Latitude is required.")
    @Schema(example = "26.5775")
    private Double latitude;

    @NotNull(message = "Longitude is required.")
    @Schema(example = "93.1711")
    private Double longitude;

    @NotBlank(message = "Best season is required.")
    @Size(max = 150, message = "Best season cannot exceed 150 characters.")
    @Schema(example = "November to April")
    private String bestSeason;

    @DecimalMin(value = "0.0", inclusive = true, message = "Entry fee cannot be negative.")
    @Schema(example = "200.00")
    private BigDecimal entryFee;

    @Size(max = 500, message = "Thumbnail URL cannot exceed 500 characters.")
    @Schema(example = "https://example.com/kaziranga.jpg")
    private String thumbnailUrl;

    @NotNull(message = "State ID is required.")
    @Schema(example = "1")
    private Long stateId;

    @Schema(
            description = "IDs of tags associated with the destination",
            example = "[1,2,3]"
    )
    private Set<Long> tagIds = new HashSet<>();

    @Schema(
            description = "IDs of experiences associated with the destination",
            example = "[1,2]"
    )
    private Set<Long> experienceIds = new HashSet<>();

}