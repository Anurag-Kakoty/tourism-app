package com.tourism.backend.ai.dto.candidate;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FestivalCandidate {

    private Long occurrenceId;
    private Long festivalId;
    private String festivalName;
    private String description;
    private String category;
    private Long stateId;
    private String stateName;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean confirmed;
    private String notes;
}