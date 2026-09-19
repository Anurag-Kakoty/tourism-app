package com.tourism.backend.ai.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AiItineraryResponse {

    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer numberOfTravelers;

    private BigDecimal estimatedBudget;

    private Long destinationId;

    private List<AiItineraryItemResponse> items;
}