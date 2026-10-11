package com.tourism.backend.recommendation.service;

import com.tourism.backend.attraction.dto.AttractionResponse;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.attraction.mapper.AttractionMapper;
import com.tourism.backend.attraction.repository.AttractionRepository;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.recommendation.dto.RecommendedAttractionResponse;
import com.tourism.backend.tag.entity.Tag;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttractionRecommendationServiceTest {

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttractionMapper attractionMapper;

    @InjectMocks
    private AttractionRecommendationServiceImpl recommendationService;

    private User testUser;
    private Tag tagNature;
    private Tag tagAdventure;
    private Tag tagCulture;
    private Destination testDestination;

    private Attraction attractionDoubleMatch;
    private Attraction attractionSingleMatch;
    private Attraction attractionNoMatch;

    @BeforeEach
    void setUp() {
        tagNature = new Tag();
        tagNature.setId(1L);
        tagNature.setName("Nature");

        tagAdventure = new Tag();
        tagAdventure.setId(2L);
        tagAdventure.setName("Adventure");

        tagCulture = new Tag();
        tagCulture.setId(3L);
        tagCulture.setName("Culture");

        testDestination = new Destination();
        testDestination.setId(10L);
        testDestination.setName("Cherrapunji");

        testUser = User.builder()
                .name("Bob Traveler")
                .email("bob@example.com")
                .role(Role.USER)
                .enabled(true)
                .onboardingCompleted(true)
                .interests(new HashSet<>(Set.of(tagNature, tagAdventure)))
                .build();
        testUser.setId(201L);

        attractionDoubleMatch = new Attraction();
        attractionDoubleMatch.setId(101L);
        attractionDoubleMatch.setName("Living Root Bridge Trek");
        attractionDoubleMatch.setDestination(testDestination);
        attractionDoubleMatch.setFeatured(true);
        attractionDoubleMatch.setDisplayOrder(1);
        attractionDoubleMatch.setTags(new HashSet<>(Set.of(tagNature, tagAdventure)));

        attractionSingleMatch = new Attraction();
        attractionSingleMatch.setId(102L);
        attractionSingleMatch.setName("Nohkalikai Falls Viewpoint");
        attractionSingleMatch.setDestination(testDestination);
        attractionSingleMatch.setFeatured(false);
        attractionSingleMatch.setDisplayOrder(2);
        attractionSingleMatch.setTags(new HashSet<>(Set.of(tagNature)));

        attractionNoMatch = new Attraction();
        attractionNoMatch.setId(103L);
        attractionNoMatch.setName("Monolith Heritage Site");
        attractionNoMatch.setDestination(testDestination);
        attractionNoMatch.setFeatured(true);
        attractionNoMatch.setDisplayOrder(3);
        attractionNoMatch.setTags(new HashSet<>(Set.of(tagCulture)));

        lenient().when(attractionMapper.toResponse(any(Attraction.class)))
                .thenAnswer(inv -> {
                    Attraction a = inv.getArgument(0);
                    AttractionResponse resp = new AttractionResponse();
                    resp.setId(a.getId());
                    resp.setName(a.getName());
                    resp.setFeatured(a.getFeatured());
                    resp.setDisplayOrder(a.getDisplayOrder());
                    if (a.getDestination() != null) {
                        resp.setDestinationId(a.getDestination().getId());
                        resp.setDestinationName(a.getDestination().getName());
                    }
                    return resp;
                });
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, Collections.emptyList()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("Should rank attractions by tag match count with clear explanations for authenticated user")
    void shouldRankAttractionsByTagMatches() {
        authenticateUser(testUser);
        when(userRepository.findWithInterestsByEmailIgnoreCase("bob@example.com"))
                .thenReturn(Optional.of(testUser));
        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(attractionNoMatch, attractionDoubleMatch, attractionSingleMatch));

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 10L, null);

        assertThat(results).hasSize(3);

        // 1st place: 2 matches (Nature, Adventure)
        RecommendedAttractionResponse first = results.get(0);
        assertThat(first.getAttraction().getId()).isEqualTo(101L);
        assertThat(first.getIsFallback()).isFalse();
        assertThat(first.getMatchedTags()).containsExactly("Adventure", "Nature");
        assertThat(first.getMatchScore()).isGreaterThanOrEqualTo(20.0);
        assertThat(first.getExplanation()).isEqualTo("Matches your interest in Adventure, Nature");

        // 2nd place: 1 match (Nature)
        RecommendedAttractionResponse second = results.get(1);
        assertThat(second.getAttraction().getId()).isEqualTo(102L);
        assertThat(second.getIsFallback()).isFalse();
        assertThat(second.getMatchedTags()).containsExactly("Nature");
        assertThat(second.getMatchScore()).isGreaterThan(9.0);
        assertThat(second.getExplanation()).isEqualTo("Matches your interest in Nature");

        // 3rd place: 0 matches -> fallback
        RecommendedAttractionResponse third = results.get(2);
        assertThat(third.getAttraction().getId()).isEqualTo(103L);
        assertThat(third.getIsFallback()).isTrue();
        assertThat(third.getMatchedTags()).isEmpty();
        assertThat(third.getMatchScore()).isLessThan(2.0);
        assertThat(third.getExplanation()).contains("Popular featured attraction in Cherrapunji");
    }

    @Test
    @DisplayName("Should break ties deterministically using featured status, displayOrder, and name")
    void shouldBreakTiesDeterministically() {
        authenticateUser(testUser);
        when(userRepository.findWithInterestsByEmailIgnoreCase("bob@example.com"))
                .thenReturn(Optional.of(testUser));

        // Two attractions both matching 1 tag ("Nature")
        Attraction tieA = new Attraction();
        tieA.setId(201L);
        tieA.setName("Attraction Alpha");
        tieA.setDestination(testDestination);
        tieA.setFeatured(false);
        tieA.setDisplayOrder(1);
        tieA.setTags(new HashSet<>(Set.of(tagNature)));

        Attraction tieB = new Attraction();
        tieB.setId(202L);
        tieB.setName("Attraction Beta");
        tieB.setDestination(testDestination);
        tieB.setFeatured(true); // Featured beats non-featured on tie
        tieB.setDisplayOrder(2);
        tieB.setTags(new HashSet<>(Set.of(tagNature)));

        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(tieA, tieB));

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 10L, null);

        assertThat(results).hasSize(2);
        // Featured tieB should rank first despite higher display order
        assertThat(results.get(0).getAttraction().getId()).isEqualTo(202L);
        assertThat(results.get(1).getAttraction().getId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should return eligible attractions in default discovery order when user has no preferences")
    void shouldReturnDefaultDiscoveryOrderWhenUserHasNoPreferences() {
        testUser.setInterests(Collections.emptySet());
        authenticateUser(testUser);
        when(userRepository.findWithInterestsByEmailIgnoreCase("bob@example.com"))
                .thenReturn(Optional.of(testUser));
        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(attractionSingleMatch, attractionNoMatch, attractionDoubleMatch));

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 10L, null);

        assertThat(results).hasSize(3);
        // All should be marked as fallback
        assertThat(results).allMatch(RecommendedAttractionResponse::getIsFallback);
        // Featured items (101L displayOrder 1, 103L displayOrder 3) rank before non-featured (102L)
        assertThat(results.get(0).getAttraction().getId()).isEqualTo(101L);
        assertThat(results.get(1).getAttraction().getId()).isEqualTo(103L);
        assertThat(results.get(2).getAttraction().getId()).isEqualTo(102L);
    }

    @Test
    @DisplayName("Should return default discovery order for guest / unauthenticated requests")
    void shouldReturnDefaultDiscoveryOrderForGuest() {
        SecurityContextHolder.clearContext();
        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(attractionSingleMatch, attractionNoMatch, attractionDoubleMatch));

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 10L, null);

        assertThat(results).hasSize(3);
        assertThat(results).allMatch(RecommendedAttractionResponse::getIsFallback);
        assertThat(results.get(0).getAttraction().getId()).isEqualTo(101L);
        verify(userRepository, never()).findWithInterestsByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("Should return default discovery order for anonymousUser token")
    void shouldHandleAnonymousUserTokenSafely() {
        Authentication anonAuth = new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        SecurityContextHolder.getContext().setAuthentication(anonAuth);

        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(attractionDoubleMatch));

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 10L, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIsFallback()).isTrue();
        verify(userRepository, never()).findWithInterestsByEmailIgnoreCase(any());
    }

    @Test
    @DisplayName("Should return all eligible attractions as fallback when user preferences match nothing")
    void shouldFallbackWhenUserPreferencesMatchNoAttractions() {
        Tag tagWildlife = new Tag();
        tagWildlife.setId(99L);
        tagWildlife.setName("Wildlife");

        testUser.setInterests(new HashSet<>(Set.of(tagWildlife)));
        authenticateUser(testUser);
        when(userRepository.findWithInterestsByEmailIgnoreCase("bob@example.com"))
                .thenReturn(Optional.of(testUser));
        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(attractionDoubleMatch, attractionSingleMatch));

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 10L, null);

        // Never empty! Both attractions are returned as fallbacks
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(RecommendedAttractionResponse::getIsFallback);
    }

    @Test
    @DisplayName("Should respect limit parameter")
    void shouldRespectLimitParameter() {
        SecurityContextHolder.clearContext();
        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(attractionDoubleMatch, attractionSingleMatch, attractionNoMatch));

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 10L, 2);

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list when no attractions exist in repository for the given filter")
    void shouldReturnEmptyListWhenNoAttractionsExist() {
        SecurityContextHolder.clearContext();
        when(attractionRepository.findAll(any(Specification.class)))
                .thenReturn(Collections.emptyList());

        List<RecommendedAttractionResponse> results = recommendationService.getRecommendations(null, 999L, null);

        assertThat(results).isEmpty();
    }
}
