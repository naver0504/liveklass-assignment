package com.liveklass.notification.application.service;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.notification.application.repository.InAppNotificationRepository;
import com.liveklass.notification.domain.InAppNotification;
import com.liveklass.notification.domain.exception.NotificationException;
import com.liveklass.notification.domain.id.NotificationId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InAppNotificationService {

    private final InAppNotificationRepository notificationRepository;

    @Transactional
    public void createInAppNotification(
            final Long outboxId,
            final Long recipientId,
            final String title,
            final String body,
            final LocalDateTime publishedAt,
            final LocalDateTime createdAt
    ) {
        notificationRepository.save(
                InAppNotification.create(outboxId, recipientId, title, body, publishedAt, createdAt)
        );
    }

    @Transactional
    public void createInAppNotificationsBatch(final List<InAppNotification> notifications) {
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void markAsRead(final Long notificationId, final Long userId) {
        final InAppNotification notification = notificationRepository.findById(new NotificationId(notificationId))
                .orElseThrow(() -> ExceptionCreator.create(NotificationException.NOTIFICATION_NOT_FOUND,
                        "notificationId: " + notificationId));

        notification.validateRecipient(userId);

        notificationRepository.save(notification.markRead());
    }

    @Transactional(readOnly = true)
    public InAppNotification findById(final Long notificationId, final Long recipientId) {
        final InAppNotification notification = notificationRepository.findById(new NotificationId(notificationId))
                .orElseThrow(() -> ExceptionCreator.create(NotificationException.NOTIFICATION_NOT_FOUND,
                        "notificationId: " + notificationId));
        notification.validateRecipient(recipientId);
        return notification;
    }

    @Transactional(readOnly = true)
    public List<InAppNotification> findAllByRecipientId(final Long recipientId) {
        return notificationRepository.findAllByRecipientId(recipientId);
    }

    @Transactional(readOnly = true)
    public List<InAppNotification> findAllByRecipientId(final Long recipientId, final boolean isRead) {
        return notificationRepository.findAllByRecipientIdAndIsRead(recipientId, isRead);
    }

    @Transactional(readOnly = true)
    public boolean existsByOutboxId(final Long outboxId) {
        return notificationRepository.existsByOutboxId(outboxId);
    }
}
