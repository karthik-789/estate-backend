package com.realestate.service;

import com.realestate.dto.PropertyDtos.*;
import com.realestate.entity.Property;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface PropertyService {
    Page<PropertyResponse> search(PropertyFilter filter, Pageable pageable);
    PropertyResponse approvedById(Long id);
    Page<PropertyResponse> mine(Long ownerId, Pageable pageable);
    PropertyResponse create(Long ownerId, PropertyRequest request);
    PropertyResponse update(Long ownerId, Long id, PropertyRequest request);
    void deactivate(Long ownerId, Long id);
    List<ImageResponse> uploadImages(Long ownerId, Long id, List<MultipartFile> files);
    Property requireProperty(Long id);
    PropertyResponse internalById(Long id);
}

