package com.tourism.backend.experience.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for creating or updating an experience")
public class ExperienceRequest {

    @NotBlank(message = "Experience name is required.")
    @Size(max = 100, message = "Experience name must not exceed 100 characters.")
    @Schema(
            description = "Name of the experience",
            example = "Trekking"
    )
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    @Schema(
            description = "Brief description of the experience",
            example = "Explore scenic trails through forests and mountains."
    )
    private String description;

    @Size(max = 100, message = "Icon must not exceed 100 characters.")
    @Schema(
            description = "Frontend icon identifier",
            example = "mountain"
    )
    private String icon;
}