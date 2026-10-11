package com.tourism.backend.user.service;

import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.tag.dto.TagResponse;
import com.tourism.backend.tag.entity.Tag;
import com.tourism.backend.tag.mapper.TagMapper;
import com.tourism.backend.tag.repository.TagRepository;
import com.tourism.backend.user.dto.UpdateUserPreferencesRequest;
import com.tourism.backend.user.dto.UserPreferencesResponse;
import com.tourism.backend.user.entity.Role;
import com.tourism.backend.user.entity.User;
import com.tourism.backend.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferencesServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private UserPreferencesServiceImpl userPreferencesService;

    private User testUser;
    private Tag tagNature;
    private Tag tagAdventure;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Alice Traveller")
                .email("alice@example.com")
                .password("encoded_pass")
                .role(Role.USER)
                .enabled(true)
                .onboardingCompleted(false)
                .interests(new HashSet<>())
                .build();
        testUser.setId(101L);

        tagNature = new Tag();
        tagNature.setId(1L);
        tagNature.setName("Nature");
        tagNature.setDescription("Natural scenery");

        tagAdventure = new Tag();
        tagAdventure.setId(2L);
        tagAdventure.setName("Adventure");
        tagAdventure.setDescription("Adventure sports and activities");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(), null, Collections.emptyList()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return preferences with onboardingCompleted=false for new user")
    void shouldGetPreferencesForNewUser() {
        when(userRepository.findWithInterestsByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(testUser));

        UserPreferencesResponse response = userPreferencesService.getPreferences();

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(101L);
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getName()).isEqualTo("Alice Traveller");
        assertThat(response.getOnboardingCompleted()).isFalse();
        assertThat(response.getInterests()).isEmpty();
    }

    @Test
    @DisplayName("Should return preferences with saved tags for user who completed onboarding")
    void shouldGetPreferencesForUserWithInterests() {
        testUser.setOnboardingCompleted(true);
        testUser.getInterests().add(tagNature);

        TagResponse tagResponse = new TagResponse();
        tagResponse.setId(1L);
        tagResponse.setName("Nature");
        tagResponse.setDescription("Natural scenery");

        when(userRepository.findWithInterestsByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(testUser));
        when(tagMapper.toResponse(tagNature)).thenReturn(tagResponse);

        UserPreferencesResponse response = userPreferencesService.getPreferences();

        assertThat(response.getOnboardingCompleted()).isTrue();
        assertThat(response.getInterests()).hasSize(1);
        assertThat(response.getInterests().iterator().next().getName()).isEqualTo("Nature");
    }

    @Test
    @DisplayName("Should update user interests and mark onboarding as completed")
    void shouldUpdatePreferencesWithValidTags() {
        UpdateUserPreferencesRequest request = UpdateUserPreferencesRequest.builder()
                .tagIds(Set.of(1L, 2L))
                .build();

        TagResponse resp1 = new TagResponse();
        resp1.setId(1L);
        resp1.setName("Nature");

        TagResponse resp2 = new TagResponse();
        resp2.setId(2L);
        resp2.setName("Adventure");

        when(userRepository.findWithInterestsByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(testUser));
        when(tagRepository.findAllById(Set.of(1L, 2L)))
                .thenReturn(List.of(tagNature, tagAdventure));
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(tagMapper.toResponse(tagNature)).thenReturn(resp1);
        when(tagMapper.toResponse(tagAdventure)).thenReturn(resp2);

        UserPreferencesResponse response = userPreferencesService.updatePreferences(request);

        assertThat(response).isNotNull();
        assertThat(response.getOnboardingCompleted()).isTrue();
        assertThat(response.getInterests()).hasSize(2);
        verify(userRepository).save(testUser);
        assertThat(testUser.getOnboardingCompleted()).isTrue();
        assertThat(testUser.getInterests()).containsExactlyInAnyOrder(tagNature, tagAdventure);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when any requested tag ID does not exist")
    void shouldThrowWhenTagDoesNotExist() {
        UpdateUserPreferencesRequest request = UpdateUserPreferencesRequest.builder()
                .tagIds(Set.of(1L, 999L))
                .build();

        when(userRepository.findWithInterestsByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(testUser));
        when(tagRepository.findAllById(Set.of(1L, 999L)))
                .thenReturn(List.of(tagNature)); // 999L not found

        assertThatThrownBy(() -> userPreferencesService.updatePreferences(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should mark onboarding completed when user skips preferences")
    void shouldSkipPreferencesSuccessfully() {
        when(userRepository.findWithInterestsByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserPreferencesResponse response = userPreferencesService.skipPreferences();

        assertThat(response).isNotNull();
        assertThat(response.getOnboardingCompleted()).isTrue();
        assertThat(response.getInterests()).isEmpty();
        verify(userRepository).save(testUser);
        assertThat(testUser.getOnboardingCompleted()).isTrue();
    }

    @Test
    @DisplayName("Should preserve existing interests when user skips preferences")
    void shouldPreserveExistingInterestsWhenUserSkipsPreferences() {
        testUser.getInterests().add(tagNature);

        TagResponse tagResponse = new TagResponse();
        tagResponse.setId(1L);
        tagResponse.setName("Nature");

        when(userRepository.findWithInterestsByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(tagMapper.toResponse(tagNature)).thenReturn(tagResponse);

        UserPreferencesResponse response = userPreferencesService.skipPreferences();

        assertThat(response).isNotNull();
        assertThat(response.getOnboardingCompleted()).isTrue();
        assertThat(response.getInterests()).hasSize(1);
        assertThat(response.getInterests().iterator().next().getName()).isEqualTo("Nature");
        verify(userRepository).save(testUser);
        assertThat(testUser.getOnboardingCompleted()).isTrue();
        assertThat(testUser.getInterests()).containsExactly(tagNature);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when authenticated user is not in database")
    void shouldThrowWhenUserNotInDatabase() {
        when(userRepository.findWithInterestsByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userPreferencesService.getPreferences())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Authenticated user not found");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when security context has no authentication")
    void shouldThrowWhenSecurityContextEmpty() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> userPreferencesService.getPreferences())
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
