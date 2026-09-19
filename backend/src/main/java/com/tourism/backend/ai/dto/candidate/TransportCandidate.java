package com.tourism.backend.ai.dto.candidate;

import com.tourism.backend.transport.entity.TransportType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TransportCandidate {

    private Long id;
    private TransportType type;
    private String providerName;
    private String pickupLocation;
    private String dropLocation;
    private String estimatedDuration;
    private BigDecimal estimatedFare;
    private Boolean available;
}