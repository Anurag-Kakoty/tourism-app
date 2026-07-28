package com.tourism.backend.experience.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Experience response")
public class ExperienceResponse {

    @Schema(
            description = "Experience ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the experience",
            example = "Trekking"
    )
    private String name;

    @Schema(
            description = "Description of the experience",
            example = "Explore scenic trails through forests and mountains."
    )
    private String description;

    @Schema(
            description = "Frontend icon identifier",
            example = "mountain"
    )
    private String icon;
}