package com.tourism.backend.recommendation.service;

import com.tourism.backend.recommendation.dto.RecommendedAttractionResponse;

import java.util.List;

public interface AttractionRecommendationService {

    List<RecommendedAttractionResponse> getRecommendations(
            Long stateId,
            Long destinationId,
            Integer limit
    );
}
