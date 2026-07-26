package com.tourism.backend.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Response object representing a tag")
public class TagResponse {

    @Schema(
            description = "Unique identifier of the tag",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the tag",
            example = "Nature"
    )
    private String name;

    @Schema(
            description = "Description of the tag",
            example = "Natural attractions such as forests, waterfalls and mountains."
    )
    private String description;
}