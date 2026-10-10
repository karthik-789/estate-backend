package com.realestate.controller;

import com.realestate.dto.WorkflowDtos.*;
import com.realestate.security.AppUserDetails;
import com.realestate.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/bookings") @RequiredArgsConstructor
public class BookingController {
    private final WorkflowService workflowService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public BookingResponse create(@AuthenticationPrincipal AppUserDetails principal, @Valid @RequestBody BookingRequest request) {
        return workflowService.createBooking(principal.id(), request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public Page<BookingResponse> my(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return workflowService.userBookings(principal.id(), pageable);
    }

    @GetMapping("/agent")
    @PreAuthorize("hasRole('AGENT')")
    public Page<BookingResponse> agent(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return workflowService.agentBookings(principal.id(), pageable);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public BookingResponse update(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id,
                                  @Valid @RequestBody UpdateBookingStatusRequest request) {
        return workflowService.updateBooking(principal.id(), principal.user().getRole(), id, request);
    }
}

