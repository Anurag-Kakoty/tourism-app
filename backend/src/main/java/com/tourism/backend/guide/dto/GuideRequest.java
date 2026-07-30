package com.tourism.backend.guide.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuideRequest {

    @NotBlank(message = "Guide name is required.")
    @Size(max = 100)
    private String name;

    @Size(max = 2000)
    private String bio;

    @NotBlank(message = "Phone number is required.")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Languages are required.")
    @Size(max = 300)
    private String languages;

    @NotNull(message = "Years of experience is required.")
    @PositiveOrZero
    private Integer yearsOfExperience;

    @NotNull(message = "Price per day is required.")
    @PositiveOrZero
    private Double pricePerDay;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double rating;

    @NotNull(message = "Availability is required.")
    private Boolean available;

    @NotBlank(message = "License number is required.")
    @Size(max = 50)
    private String licenseNumber;

    @NotNull(message = "Provide transport flag is required.")
    private Boolean providesTransport;

    @Size(max = 500)
    private String imageUrl;

    @NotNull(message = "Destination is required.")
    private Long destinationId;
}