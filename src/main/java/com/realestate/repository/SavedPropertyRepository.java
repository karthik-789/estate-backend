package com.realestate.repository;

import com.realestate.entity.SavedProperty;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SavedPropertyRepository extends JpaRepository<SavedProperty, Long> {
    Page<SavedProperty> findByUserId(Long userId, Pageable pageable);
    Optional<SavedProperty> findByUserIdAndPropertyId(Long userId, Long propertyId);
}

