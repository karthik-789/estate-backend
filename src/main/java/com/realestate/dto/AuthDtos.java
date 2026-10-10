package com.realestate.dto;

import com.realestate.entity.Role;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() { }

    public record RegisterRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 160) String email,
        @Size(max = 30) String phone,
        @NotBlank @Size(min = 10, max = 100) @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must include uppercase, lowercase, and a digit.") String password,
        @NotNull Role role
    ) { }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }

    public record AuthResponse(String accessToken, String tokenType, UserResponse user) { }

    public record UserResponse(Long id, String fullName, String email, String phone, Role role,
                               String profileImage, String address, String city, String state, boolean active) { }

    public record UpdateProfileRequest(@NotBlank @Size(max = 120) String fullName, @Size(max = 30) String phone,
                                       @Size(max = 500) String address, @Size(max = 80) String city,
                                       @Size(max = 80) String state, String profileImage) { }
}

