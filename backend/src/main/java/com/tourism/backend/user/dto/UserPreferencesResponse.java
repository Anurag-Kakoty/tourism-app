package com.tourism.backend.user.dto;

import com.tourism.backend.tag.dto.TagResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response object representing the user's travel preferences and onboarding status")
public class UserPreferencesResponse {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long userId;

    @Schema(description = "User's email", example = "user@example.com")
    private String email;

    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @Schema(description = "Whether the user has completed or skipped onboarding", example = "true")
    private Boolean onboardingCompleted;

    @Schema(description = "Set of tags representing user's travel interests")
    @Builder.Default
    private Set<TagResponse> interests = new HashSet<>();
}
