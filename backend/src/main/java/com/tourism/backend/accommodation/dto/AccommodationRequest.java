package com.tourism.backend.accommodation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tourism.backend.accommodation.entity.AccommodationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class AccommodationRequest {

    @NotBlank(message = "Accommodation name is required")
    private String name;

    private String description;

    @NotNull(message = "Accommodation type is required")
    private AccommodationType type;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal pricePerNight;

    @DecimalMin(value = "0.0", message = "Rating cannot be less than 0")
    @DecimalMax(value = "5.0", message = "Rating cannot be greater than 5")
    private Double rating;

    private String contactNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String website;

    @NotBlank(message = "Address is required")
    private String address;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm[:ss]")
    @Schema(
            description = "Standard check-in time in 24-hour format (e.g. 14:00:00). Optional.",
            example = "14:00:00"
    )
    private LocalTime checkInTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm[:ss]")
    @Schema(
            description = "Standard check-out time in 24-hour format (e.g. 11:00:00). Optional.",
            example = "11:00:00"
    )
    private LocalTime checkOutTime;

    private Boolean available;

    @NotNull(message = "Destination is required")
    private Long destinationId;
}