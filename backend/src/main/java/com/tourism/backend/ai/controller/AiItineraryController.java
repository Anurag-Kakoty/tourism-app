package com.tourism.backend.ai.controller;

import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.AiItineraryResponse;
import com.tourism.backend.ai.service.AiItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/itineraries")
@RequiredArgsConstructor
public class AiItineraryController {

    private final AiItineraryService aiItineraryService;

    @PostMapping("/generate")
    public AiItineraryResponse generateItinerary(
            @Valid @RequestBody AiItineraryRequest request) {

        return aiItineraryService.generateItinerary(request);
    }
}