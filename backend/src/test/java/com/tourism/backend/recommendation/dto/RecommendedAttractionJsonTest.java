package com.tourism.backend.recommendation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tourism.backend.attraction.dto.AttractionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendedAttractionJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should serialize and deserialize RecommendedAttractionResponse JSON")
    void shouldSerializeAndDeserializeResponse() throws Exception {
        AttractionResponse attraction = new AttractionResponse();
        attraction.setId(50L);
        attraction.setName("Krang Suri Falls");
        attraction.setDestinationName("Jowai");

        RecommendedAttractionResponse response = RecommendedAttractionResponse.builder()
                .attraction(attraction)
                .matchScore(20.98)
                .matchedTags(List.of("Nature", "Waterfalls"))
                .explanation("Matches your interest in Nature, Waterfalls")
                .isFallback(false)
                .build();

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"matchScore\":20.98");
        assertThat(json).contains("\"explanation\":\"Matches your interest in Nature, Waterfalls\"");
        assertThat(json).contains("\"isFallback\":false");
        assertThat(json).contains("\"name\":\"Krang Suri Falls\"");

        RecommendedAttractionResponse deserialized = objectMapper.readValue(json, RecommendedAttractionResponse.class);
        assertThat(deserialized.getMatchScore()).isEqualTo(20.98);
        assertThat(deserialized.getMatchedTags()).containsExactly("Nature", "Waterfalls");
        assertThat(deserialized.getIsFallback()).isFalse();
        assertThat(deserialized.getAttraction().getName()).isEqualTo("Krang Suri Falls");
    }
}
