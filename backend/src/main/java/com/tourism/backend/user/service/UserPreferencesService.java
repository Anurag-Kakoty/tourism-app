package com.tourism.backend.user.service;

import com.tourism.backend.user.dto.UpdateUserPreferencesRequest;
import com.tourism.backend.user.dto.UserPreferencesResponse;

public interface UserPreferencesService {

    UserPreferencesResponse getPreferences();

    UserPreferencesResponse updatePreferences(UpdateUserPreferencesRequest request);

    UserPreferencesResponse skipPreferences();
}
