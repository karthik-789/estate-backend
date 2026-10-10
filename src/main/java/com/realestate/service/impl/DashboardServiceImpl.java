package com.realestate.service.impl;

import com.realestate.entity.*;
import com.realestate.repository.*;
import com.realestate.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final EnquiryRepository enquiryRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PropertyRepository propertyRepository;

    @Override @Transactional(readOnly = true)
    public Map<String, Long> userDashboard(Long userId) {
        return Map.of(
            "enquiries", enquiryRepository.countByUserId(userId),
            "bookings", bookingRepository.countByUserId(userId),
            "payments", paymentRepository.countByUserId(userId)
        );
    }

    @Override @Transactional(readOnly = true)
    public Map<String, Long> agentDashboard(Long agentId) {
        long properties = propertyRepository.count((root, query, cb) -> cb.equal(root.get("owner").get("id"), agentId));
        long approved = propertyRepository.count((root, query, cb) -> cb.and(
            cb.equal(root.get("owner").get("id"), agentId),
            cb.equal(root.get("verificationStatus"), VerificationStatus.APPROVED)));
        long pending = propertyRepository.count((root, query, cb) -> cb.and(
            cb.equal(root.get("owner").get("id"), agentId),
            cb.equal(root.get("verificationStatus"), VerificationStatus.PENDING)));
        return Map.of("properties", properties, "approvedProperties", approved, "pendingProperties", pending,
            "enquiries", enquiryRepository.countByAgentId(agentId), "bookings", bookingRepository.countByAgentId(agentId));
    }
}

