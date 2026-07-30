package com.tourism.backend.accommodation.dto;

import com.tourism.backend.accommodation.entity.AccommodationType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private Boolean available;

    private Long destinationId;

    private String destinationName;
}