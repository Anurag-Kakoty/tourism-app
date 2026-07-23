package com.tourism.backend.dto.state;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StateResponse {

    private Long id;

    private String name;

    private String capital;

    private String description;

    private String thumbnailUrl;
}