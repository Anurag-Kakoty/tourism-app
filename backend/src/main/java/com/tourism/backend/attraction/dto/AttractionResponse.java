package com.tourism.backend.attraction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Response object representing an attraction")
public class AttractionResponse {

    @Schema(
            description = "Unique identifier of the attraction",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the attraction",
            example = "Living Root Bridge"
    )
    private String name;

    @Schema(
            description = "Description of the attraction",
            example = "A naturally formed bridge created from the roots of rubber trees."
    )
    private String description;

    @Schema(
            description = "Latitude of the attraction",
            example = "25.2866"
    )
    private Double latitude;

    @Schema(
            description = "Longitude of the attraction",
            example = "91.7326"
    )
    private Double longitude;

    @Schema(
            description = "Best season to visit",
            example = "October to April"
    )
    private String bestSeason;

    @Schema(
            description = "Entry fee",
            example = "50.00"
    )
    private BigDecimal entryFee;

    @Schema(
            description = "Thumbnail image URL",
            example = "https://example.com/living-root-bridge.jpg"
    )
    private String thumbnailUrl;

    @Schema(
            description = "Whether the attraction is featured",
            example = "true"
    )
    private Boolean featured;

    @Schema(
            description = "Display order within the destination",
            example = "1"
    )
    private Integer displayOrder;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Opening time in 24-hour format",
            example = "09:00:00"
    )
    private LocalTime openingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Closing time in 24-hour format",
            example = "17:30:00"
    )
    private LocalTime closingTime;

    @Schema(
            description = "Days of the week when the attraction is closed",
            example = "[\"MONDAY\"]"
    )
    private Set<DayOfWeek> closedDays = new HashSet<>();

    @Schema(
            description = "Destination ID",
            example = "1"
    )
    private Long destinationId;

    @Schema(
            description = "Destination name",
            example = "Cherrapunji"
    )
    private String destinationName;

    @Schema(
            description = "State ID",
            example = "1"
    )
    private Long stateId;

    @Schema(
            description = "State name",
            example = "Meghalaya"
    )
    private String stateName;

    @Schema(
            description = "IDs of associated tags",
            example = "[1,2]"
    )
    private Set<Long> tagIds = new HashSet<>();

    @Schema(
            description = "Names of associated tags",
            example = "[\"Nature\",\"Photography\"]"
    )
    private Set<String> tagNames = new HashSet<>();

    @Schema(
            description = "IDs of associated experiences",
            example = "[1,2]"
    )
    private Set<Long> experienceIds = new HashSet<>();

    @Schema(
            description = "Names of associated experiences",
            example = "[\"Trekking\",\"Camping\"]"
    )
    private Set<String> experienceNames = new HashSet<>();
}