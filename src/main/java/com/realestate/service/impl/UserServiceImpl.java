package com.realestate.service.impl;

import com.realestate.dto.AuthDtos.*;
import com.realestate.entity.User;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.mapper.ApiMapper;
import com.realestate.repository.UserRepository;
import com.realestate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override @Transactional(readOnly = true)
    public UserResponse profile(Long userId) { return ApiMapper.user(requireUser(userId)); }

    @Override @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = requireUser(userId);
        user.setFullName(request.fullName().trim());
        user.setPhone(request.phone());
        user.setAddress(request.address());
        user.setCity(request.city());
        user.setState(request.state());
        user.setProfileImage(request.profileImage());
        return ApiMapper.user(user);
    }

    @Override @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(ApiMapper::user);
    }

    @Override @Transactional
    public void setActive(Long id, boolean active) {
        User user = requireUser(id);
        user.setActive(active);
    }

    @Override @Transactional(readOnly = true)
    public User requireUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}

