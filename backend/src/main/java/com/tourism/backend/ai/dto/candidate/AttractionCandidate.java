package com.tourism.backend.ai.dto.candidate;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class AttractionCandidate {

    private Long id;
    private String name;
    private String description;
    private String bestSeason;
    private BigDecimal entryFee;
    private Boolean featured;
    private List<String> experiences;
    private List<String> tags;
}