package com.tourism.backend.recommendation.dto;

import com.tourism.backend.attraction.dto.AttractionResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Recommendation response containing attraction details and matching metadata")
public class RecommendedAttractionResponse {

    @Schema(description = "Tourist attraction details")
    private AttractionResponse attraction;

    @Schema(description = "Calculated recommendation score based on interest tag matches and popularity", example = "20.99")
    private Double matchScore;

    @Schema(description = "Names of the user interest tags that matched this attraction", example = "[\"Nature\", \"Waterfalls\"]")
    @Builder.Default
    private List<String> matchedTags = new ArrayList<>();

    @Schema(description = "Human-readable explanation of why this attraction is recommended", example = "Matches your interest in Nature, Waterfalls")
    private String explanation;

    @Schema(description = "Whether this result is a fallback default recommendation due to absence of tag matches", example = "false")
    private Boolean isFallback;
}
