package com.tourism.backend.festival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalRequest {

    @NotBlank(message = "Festival name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    @Size(max = 100)
    private String category;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 500)
    private String officialWebsite;
}