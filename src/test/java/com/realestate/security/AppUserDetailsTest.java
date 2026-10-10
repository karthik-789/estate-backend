package com.realestate.security;

import com.realestate.entity.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AppUserDetailsTest {
    @Test
    void exposesRoleAsSpringSecurityAuthority() {
        User user = new User(); user.setId(1L); user.setEmail("admin@example.com"); user.setRole(Role.ADMIN); user.setActive(true);
        AppUserDetails details = new AppUserDetails(user);
        assertThat(details.getAuthorities()).extracting(Object::toString).contains("ROLE_ADMIN");
        assertThat(details.isEnabled()).isTrue();
    }
}

