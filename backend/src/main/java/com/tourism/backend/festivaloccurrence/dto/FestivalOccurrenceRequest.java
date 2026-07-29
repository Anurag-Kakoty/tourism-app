package com.tourism.backend.festivaloccurrence.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalOccurrenceRequest {

    @NotNull(message = "Festival is required")
    private Long festivalId;

    @NotNull(message = "State is required")
    private Long stateId;

    @NotNull(message = "Year is required")
    @Min(1900)
    @Max(3000)
    private Integer year;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Boolean confirmed;

    private String notes;
}