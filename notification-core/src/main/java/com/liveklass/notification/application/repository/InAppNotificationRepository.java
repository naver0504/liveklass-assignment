package com.liveklass.notification.application.repository;

import com.liveklass.notification.domain.InAppNotification;
import com.liveklass.notification.domain.id.NotificationId;

import java.util.List;
import java.util.Optional;

public interface InAppNotificationRepository {

    void save(InAppNotification notification);

    void saveAll(List<InAppNotification> notifications);

    Optional<InAppNotification> findById(NotificationId id);

    boolean existsByOutboxId(Long outboxId);

    List<InAppNotification> findAllByRecipientId(Long recipientId);

    List<InAppNotification> findAllByRecipientIdAndIsRead(Long recipientId, boolean isRead);
}
