package com.realestate.service;

import com.realestate.dto.EngagementDtos.NotificationResponse;
import com.realestate.dto.PropertyDtos.PropertyResponse;
import org.springframework.data.domain.*;

public interface EngagementService {
    void save(Long userId, Long propertyId);
    void remove(Long userId, Long propertyId);
    Page<PropertyResponse> saved(Long userId, Pageable pageable);
    Page<NotificationResponse> notifications(Long userId, Pageable pageable);
    void read(Long userId, Long notificationId);
}

