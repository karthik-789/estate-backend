package com.realestate.service;

import com.realestate.entity.User;

public interface NotificationService {
    void create(User user, String title, String message, String type);
}

