package com.tourism.backend.ai.dto;

import com.tourism.backend.itinerary.entity.ActivityType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class AiItineraryItemResponse {

    private Integer dayNumber;

    private Integer activityOrder;

    private LocalTime time;

    private ActivityType activityType;

    private Long referenceId;

    private String referenceName;

    private String notes;
}