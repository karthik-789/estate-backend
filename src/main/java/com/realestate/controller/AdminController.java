package com.realestate.controller;

import com.realestate.dto.AuthDtos.UserResponse;
import com.realestate.dto.PropertyDtos.PropertyResponse;
import com.realestate.dto.WorkflowDtos.*;
import com.realestate.service.*;
import com.realestate.entity.VerificationStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/admin") @PreAuthorize("hasRole('ADMIN')") @RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final WorkflowService workflowService;
    private final AgentProfileService agentProfileService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() { return adminService.dashboard(); }

    @GetMapping("/users")
    public Page<UserResponse> users(Pageable pageable) { return userService.list(pageable); }

    @PutMapping("/users/{id}/status")
    public Map<String, String> userStatus(@PathVariable Long id, @RequestParam boolean active) {
        userService.setActive(id, active);
        return Map.of("message", active ? "User activated." : "User blocked.");
    }

    @GetMapping("/agents")
    public Page<UserResponse> agents(Pageable pageable) { return adminService.agents(pageable); }

    @PutMapping("/agents/{userId}/verification")
    public Object agentVerification(@PathVariable Long userId, @RequestParam VerificationStatus status) {
        return agentProfileService.verification(userId, status);
    }

    @GetMapping("/properties/pending")
    public Page<PropertyResponse> pendingProperties(Pageable pageable) { return adminService.pendingProperties(pageable); }

    @PutMapping("/properties/{id}/approve")
    public PropertyResponse approve(@PathVariable Long id) { return adminService.approveProperty(id); }

    @PutMapping("/properties/{id}/reject")
    public PropertyResponse reject(@PathVariable Long id, @Valid @RequestBody RejectPropertyRequest request) {
        return adminService.rejectProperty(id, request);
    }

    @GetMapping("/enquiries")
    public Page<EnquiryResponse> enquiries(Pageable pageable) { return workflowService.allEnquiries(pageable); }

    @GetMapping("/bookings")
    public Page<BookingResponse> bookings(Pageable pageable) { return workflowService.allBookings(pageable); }

    @GetMapping("/payments")
    public Page<PaymentResponse> payments(Pageable pageable) { return workflowService.allPayments(pageable); }

    @GetMapping("/reports")
    public DashboardResponse reports() { return adminService.dashboard(); }
}

