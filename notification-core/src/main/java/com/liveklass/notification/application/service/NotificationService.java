package com.liveklass.notification.application.service;

import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.event.EmailNotificationEvent;
import com.liveklass.notification.domain.event.InAppNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DomainEventPublisher eventPublisher;

    public void requestInAppNotification(
            final Long recipientId,
            final Long referenceId,
            final String title,
            final String body,
            final LocalDateTime publishedAt
    ) {
        eventPublisher.publish(
                new InAppNotificationEvent(Topic.IN_APP_NOTIFICATION_REQUEST, recipientId, String.valueOf(referenceId), title, body, publishedAt)
        );
    }

    public void requestEmailNotification(
            final Long recipientId,
            final Long referenceId,
            final String subject,
            final String body,
            final String recipientEmail,
            final LocalDateTime publishedAt
    ) {
        eventPublisher.publish(
                new EmailNotificationEvent(Topic.EMAIL_NOTIFICATION_REQUEST, recipientId, String.valueOf(referenceId), subject, body, recipientEmail, publishedAt)
        );
    }
}
