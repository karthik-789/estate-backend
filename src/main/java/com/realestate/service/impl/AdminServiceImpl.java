package com.realestate.service.impl;

import com.realestate.dto.AuthDtos.UserResponse;
import com.realestate.dto.PropertyDtos.PropertyResponse;
import com.realestate.dto.WorkflowDtos.*;
import com.realestate.entity.*;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.mapper.ApiMapper;
import com.realestate.repository.*;
import com.realestate.service.AdminService;
import com.realestate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final EnquiryRepository enquiryRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Override @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        return new DashboardResponse(userRepository.countByRole(Role.USER), userRepository.countByRole(Role.AGENT),
            propertyRepository.count(), propertyRepository.countByVerificationStatus(VerificationStatus.PENDING),
            propertyRepository.countByVerificationStatus(VerificationStatus.APPROVED), bookingRepository.count(),
            enquiryRepository.count(), paymentRepository.count());
    }

    @Override @Transactional(readOnly = true)
    public Page<PropertyResponse> pendingProperties(Pageable pageable) {
        return propertyRepository.findAll((root, query, cb) ->
            cb.equal(root.get("verificationStatus"), VerificationStatus.PENDING), pageable).map(ApiMapper::property);
    }

    @Override @Transactional
    public PropertyResponse approveProperty(Long id) {
        Property property = property(id);
        property.setVerificationStatus(VerificationStatus.APPROVED);
        property.setRejectionReason(null);
        property.setStatus(PropertyStatus.AVAILABLE);
        notificationService.create(property.getOwner(), "Property approved",
            property.getTitle() + " is now published to public search.", "PROPERTY");
        return ApiMapper.property(property);
    }

    @Override @Transactional
    public PropertyResponse rejectProperty(Long id, RejectPropertyRequest request) {
        Property property = property(id);
        property.setVerificationStatus(VerificationStatus.REJECTED);
        property.setRejectionReason(request.reason().trim());
        notificationService.create(property.getOwner(), "Property needs changes",
            property.getTitle() + " was rejected: " + request.reason().trim(), "PROPERTY");
        return ApiMapper.property(property);
    }

    @Override @Transactional(readOnly = true)
    public Page<UserResponse> agents(Pageable pageable) {
        return userRepository.findByRole(Role.AGENT, pageable).map(ApiMapper::user);
    }

    private Property property(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found."));
    }
}

