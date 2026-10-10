package com.realestate.service.impl;

import com.realestate.dto.EngagementDtos.NotificationResponse;
import com.realestate.dto.PropertyDtos.PropertyResponse;
import com.realestate.entity.*;
import com.realestate.exception.*;
import com.realestate.mapper.ApiMapper;
import com.realestate.repository.*;
import com.realestate.service.EngagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class EngagementServiceImpl implements EngagementService {
    private final SavedPropertyRepository savedPropertyRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override @Transactional
    public void save(Long userId, Long propertyId) {
        if (savedPropertyRepository.findByUserIdAndPropertyId(userId, propertyId).isPresent()) return;
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property not found."));
        if (property.getVerificationStatus() != VerificationStatus.APPROVED || property.getStatus() == PropertyStatus.INACTIVE) {
            throw new BadRequestException("Only available, approved properties can be saved.");
        }
        SavedProperty saved = new SavedProperty();
        saved.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found.")));
        saved.setProperty(property);
        savedPropertyRepository.save(saved);
    }

    @Override @Transactional
    public void remove(Long userId, Long propertyId) {
        SavedProperty saved = savedPropertyRepository.findByUserIdAndPropertyId(userId, propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Saved property not found."));
        savedPropertyRepository.delete(saved);
    }

    @Override @Transactional(readOnly = true)
    public Page<PropertyResponse> saved(Long userId, Pageable pageable) {
        return savedPropertyRepository.findByUserId(userId, pageable).map(item -> ApiMapper.property(item.getProperty()));
    }

    @Override @Transactional(readOnly = true)
    public Page<NotificationResponse> notifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable).map(item ->
            new NotificationResponse(item.getId(), item.getTitle(), item.getMessage(), item.getType(), item.isRead(), item.getCreatedAt()));
    }

    @Override @Transactional
    public void read(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
        if (!notification.getUser().getId().equals(userId)) throw new UnauthorizedException("You cannot modify this notification.");
        notification.setRead(true);
    }
}

