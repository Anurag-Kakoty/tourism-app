package com.tourism.backend.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Response object representing a destination")
public class DestinationResponse {

    @Schema(
            description = "Unique identifier of the destination",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the destination",
            example = "Kaziranga National Park"
    )
    private String name;

    @Schema(
            description = "Description of the destination",
            example = "A UNESCO World Heritage Site famous for the one-horned rhinoceros."
    )
    private String description;

    @Schema(
            description = "District where the destination is located",
            example = "Golaghat"
    )
    private String district;

    @Schema(
            description = "Latitude of the destination",
            example = "26.5775"
    )
    private Double latitude;

    @Schema(
            description = "Longitude of the destination",
            example = "93.1711"
    )
    private Double longitude;

    @Schema(
            description = "Best season to visit",
            example = "November to April"
    )
    private String bestSeason;

    @Schema(
            description = "Entry fee",
            example = "200.00"
    )
    private BigDecimal entryFee;

    @Schema(
            description = "Thumbnail image URL",
            example = "https://example.com/kaziranga.jpg"
    )
    private String thumbnailUrl;

    @Schema(
            description = "State ID",
            example = "1"
    )
    private Long stateId;

    @Schema(
            description = "State name",
            example = "Assam"
    )
    private String stateName;

    @Schema(
            description = "IDs of associated tags",
            example = "[1,2,3]"
    )
    private Set<Long> tagIds = new HashSet<>();

    @Schema(
            description = "Names of associated tags",
            example = "[\"Nature\",\"Wildlife\",\"Photography\"]"
    )
    private Set<String> tagNames = new HashSet<>();

}