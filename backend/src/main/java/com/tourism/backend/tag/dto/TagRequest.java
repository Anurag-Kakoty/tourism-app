package com.tourism.backend.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request object for creating or updating a tag")
public class TagRequest {

    @NotBlank(message = "Tag name is required.")
    @Size(max = 100, message = "Tag name cannot exceed 100 characters.")
    @Schema(
            description = "Name of the tag",
            example = "Nature",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    @Schema(
            description = "Description of the tag",
            example = "Natural attractions such as forests, waterfalls and mountains."
    )
    private String description;
}