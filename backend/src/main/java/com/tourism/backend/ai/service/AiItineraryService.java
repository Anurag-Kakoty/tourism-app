package com.tourism.backend.ai.service;

import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.AiItineraryResponse;

public interface AiItineraryService {

    AiItineraryResponse generateItinerary(
            AiItineraryRequest request
    );
}