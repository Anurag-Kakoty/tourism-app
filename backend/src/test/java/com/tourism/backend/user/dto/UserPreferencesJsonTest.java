package com.tourism.backend.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.backend.tag.dto.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserPreferencesJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should deserialize UpdateUserPreferencesRequest from JSON payload")
    void shouldDeserializeUpdateRequest() throws Exception {
        String json = """
                {
                    "tagIds": [1, 3, 5]
                }
                """;

        UpdateUserPreferencesRequest request = objectMapper.readValue(json, UpdateUserPreferencesRequest.class);

        assertThat(request.getTagIds()).containsExactlyInAnyOrder(1L, 3L, 5L);
    }

    @Test
    @DisplayName("Should serialize and deserialize UserPreferencesResponse JSON")
    void shouldSerializeAndDeserializeResponse() throws Exception {
        TagResponse tag = new TagResponse();
        tag.setId(2L);
        tag.setName("Trekking");
        tag.setDescription("Mountain hiking and trails");

        UserPreferencesResponse response = UserPreferencesResponse.builder()
                .userId(10L)
                .email("traveler@example.com")
                .name("Alex")
                .onboardingCompleted(true)
                .interests(Set.of(tag))
                .build();

        String json = objectMapper.writeValueAsString(response);
        assertThat(json).contains("\"userId\":10");
        assertThat(json).contains("\"email\":\"traveler@example.com\"");
        assertThat(json).contains("\"onboardingCompleted\":true");
        assertThat(json).contains("\"name\":\"Trekking\"");

        UserPreferencesResponse deserialized = objectMapper.readValue(json, UserPreferencesResponse.class);
        assertThat(deserialized.getUserId()).isEqualTo(10L);
        assertThat(deserialized.getEmail()).isEqualTo("traveler@example.com");
        assertThat(deserialized.getOnboardingCompleted()).isTrue();
        assertThat(deserialized.getInterests()).hasSize(1);
    }
}
