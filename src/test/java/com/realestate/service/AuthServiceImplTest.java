package com.realestate.service;

import com.realestate.dto.AuthDtos.*;
import com.realestate.entity.*;
import com.realestate.repository.*;
import com.realestate.security.JwtService;
import com.realestate.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock UserRepository users;
    @Mock AgentProfileRepository profiles;
    @Mock PasswordEncoder passwords;
    @Mock JwtService jwt;
    @InjectMocks AuthServiceImpl service;

    @Test
    void registersAnAgentWithoutAllowingAdminRegistration() {
        when(users.findByEmailIgnoreCase("agent@example.com")).thenReturn(java.util.Optional.empty());
        when(passwords.encode("StrongPassword123")).thenReturn("hashed");
        when(users.save(any(User.class))).thenAnswer(call -> {
            User user = call.getArgument(0);
            user.setId(12L);
            return user;
        });
        when(jwt.generateToken(any())).thenReturn("token");

        AuthResponse response = service.register(new RegisterRequest("Asha Agent", "agent@example.com", "999", "StrongPassword123", Role.AGENT));

        assertThat(response.user().role()).isEqualTo(Role.AGENT);
        assertThat(response.accessToken()).isEqualTo("token");
        verify(profiles).save(argThat(profile -> profile.getUser().getId().equals(12L)));
    }
}

