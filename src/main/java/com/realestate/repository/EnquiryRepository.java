package com.realestate.repository;

import com.realestate.entity.Enquiry;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    Page<Enquiry> findByUserId(Long userId, Pageable pageable);
    Page<Enquiry> findByAgentId(Long agentId, Pageable pageable);
    long countByUserId(Long userId);
    long countByAgentId(Long agentId);
}

