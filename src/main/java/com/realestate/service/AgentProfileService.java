package com.realestate.service;

import com.realestate.dto.AgentProfileDtos.*;
import com.realestate.entity.VerificationStatus;

public interface AgentProfileService {
    AgentProfileResponse profile(Long userId);
    AgentProfileResponse update(Long userId, UpdateAgentProfileRequest request);
    AgentProfileResponse verification(Long userId, VerificationStatus status);
}

