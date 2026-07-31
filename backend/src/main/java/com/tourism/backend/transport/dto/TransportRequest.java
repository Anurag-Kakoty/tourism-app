package com.tourism.backend.transport.dto;

import com.tourism.backend.transport.entity.TransportType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransportRequest {

    @NotNull(message = "Transport type is required.")
    private TransportType type;

    @NotBlank(message = "Provider name is required.")
    @Size(max = 150)
    private String providerName;

    @NotBlank(message = "Pickup location is required.")
    @Size(max = 200)
    private String pickupLocation;

    @NotBlank(message = "Drop location is required.")
    @Size(max = 200)
    private String dropLocation;

    @NotBlank(message = "Estimated duration is required.")
    @Size(max = 100)
    private String estimatedDuration;

    @NotNull(message = "Estimated fare is required.")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Estimated fare cannot be negative.")
    private BigDecimal estimatedFare;

    @Size(max = 20)
    private String contactNumber;

    @Size(max = 300)
    private String website;

    @Size(max = 500)
    private String bookingUrl;

    @NotNull(message = "Availability is required.")
    private Boolean available;

    @NotNull(message = "Destination is required.")
    private Long destinationId;
}