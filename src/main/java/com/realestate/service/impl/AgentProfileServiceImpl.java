package com.realestate.service.impl;

import com.realestate.dto.AgentProfileDtos.*;
import com.realestate.entity.*;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.repository.AgentProfileRepository;
import com.realestate.service.AgentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AgentProfileServiceImpl implements AgentProfileService {
    private final AgentProfileRepository profiles;

    @Override @Transactional(readOnly = true)
    public AgentProfileResponse profile(Long userId) { return response(require(userId)); }

    @Override @Transactional
    public AgentProfileResponse update(Long userId, UpdateAgentProfileRequest request) {
        AgentProfile profile = require(userId);
        profile.setAgencyName(request.agencyName());
        profile.setLicenseNumber(request.licenseNumber());
        profile.setExperience(request.experience());
        profile.setDescription(request.description());
        profile.setVerificationStatus(VerificationStatus.PENDING);
        return response(profile);
    }

    @Override @Transactional
    public AgentProfileResponse verification(Long userId, VerificationStatus status) {
        AgentProfile profile = require(userId);
        profile.setVerificationStatus(status);
        return response(profile);
    }

    private AgentProfile require(Long userId) {
        return profiles.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Agent profile not found."));
    }

    private AgentProfileResponse response(AgentProfile profile) {
        return new AgentProfileResponse(profile.getId(), profile.getUser().getId(), profile.getAgencyName(),
            profile.getLicenseNumber(), profile.getExperience(), profile.getDescription(), profile.getVerificationStatus());
    }
}

