package com.realestate.service.impl;

import com.realestate.dto.AuthDtos.*;
import com.realestate.entity.*;
import com.realestate.exception.*;
import com.realestate.mapper.ApiMapper;
import com.realestate.repository.*;
import com.realestate.security.*;
import com.realestate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) throw new BadRequestException("Administrator registration is not public.");
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new BadRequestException("An account with this email already exists.");
        }
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        userRepository.save(user);
        if (user.getRole() == Role.AGENT) {
            AgentProfile profile = new AgentProfile();
            profile.setUser(user);
            agentProfileRepository.save(profile);
        }
        return token(user);
    }

    @Override @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));
        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }
        return token(user);
    }

    private AuthResponse token(User user) {
        AppUserDetails principal = new AppUserDetails(user);
        return new AuthResponse(jwtService.generateToken(principal), "Bearer", ApiMapper.user(user));
    }
}

