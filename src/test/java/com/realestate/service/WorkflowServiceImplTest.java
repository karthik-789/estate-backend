package com.realestate.service;

import com.realestate.dto.WorkflowDtos.*;
import com.realestate.entity.*;
import com.realestate.repository.*;
import com.realestate.service.impl.WorkflowServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceImplTest {
    @Mock EnquiryRepository enquiries;
    @Mock BookingRepository bookings;
    @Mock PaymentRepository payments;
    @Mock PropertyRepository properties;
    @Mock UserRepository users;
    @Mock NotificationService notifications;
    @InjectMocks WorkflowServiceImpl service;

    @Test
    void createsBookingForApprovedAvailableProperty() {
        User seeker = new User(); seeker.setId(1L); seeker.setFullName("Seeker");
        User agent = new User(); agent.setId(2L); agent.setFullName("Agent");
        Property property = property(7L, agent);
        when(properties.findById(7L)).thenReturn(java.util.Optional.of(property));
        when(users.findById(1L)).thenReturn(java.util.Optional.of(seeker));
        when(bookings.save(any(Booking.class))).thenAnswer(call -> {
            Booking booking = call.getArgument(0); booking.setId(9L); return booking;
        });

        BookingResponse response = service.createBooking(1L,
            new BookingRequest(7L, LocalDate.now().plusDays(2), BigDecimal.valueOf(10000)));

        assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
        verify(notifications).create(eq(agent), contains("booking"), anyString(), eq("BOOKING"));
    }

    @Test
    void successfulMockPaymentConfirmsBooking() {
        User seeker = new User(); seeker.setId(1L); seeker.setFullName("Seeker");
        Property property = property(7L, seeker);
        Booking booking = new Booking(); booking.setId(9L); booking.setUser(seeker); booking.setProperty(property);
        booking.setAgent(seeker); booking.setAmount(BigDecimal.valueOf(1000)); booking.setStatus(BookingStatus.PENDING);
        when(bookings.findById(9L)).thenReturn(java.util.Optional.of(booking));
        when(payments.findByBookingId(9L)).thenReturn(java.util.Optional.empty());
        when(payments.save(any(Payment.class))).thenAnswer(call -> {
            Payment payment = call.getArgument(0); payment.setId(3L); return payment;
        });

        PaymentResponse response = service.mockPay(1L, new MockPaymentRequest(9L, PaymentMethod.UPI, true));

        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(property.getStatus()).isEqualTo(PropertyStatus.BOOKED);
    }

    private Property property(Long id, User owner) {
        Property property = new Property(); property.setId(id); property.setTitle("Test property");
        property.setOwner(owner); property.setVerificationStatus(VerificationStatus.APPROVED); property.setStatus(PropertyStatus.AVAILABLE);
        return property;
    }
}

