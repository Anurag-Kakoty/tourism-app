package com.tourism.backend.festival.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalResponse {

    private Long id;

    private String name;

    private String description;

    private String category;

    private String imageUrl;

    private String officialWebsite;
}