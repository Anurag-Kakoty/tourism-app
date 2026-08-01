package com.tourism.backend.itinerary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItineraryResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer numberOfTravelers;

    private BigDecimal estimatedBudget;

    private Long destinationId;

    private String destinationName;

    private Long stateId;

    private String stateName;

    private List<ItineraryItemResponse> items = new ArrayList<>();
}