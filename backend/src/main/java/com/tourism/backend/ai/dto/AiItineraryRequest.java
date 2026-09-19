package com.tourism.backend.ai.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AiItineraryRequest {

    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    @NotNull(message = "Number of travelers is required.")
    @Min(value = 1, message = "At least one traveler is required.")
    private Integer numberOfTravelers;

    @NotNull(message = "Budget is required.")
    @Min(value = 0, message = "Budget cannot be negative.")
    private BigDecimal budget;

    @NotNull(message = "Destination is required.")
    private Long destinationId;

    private List<Long> experienceIds;

    private Long festivalId;
}