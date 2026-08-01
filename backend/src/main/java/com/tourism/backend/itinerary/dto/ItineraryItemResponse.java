package com.tourism.backend.itinerary.dto;

import com.tourism.backend.itinerary.entity.ActivityType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class ItineraryItemResponse {

    private Long id;

    private Integer dayNumber;

    private Integer activityOrder;

    private LocalTime time;

    private ActivityType activityType;

    private Long referenceId;

    /**
     * Human-readable name of the referenced entity.
     * Example:
     *  - Double Decker Living Root Bridge
     *  - Highland Grill
     *  - Meghalaya Transport Corporation
     */
    private String referenceName;

    private String notes;
}