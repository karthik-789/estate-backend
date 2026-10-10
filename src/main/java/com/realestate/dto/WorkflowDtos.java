package com.realestate.dto;

import com.realestate.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;

public final class WorkflowDtos {
    private WorkflowDtos() { }

    public record EnquiryRequest(@NotNull Long propertyId, @NotBlank @Size(max = 2000) String message) { }
    public record UpdateEnquiryRequest(@NotNull EnquiryStatus status) { }
    public record EnquiryResponse(Long id, Long propertyId, String propertyTitle, Long userId, String userName,
        Long agentId, String agentName, String message, EnquiryStatus status, LocalDateTime createdAt) { }

    public record BookingRequest(@NotNull Long propertyId, @NotNull @FutureOrPresent LocalDate visitDate,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount) { }
    public record UpdateBookingStatusRequest(@NotNull BookingStatus status) { }
    public record BookingResponse(Long id, Long propertyId, String propertyTitle, Long userId, String userName,
        Long agentId, String agentName, LocalDate bookingDate, LocalDate visitDate, BigDecimal amount,
        BookingStatus status, LocalDateTime createdAt) { }

    public record MockPaymentRequest(@NotNull Long bookingId, @NotNull PaymentMethod paymentMethod,
        @NotNull Boolean simulateSuccess) { }
    public record PaymentResponse(Long id, Long bookingId, BigDecimal amount, PaymentMethod paymentMethod,
        String transactionId, PaymentStatus paymentStatus, LocalDateTime paymentDate) { }

    public record RejectPropertyRequest(@NotBlank @Size(max = 1000) String reason) { }
    public record DashboardResponse(long users, long agents, long properties, long pendingProperties,
        long approvedProperties, long bookings, long enquiries, long payments) { }
}

