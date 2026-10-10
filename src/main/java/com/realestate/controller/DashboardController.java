package com.realestate.controller;

import com.realestate.security.AppUserDetails;
import com.realestate.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/api/user/dashboard")
    public Map<String, Long> user(@AuthenticationPrincipal AppUserDetails principal) {
        return dashboardService.userDashboard(principal.id());
    }

    @GetMapping("/api/agent/dashboard")
    @PreAuthorize("hasRole('AGENT')")
    public Map<String, Long> agent(@AuthenticationPrincipal AppUserDetails principal) {
        return dashboardService.agentDashboard(principal.id());
    }
}

