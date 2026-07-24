package com.tourism.backend.dto.state;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateRequest {

    @Schema(
            description = "Name of the Indian state",
            example = "Assam"
    )
    @NotBlank(message = "State name is required")
    @Size(max = 100,
            message = "State name cannot exceed 100 characters")
    private String name;

    @Schema(
            description = "Capital city of the state",
            example = "Dispur"
    )
    @NotBlank(message = "Capital is required")
    @Size(max = 100,
            message = "State capital cannot exceed 100 characters")
    private String capital;

    @Schema(
            description = "Brief description of the state",
            example = "Known for its tea gardens, Kaziranga National Park, and the Brahmaputra River."
    )
    @Size(max = 2000,
            message = "Description cannot exceed 2000 characters")
    private String description;

    @Schema(
            description = "URL of the state's thumbnail image",
            example = "https://example.com/images/assam.jpg"
    )
    private String thumbnailUrl;
}