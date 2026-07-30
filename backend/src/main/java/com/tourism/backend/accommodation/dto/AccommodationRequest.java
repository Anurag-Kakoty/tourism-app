package com.tourism.backend.accommodation.dto;

import com.tourism.backend.accommodation.entity.AccommodationType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    private Boolean available;

    @NotNull(message = "Destination is required")
    private Long destinationId;
}