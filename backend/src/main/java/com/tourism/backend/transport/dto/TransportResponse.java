package com.tourism.backend.transport.dto;

import com.tourism.backend.transport.entity.TransportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransportResponse {

    private Long id;

    private TransportType type;

    private String providerName;

    private String pickupLocation;

    private String dropLocation;

    private String estimatedDuration;

    private BigDecimal estimatedFare;

    private String contactNumber;

    private String website;

    private String bookingUrl;

    private Boolean available;

    private Long destinationId;

    private String destinationName;

    private Long stateId;

    private String stateName;
}