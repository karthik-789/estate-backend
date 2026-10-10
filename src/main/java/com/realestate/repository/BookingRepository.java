package com.realestate.repository;

import com.realestate.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUserId(Long userId, Pageable pageable);
    Page<Booking> findByAgentId(Long agentId, Pageable pageable);
    long countByUserId(Long userId);
    long countByAgentId(Long agentId);
    long countByStatus(BookingStatus status);
}

