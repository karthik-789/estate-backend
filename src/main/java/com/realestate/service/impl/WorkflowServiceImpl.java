package com.realestate.service.impl;

import com.realestate.dto.WorkflowDtos.*;
import com.realestate.entity.*;
import com.realestate.exception.*;
import com.realestate.mapper.ApiMapper;
import com.realestate.repository.*;
import com.realestate.service.WorkflowService;
import com.realestate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {
    private final EnquiryRepository enquiryRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override @Transactional
    public EnquiryResponse createEnquiry(Long userId, EnquiryRequest request) {
        Property property = approvedProperty(request.propertyId());
        User user = user(userId);
        Enquiry enquiry = new Enquiry();
        enquiry.setProperty(property);
        enquiry.setUser(user);
        enquiry.setAgent(property.getOwner());
        enquiry.setMessage(request.message().trim());
        notificationService.create(property.getOwner(), "New property enquiry",
            user.getFullName() + " is interested in " + property.getTitle() + ". Open Interested buyers to read their message.", "ENQUIRY");
        return ApiMapper.enquiry(enquiryRepository.save(enquiry));
    }

    @Override @Transactional(readOnly = true)
    public Page<EnquiryResponse> userEnquiries(Long userId, Pageable pageable) {
        return enquiryRepository.findByUserId(userId, pageable).map(ApiMapper::enquiry);
    }

    @Override @Transactional(readOnly = true)
    public Page<EnquiryResponse> agentEnquiries(Long agentId, Pageable pageable) {
        return enquiryRepository.findByAgentId(agentId, pageable).map(ApiMapper::enquiry);
    }

    @Override @Transactional(readOnly = true)
    public Page<EnquiryResponse> allEnquiries(Pageable pageable) {
        return enquiryRepository.findAll(pageable).map(ApiMapper::enquiry);
    }

    @Override @Transactional
    public EnquiryResponse updateEnquiry(Long actorId, Role actorRole, Long id, UpdateEnquiryRequest request) {
        Enquiry enquiry = enquiryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enquiry not found."));
        if (actorRole != Role.ADMIN && !enquiry.getAgent().getId().equals(actorId)) {
            throw new UnauthorizedException("You cannot manage this enquiry.");
        }
        enquiry.setStatus(request.status());
        return ApiMapper.enquiry(enquiry);
    }

    @Override @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        Property property = approvedProperty(request.propertyId());
        if (property.getStatus() != PropertyStatus.AVAILABLE) throw new BadRequestException("Property is not available for booking.");
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setUser(user(userId));
        booking.setAgent(property.getOwner());
        booking.setVisitDate(request.visitDate());
        booking.setAmount(request.amount());
        notificationService.create(property.getOwner(), "New booking request",
            "A visit was requested for " + property.getTitle() + ".", "BOOKING");
        return ApiMapper.booking(bookingRepository.save(booking));
    }

    @Override @Transactional(readOnly = true)
    public Page<BookingResponse> userBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(ApiMapper::booking);
    }

    @Override @Transactional(readOnly = true)
    public Page<BookingResponse> agentBookings(Long agentId, Pageable pageable) {
        return bookingRepository.findByAgentId(agentId, pageable).map(ApiMapper::booking);
    }

    @Override @Transactional(readOnly = true)
    public Page<BookingResponse> allBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(ApiMapper::booking);
    }

    @Override @Transactional
    public BookingResponse updateBooking(Long actorId, Role role, Long id, UpdateBookingStatusRequest request) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        if (role != Role.ADMIN && !booking.getAgent().getId().equals(actorId)) {
            throw new UnauthorizedException("You cannot manage this booking.");
        }
        booking.setStatus(request.status());
        if (request.status() == BookingStatus.CONFIRMED) booking.getProperty().setStatus(PropertyStatus.BOOKED);
        if (request.status() == BookingStatus.CANCELLED && booking.getProperty().getStatus() == PropertyStatus.BOOKED) {
            booking.getProperty().setStatus(PropertyStatus.AVAILABLE);
        }
        notificationService.create(booking.getUser(), "Booking update",
            "Your booking for " + booking.getProperty().getTitle() + " is now " + request.status() + ".", "BOOKING");
        return ApiMapper.booking(booking);
    }

    @Override @Transactional
    public PaymentResponse mockPay(Long userId, MockPaymentRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        if (!booking.getUser().getId().equals(userId)) throw new UnauthorizedException("You cannot pay for this booking.");
        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) throw new BadRequestException("This booking already has a payment.");
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Payment is not allowed for this booking.");
        }
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(booking.getUser());
        payment.setAmount(booking.getAmount());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setTransactionId("DEMO-" + UUID.randomUUID());
        payment.setPaymentStatus(Boolean.TRUE.equals(request.simulateSuccess()) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.getProperty().setStatus(PropertyStatus.BOOKED);
        }
        notificationService.create(booking.getUser(), "Mock payment processed",
            "Your DEMO payment for " + booking.getProperty().getTitle() + " is " + payment.getPaymentStatus() + ".", "PAYMENT");
        return ApiMapper.payment(paymentRepository.save(payment));
    }

    @Override @Transactional(readOnly = true)
    public Page<PaymentResponse> userPayments(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable).map(ApiMapper::payment);
    }

    @Override @Transactional(readOnly = true)
    public Page<PaymentResponse> allPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(ApiMapper::payment);
    }

    private User user(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private Property approvedProperty(Long id) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found."));
        if (property.getVerificationStatus() != VerificationStatus.APPROVED || property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new BadRequestException("Property is not available.");
        }
        return property;
    }
}

