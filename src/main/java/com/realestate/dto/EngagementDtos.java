package com.realestate.dto;

import java.time.LocalDateTime;

public final class EngagementDtos {
    private EngagementDtos() { }
    public record NotificationResponse(Long id, String title, String message, String type, boolean read, LocalDateTime createdAt) { }
}

