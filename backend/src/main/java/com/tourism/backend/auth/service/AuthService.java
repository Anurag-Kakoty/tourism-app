package com.tourism.backend.auth.service;

import com.tourism.backend.auth.dto.RegisterRequest;
import com.tourism.backend.auth.dto.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);
}