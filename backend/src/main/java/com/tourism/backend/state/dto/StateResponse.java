package com.tourism.backend.state.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Response object representing an Indian state")
public class StateResponse {

    @Schema(
            description = "Unique identifier of the state",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the Indian state",
            example = "Assam"
    )
    private String name;

    @Schema(
            description = "Capital city of the state",
            example = "Dispur"
    )
    private String capital;

    @Schema(
            description = "Brief description of the state",
            example = "Known for its tea gardens, Kaziranga National Park, and the Brahmaputra River."
    )
    private String description;

    @Schema(
            description = "URL of the state's thumbnail image",
            example = "https://example.com/images/assam.jpg"
    )
    private String thumbnailUrl;
}