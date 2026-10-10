package com.realestate.service;

import com.realestate.dto.*;
import com.realestate.dto.AuthDtos.UserResponse;
import com.realestate.dto.PropertyDtos.PropertyResponse;
import com.realestate.dto.WorkflowDtos.*;
import org.springframework.data.domain.*;

public interface AdminService {
    DashboardResponse dashboard();
    Page<PropertyResponse> pendingProperties(Pageable pageable);
    PropertyResponse approveProperty(Long id);
    PropertyResponse rejectProperty(Long id, RejectPropertyRequest request);
    Page<UserResponse> agents(Pageable pageable);
}

