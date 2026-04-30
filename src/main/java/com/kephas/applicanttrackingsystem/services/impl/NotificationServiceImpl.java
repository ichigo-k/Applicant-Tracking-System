package com.kephas.applicanttrackingsystem.services.impl;

import com.kephas.applicanttrackingsystem.entities.Notification;
import com.kephas.applicanttrackingsystem.repositories.NotificationRepository;
import com.kephas.applicanttrackingsystem.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Async
    public void sendNotificationAsync(UUID applicationId, String email,String stage ) {
        try {
            log.info("Sending notification to {}", email);


            Thread.sleep(3000);

            Notification notification = Notification.builder()
                    .applicationId(applicationId)
                    .email(email)
                    .type(stage)
                    .status("SENT")
                    .sentAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);

            log.info("Notification sent successfully to {}", email);

        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }
}