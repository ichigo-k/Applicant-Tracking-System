package com.kephas.applicanttrackingsystem.services;

import java.util.UUID;

public interface NotificationService {
    void sendNotificationAsync(UUID applicationId, String email, String stage);
}
