package com.tourism.backend.accommodation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tourism.backend.accommodation.entity.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class AccommodationResponse {

    private Long id;

    private String name;

    private String description;

    private AccommodationType type;

    private BigDecimal pricePerNight;

    private Double rating;

    private String contactNumber;

    private String email;

    private String website;

    private String address;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Standard check-in time in 24-hour format (e.g. 14:00:00). Optional.",
            example = "14:00:00"
    )
    private LocalTime checkInTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Standard check-out time in 24-hour format (e.g. 11:00:00). Optional.",
            example = "11:00:00"
    )
    private LocalTime checkOutTime;

    private Boolean available;

    private Long destinationId;

    private String destinationName;

    private Long stateId;

    private String stateName;
}