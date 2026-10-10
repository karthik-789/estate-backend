package com.realestate.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Long> userDashboard(Long userId);
    Map<String, Long> agentDashboard(Long agentId);
}

