package com.tourism.backend.user.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.user.dto.UpdateUserPreferencesRequest;
import com.tourism.backend.user.dto.UserPreferencesResponse;
import com.tourism.backend.user.service.UserPreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.USER_PREFERENCES)
@RequiredArgsConstructor
@Tag(
        name = "User Preferences",
        description = "Operations for managing user travel preferences and onboarding"
)
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get user preferences",
            description = "Retrieves the current authenticated user's travel preferences and onboarding status."
    )
    @ApiResponse(responseCode = "200", description = "Preferences retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public UserPreferencesResponse getPreferences() {
        return userPreferencesService.getPreferences();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update user preferences",
            description = "Updates the current authenticated user's interest tags and marks onboarding as completed."
    )
    @ApiResponse(responseCode = "200", description = "Preferences updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request or validation error")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "One or more tag IDs not found")
    public UserPreferencesResponse updatePreferences(
            @Valid @RequestBody UpdateUserPreferencesRequest request) {
        return userPreferencesService.updatePreferences(request);
    }

    @PostMapping("/skip")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Skip onboarding preferences",
            description = "Marks onboarding as completed for the current authenticated user without modifying interest tags."
    )
    @ApiResponse(responseCode = "200", description = "Onboarding skipped successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public UserPreferencesResponse skipPreferences() {
        return userPreferencesService.skipPreferences();
    }
}
