package com.realestate.repository;

import com.realestate.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    Page<Property> findByOwnerId(Long ownerId, Pageable pageable);
    long countByVerificationStatus(VerificationStatus status);
}

