package com.tourism.backend.ai.client;

import com.tourism.backend.itinerary.entity.ActivityType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class AiGeneratedItinerary {

    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer numberOfTravelers;

    private BigDecimal estimatedBudget;

    private Long destinationId;

    private List<GeneratedItem> items;

    @Getter
    @Builder
    public static class GeneratedItem {

        private Integer dayNumber;

        private Integer activityOrder;

        private LocalTime time;

        private ActivityType activityType;

        private Long referenceId;

        private String notes;
    }
}