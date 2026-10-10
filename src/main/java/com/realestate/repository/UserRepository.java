package com.realestate.repository;

import com.realestate.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Page<User> findByRole(Role role, Pageable pageable);
    long countByRole(Role role);
}

