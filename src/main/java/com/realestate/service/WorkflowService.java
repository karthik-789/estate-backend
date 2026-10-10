package com.realestate.service;

import com.realestate.dto.WorkflowDtos.*;
import com.realestate.entity.Role;
import org.springframework.data.domain.*;

public interface WorkflowService {
    EnquiryResponse createEnquiry(Long userId, EnquiryRequest request);
    Page<EnquiryResponse> userEnquiries(Long userId, Pageable pageable);
    Page<EnquiryResponse> agentEnquiries(Long agentId, Pageable pageable);
    Page<EnquiryResponse> allEnquiries(Pageable pageable);
    EnquiryResponse updateEnquiry(Long actorId, Role actorRole, Long id, UpdateEnquiryRequest request);
    BookingResponse createBooking(Long userId, BookingRequest request);
    Page<BookingResponse> userBookings(Long userId, Pageable pageable);
    Page<BookingResponse> agentBookings(Long agentId, Pageable pageable);
    Page<BookingResponse> allBookings(Pageable pageable);
    BookingResponse updateBooking(Long actorId, Role role, Long id, UpdateBookingStatusRequest request);
    PaymentResponse mockPay(Long userId, MockPaymentRequest request);
    Page<PaymentResponse> userPayments(Long userId, Pageable pageable);
    Page<PaymentResponse> allPayments(Pageable pageable);
}

