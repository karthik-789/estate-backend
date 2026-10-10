package com.realestate.service;

import com.realestate.dto.AuthDtos.*;
import com.realestate.entity.User;
import org.springframework.data.domain.*;

public interface UserService {
    UserResponse profile(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
    Page<UserResponse> list(Pageable pageable);
    void setActive(Long id, boolean active);
    User requireUser(Long id);
}

