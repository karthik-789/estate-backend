package com.realestate.dto;

import com.realestate.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class PropertyDtos {
    private PropertyDtos() { }

    public record PropertyRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 5000) String description,
        @NotNull PropertyType propertyType,
        @NotNull ListingType listingType,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @PositiveOrZero Integer bedrooms,
        @PositiveOrZero Integer bathrooms,
        @NotNull @Positive Double area,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Size(max = 80) String city,
        @NotBlank @Size(max = 80) String state,
        @Size(max = 12) String pincode,
        Double latitude,
        Double longitude
    ) { }

    public record ImageResponse(Long id, String imageUrl) { }

    public record OwnerResponse(Long id, String fullName, String email, String phone, String profileImage) { }

    public record PropertyResponse(Long id, String title, String description, PropertyType propertyType,
        ListingType listingType, BigDecimal price, Integer bedrooms, Integer bathrooms, Double area,
        String address, String city, String state, String pincode, Double latitude, Double longitude,
        PropertyStatus status, VerificationStatus verificationStatus, String rejectionReason,
        OwnerResponse owner, List<ImageResponse> images, LocalDateTime createdAt, LocalDateTime updatedAt) { }

    public record PropertyFilter(String query, ListingType listingType, String city, String state,
        PropertyType propertyType, BigDecimal minPrice, BigDecimal maxPrice, Integer bedrooms,
        Integer bathrooms, Double minArea, Double maxArea, PropertyStatus status) { }
}

