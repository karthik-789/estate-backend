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

@RestController @RequestMapping("/api/payments") @RequiredArgsConstructor
public class PaymentController {
    private final WorkflowService workflowService;

    @PostMapping("/mock")
    @PreAuthorize("hasRole('USER')")
    public PaymentResponse mockPayment(@AuthenticationPrincipal AppUserDetails principal,
                                       @Valid @RequestBody MockPaymentRequest request) {
        return workflowService.mockPay(principal.id(), request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public Page<PaymentResponse> mine(@AuthenticationPrincipal AppUserDetails principal, Pageable pageable) {
        return workflowService.userPayments(principal.id(), pageable);
    }
}

