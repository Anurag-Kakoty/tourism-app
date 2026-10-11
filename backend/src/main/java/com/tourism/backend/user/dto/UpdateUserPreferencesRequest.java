package com.tourism.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for updating user travel interest tags")
public class UpdateUserPreferencesRequest {

    @NotNull(message = "Tag IDs set cannot be null.")
    @Schema(description = "Set of tag IDs representing user's interests", example = "[1, 2, 4]")
    @Builder.Default
    private Set<Long> tagIds = new HashSet<>();
}
