package com.tourism.backend.user.service;

import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.tag.dto.TagResponse;
import com.tourism.backend.tag.entity.Tag;
import com.tourism.backend.tag.mapper.TagMapper;
import com.tourism.backend.tag.repository.TagRepository;
import com.tourism.backend.user.dto.UpdateUserPreferencesRequest;
import com.tourism.backend.user.dto.UserPreferencesResponse;
import com.tourism.backend.user.entity.User;
import com.tourism.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional(readOnly = true)
    public UserPreferencesResponse getPreferences() {
        User user = getCurrentUserWithInterests();
        return mapToResponse(user);
    }

    @Override
    public UserPreferencesResponse updatePreferences(UpdateUserPreferencesRequest request) {
        User user = getCurrentUserWithInterests();

        Set<Long> requestedTagIds = request.getTagIds() != null
                ? request.getTagIds()
                : Collections.emptySet();

        Set<Tag> resolvedTags = new HashSet<>();

        if (!requestedTagIds.isEmpty()) {
            List<Tag> foundTags = tagRepository.findAllById(requestedTagIds);

            if (foundTags.size() != requestedTagIds.size()) {
                Set<Long> foundIds = foundTags.stream()
                        .map(Tag::getId)
                        .collect(Collectors.toSet());

                Set<Long> missingIds = requestedTagIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .collect(Collectors.toSet());

                throw new ResourceNotFoundException("Tags not found with IDs: " + missingIds);
            }

            resolvedTags.addAll(foundTags);
        }

        user.setInterests(resolvedTags);
        user.setOnboardingCompleted(true);

        User savedUser = userRepository.save(user);

        log.info("Updated preferences for user ID {}: {} interest tags, onboarding completed.",
                savedUser.getId(), resolvedTags.size());

        return mapToResponse(savedUser);
    }

    @Override
    public UserPreferencesResponse skipPreferences() {
        User user = getCurrentUserWithInterests();

        user.setOnboardingCompleted(true);
        User savedUser = userRepository.save(user);

        log.info("User ID {} skipped onboarding interest selection.", savedUser.getId());

        return mapToResponse(savedUser);
    }

    private User getCurrentUserWithInterests() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found.");
        }

        String email = authentication.getName();

        return userRepository
                .findWithInterestsByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found with email: " + email));
    }

    private UserPreferencesResponse mapToResponse(User user) {
        Set<TagResponse> interestResponses = user.getInterests() == null
                ? Collections.emptySet()
                : user.getInterests().stream()
                        .map(tagMapper::toResponse)
                        .sorted(Comparator.comparing(TagResponse::getName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        return UserPreferencesResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .onboardingCompleted(Boolean.TRUE.equals(user.getOnboardingCompleted()))
                .interests(interestResponses)
                .build();
    }
}
