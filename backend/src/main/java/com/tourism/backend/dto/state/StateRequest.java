package com.tourism.backend.dto.state;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateRequest {

    @NotBlank(message = "State name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String capital;

    @Size(max = 2000)
    private String description;

    private String thumbnailUrl;
}