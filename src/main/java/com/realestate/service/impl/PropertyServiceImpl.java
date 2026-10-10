package com.realestate.service.impl;

import com.realestate.dto.PropertyDtos.*;
import com.realestate.entity.*;
import com.realestate.exception.*;
import com.realestate.mapper.ApiMapper;
import com.realestate.repository.*;
import com.realestate.service.PropertyService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service @RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository imageRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override @Transactional(readOnly = true)
    public Page<PropertyResponse> search(PropertyFilter filter, Pageable pageable) {
        return propertyRepository.findAll(publicSpecification(filter), pageable).map(ApiMapper::property);
    }

    @Override @Transactional(readOnly = true)
    public PropertyResponse approvedById(Long id) {
        Property property = requireProperty(id);
        if (property.getVerificationStatus() != VerificationStatus.APPROVED || property.getStatus() == PropertyStatus.INACTIVE) {
            throw new ResourceNotFoundException("Property not found.");
        }
        return ApiMapper.property(property);
    }

    @Override @Transactional(readOnly = true)
    public Page<PropertyResponse> mine(Long ownerId, Pageable pageable) {
        return propertyRepository.findByOwnerId(ownerId, pageable).map(ApiMapper::property);
    }

    @Override @Transactional
    public PropertyResponse create(Long ownerId, PropertyRequest request) {
        Property property = new Property();
        apply(property, request);
        property.setOwner(userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("User not found.")));
        property.setVerificationStatus(VerificationStatus.PENDING);
        property.setStatus(PropertyStatus.AVAILABLE);
        return ApiMapper.property(propertyRepository.save(property));
    }

    @Override @Transactional
    public PropertyResponse update(Long ownerId, Long id, PropertyRequest request) {
        Property property = owned(ownerId, id);
        apply(property, request);
        property.setVerificationStatus(VerificationStatus.PENDING);
        property.setRejectionReason(null);
        return ApiMapper.property(property);
    }

    @Override @Transactional
    public void deactivate(Long ownerId, Long id) {
        Property property = owned(ownerId, id);
        property.setStatus(PropertyStatus.INACTIVE);
    }

    @Override @Transactional
    public List<ImageResponse> uploadImages(Long ownerId, Long id, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) throw new BadRequestException("At least one image is required.");
        Property property = owned(ownerId, id);
        List<ImageResponse> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            PropertyImage image = new PropertyImage();
            image.setProperty(property);
            image.setImageUrl(fileStorageService.store(id, file));
            imageRepository.save(image);
            saved.add(new ImageResponse(image.getId(), image.getImageUrl()));
        }
        return saved;
    }

    @Override @Transactional(readOnly = true)
    public Property requireProperty(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found."));
    }

    @Override @Transactional(readOnly = true)
    public PropertyResponse internalById(Long id) { return ApiMapper.property(requireProperty(id)); }

    private Property owned(Long ownerId, Long propertyId) {
        Property property = requireProperty(propertyId);
        if (!property.getOwner().getId().equals(ownerId)) throw new UnauthorizedException("You do not own this property.");
        return property;
    }

    private void apply(Property property, PropertyRequest request) {
        property.setTitle(request.title().trim());
        property.setDescription(request.description().trim());
        property.setPropertyType(request.propertyType());
        property.setListingType(request.listingType());
        property.setPrice(request.price());
        property.setBedrooms(request.bedrooms());
        property.setBathrooms(request.bathrooms());
        property.setArea(request.area());
        property.setAddress(request.address().trim());
        property.setCity(request.city().trim());
        property.setState(request.state().trim());
        property.setPincode(request.pincode());
        property.setLatitude(request.latitude());
        property.setLongitude(request.longitude());
    }

    private Specification<Property> publicSpecification(PropertyFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("verificationStatus"), VerificationStatus.APPROVED));
            predicates.add(builder.notEqual(root.get("status"), PropertyStatus.INACTIVE));
            if (filter.query() != null && !filter.query().isBlank()) {
                String value = "%" + filter.query().toLowerCase() + "%";
                predicates.add(builder.or(builder.like(builder.lower(root.get("title")), value),
                    builder.like(builder.lower(root.get("city")), value),
                    builder.like(builder.lower(root.get("address")), value)));
            }
            if (filter.listingType() != null) predicates.add(builder.equal(root.get("listingType"), filter.listingType()));
            if (filter.city() != null && !filter.city().isBlank()) predicates.add(builder.equal(builder.lower(root.get("city")), filter.city().toLowerCase()));
            if (filter.state() != null && !filter.state().isBlank()) predicates.add(builder.equal(builder.lower(root.get("state")), filter.state().toLowerCase()));
            if (filter.propertyType() != null) predicates.add(builder.equal(root.get("propertyType"), filter.propertyType()));
            if (filter.minPrice() != null) predicates.add(builder.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
            if (filter.maxPrice() != null) predicates.add(builder.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
            if (filter.bedrooms() != null) predicates.add(builder.greaterThanOrEqualTo(root.get("bedrooms"), filter.bedrooms()));
            if (filter.bathrooms() != null) predicates.add(builder.greaterThanOrEqualTo(root.get("bathrooms"), filter.bathrooms()));
            if (filter.minArea() != null) predicates.add(builder.greaterThanOrEqualTo(root.get("area"), filter.minArea()));
            if (filter.maxArea() != null) predicates.add(builder.lessThanOrEqualTo(root.get("area"), filter.maxArea()));
            if (filter.status() != null) predicates.add(builder.equal(root.get("status"), filter.status()));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

