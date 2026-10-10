package com.realestate.controller;

import com.realestate.dto.AgentProfileDtos.*;
import com.realestate.security.AppUserDetails;
import com.realestate.service.AgentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/agent/profile") @RequiredArgsConstructor
@PreAuthorize("hasRole('AGENT')")
public class AgentProfileController {
    private final AgentProfileService agentProfileService;

    @GetMapping
    public AgentProfileResponse profile(@AuthenticationPrincipal AppUserDetails principal) {
        return agentProfileService.profile(principal.id());
    }

    @PutMapping
    public AgentProfileResponse update(@AuthenticationPrincipal AppUserDetails principal,
                                       @Valid @RequestBody UpdateAgentProfileRequest request) {
        return agentProfileService.update(principal.id(), request);
    }
}

