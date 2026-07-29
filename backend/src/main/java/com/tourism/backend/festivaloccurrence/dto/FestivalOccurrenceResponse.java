package com.tourism.backend.festivaloccurrence.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalOccurrenceResponse {

    private Long id;

    private Long festivalId;
    private String festivalName;

    private Long stateId;
    private String stateName;

    private Integer year;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean confirmed;

    private String notes;
}