package com.realestate.service;

import com.realestate.dto.AuthDtos.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

