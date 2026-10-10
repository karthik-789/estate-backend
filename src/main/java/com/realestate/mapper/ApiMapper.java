package com.realestate.mapper;

import com.realestate.dto.*;
import com.realestate.dto.AuthDtos.*;
import com.realestate.dto.PropertyDtos.*;
import com.realestate.dto.WorkflowDtos.*;
import com.realestate.entity.*;
import java.util.*;

public final class ApiMapper {
    private ApiMapper() { }

    public static UserResponse user(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole(),
            user.getProfileImage(), user.getAddress(), user.getCity(), user.getState(), user.isActive());
    }

    public static PropertyResponse property(Property property) {
        OwnerResponse owner = new OwnerResponse(property.getOwner().getId(), property.getOwner().getFullName(),
            property.getOwner().getEmail(), property.getOwner().getPhone(), property.getOwner().getProfileImage());
        List<ImageResponse> images = property.getImages().stream()
            .map(image -> new ImageResponse(image.getId(), image.getImageUrl())).toList();
        return new PropertyResponse(property.getId(), property.getTitle(), property.getDescription(), property.getPropertyType(),
            property.getListingType(), property.getPrice(), property.getBedrooms(), property.getBathrooms(), property.getArea(),
            property.getAddress(), property.getCity(), property.getState(), property.getPincode(), property.getLatitude(),
            property.getLongitude(), property.getStatus(), property.getVerificationStatus(), property.getRejectionReason(),
            owner, images, property.getCreatedAt(), property.getUpdatedAt());
    }

    public static EnquiryResponse enquiry(Enquiry enquiry) {
        return new EnquiryResponse(enquiry.getId(), enquiry.getProperty().getId(), enquiry.getProperty().getTitle(),
            enquiry.getUser().getId(), enquiry.getUser().getFullName(), enquiry.getAgent().getId(),
            enquiry.getAgent().getFullName(), enquiry.getMessage(), enquiry.getStatus(), enquiry.getCreatedAt());
    }

    public static BookingResponse booking(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getProperty().getId(), booking.getProperty().getTitle(),
            booking.getUser().getId(), booking.getUser().getFullName(), booking.getAgent().getId(),
            booking.getAgent().getFullName(), booking.getBookingDate(), booking.getVisitDate(), booking.getAmount(),
            booking.getStatus(), booking.getCreatedAt());
    }

    public static PaymentResponse payment(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getBooking().getId(), payment.getAmount(),
            payment.getPaymentMethod(), payment.getTransactionId(), payment.getPaymentStatus(), payment.getPaymentDate());
    }
}

