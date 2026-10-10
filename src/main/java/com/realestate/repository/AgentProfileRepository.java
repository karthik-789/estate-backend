package com.realestate.repository;

import com.realestate.entity.AgentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, Long> {
    Optional<AgentProfile> findByUserId(Long userId);
}

