package com.realestate.controller;

import com.realestate.dto.EngagementDtos.NotificationResponse;
import com.realestate.dto.PropertyDtos.PropertyResponse;
import com.realestate.security.AppUserDetails;
import com.realestate.service.EngagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequiredArgsConstructor
public class EngagementController {
    private final EngagementService engagementService;

    @PostMapping("/api/saved-properties/{propertyId}")
    @PreAuthorize("hasRole('USER')")
    public Map<String, String> save(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long propertyId) {
        engagementService.save(principal.id(), propertyId);
        return Map.of("message", "Property saved.");
    }

    @DeleteMapping("/api/saved-properties/{propertyId}")
    @PreAuthorize("hasRole('USER')")
    public Map<String, String> remove(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long propertyId) {
        engagementService.remove(principal.id(), propertyId);
        return Map.of("message", "Property removed.");
    }

    @GetMapping("/api/saved-properties")
    @PreAuthorize("hasRole('USER')")
    public Page<PropertyResponse> saved(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return engagementService.saved(principal.id(), pageable);
    }

    @GetMapping("/api/notifications")
    @PreAuthorize("hasAnyRole('USER','AGENT')")
    public Page<NotificationResponse> notifications(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return engagementService.notifications(principal.id(), pageable);
    }

    @PutMapping("/api/notifications/{id}/read")
    @PreAuthorize("hasAnyRole('USER','AGENT')")
    public Map<String, String> read(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        engagementService.read(principal.id(), id);
        return Map.of("message", "Notification marked as read.");
    }
}

