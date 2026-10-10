package com.realestate.dto;

import com.realestate.entity.VerificationStatus;
import jakarta.validation.constraints.*;

public final class AgentProfileDtos {
    private AgentProfileDtos() { }

    public record UpdateAgentProfileRequest(@Size(max = 140) String agencyName, @Size(max = 100) String licenseNumber,
        @PositiveOrZero Integer experience, @Size(max = 2000) String description) { }

    public record AgentProfileResponse(Long id, Long userId, String agencyName, String licenseNumber,
        Integer experience, String description, VerificationStatus verificationStatus) { }
}

