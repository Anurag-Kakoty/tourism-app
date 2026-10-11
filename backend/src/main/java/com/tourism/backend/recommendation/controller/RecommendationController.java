package com.tourism.backend.recommendation.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.recommendation.dto.RecommendedAttractionResponse;
import com.tourism.backend.recommendation.service.AttractionRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.RECOMMENDATIONS)
@RequiredArgsConstructor
@Tag(
        name = "Recommendations",
        description = "Operations for personalized tourist attraction recommendations"
)
public class RecommendationController {

    private final AttractionRecommendationService attractionRecommendationService;

    @GetMapping("/attractions")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get personalized attraction recommendations",
            description = """
                    Retrieves attraction recommendations ranked deterministically based on the user's saved interest tags.
                    For guests or users with no preferences, returns attractions in default discovery order with fallback metadata.

                    Optional filters:
                    /api/recommendations/attractions?stateId=1
                    /api/recommendations/attractions?destinationId=1
                    /api/recommendations/attractions?destinationId=1&limit=5
                    """
    )
    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully")
    public List<RecommendedAttractionResponse> getAttractionRecommendations(
            @Parameter(description = "Filter recommendations by state ID", example = "1")
            @RequestParam(required = false) Long stateId,

            @Parameter(description = "Filter recommendations by destination ID", example = "1")
            @RequestParam(required = false) Long destinationId,

            @Parameter(description = "Maximum number of recommendations to return", example = "5")
            @RequestParam(required = false) Integer limit) {

        return attractionRecommendationService.getRecommendations(stateId, destinationId, limit);
    }
}
