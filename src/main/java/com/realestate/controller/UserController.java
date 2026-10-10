package com.realestate.controller;

import com.realestate.dto.AuthDtos.*;
import com.realestate.exception.UnauthorizedException;
import com.realestate.security.AppUserDetails;
import com.realestate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AppUserDetails principal) {
        return userService.profile(principal.id());
    }

    @PutMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal AppUserDetails principal,
                                 @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(principal.id(), request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> users(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping("/{id}")
    public UserResponse user(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        requireSelfOrAdmin(principal, id);
        return userService.profile(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id,
                               @Valid @RequestBody UpdateProfileRequest request) {
        requireSelfOrAdmin(principal, id);
        return userService.updateProfile(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deactivate(@PathVariable Long id) {
        userService.setActive(id, false);
        return Map.of("message", "User deactivated.");
    }

    private void requireSelfOrAdmin(AppUserDetails principal, Long id) {
        if (!principal.id().equals(id) && !principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("You cannot access this profile.");
        }
    }
}

