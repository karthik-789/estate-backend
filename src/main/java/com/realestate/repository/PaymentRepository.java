package com.realestate.repository;

import com.realestate.entity.Payment;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    long countByUserId(Long userId);
    Optional<Payment> findByBookingId(Long bookingId);
}

