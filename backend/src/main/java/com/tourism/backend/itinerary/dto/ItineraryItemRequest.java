package com.tourism.backend.itinerary.dto;

import com.tourism.backend.itinerary.entity.ActivityType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class ItineraryItemRequest {

    @NotNull(message = "Day number is required.")
    private Integer dayNumber;

    @NotNull(message = "Activity order is required.")
    private Integer activityOrder;

    @NotNull(message = "Time is required.")
    private LocalTime time;

    @NotNull(message = "Activity type is required.")
    private ActivityType activityType;

    @NotNull(message = "Reference ID is required.")
    private Long referenceId;

    private String notes;
}