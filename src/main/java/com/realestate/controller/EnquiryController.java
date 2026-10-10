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

@RestController @RequestMapping("/api/enquiries") @RequiredArgsConstructor
public class EnquiryController {
    private final WorkflowService workflowService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public EnquiryResponse create(@AuthenticationPrincipal AppUserDetails principal, @Valid @RequestBody EnquiryRequest request) {
        return workflowService.createEnquiry(principal.id(), request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public Page<EnquiryResponse> my(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return workflowService.userEnquiries(principal.id(), pageable);
    }

    @GetMapping("/agent")
    @PreAuthorize("hasRole('AGENT')")
    public Page<EnquiryResponse> agent(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return workflowService.agentEnquiries(principal.id(), pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public EnquiryResponse update(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id,
                                  @Valid @RequestBody UpdateEnquiryRequest request) {
        return workflowService.updateEnquiry(principal.id(), principal.user().getRole(), id, request);
    }
}

