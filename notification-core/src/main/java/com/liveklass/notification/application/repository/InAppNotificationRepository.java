package com.liveklass.notification.application.repository;

import com.liveklass.notification.domain.InAppNotification;
import com.liveklass.notification.domain.id.NotificationId;

import java.util.List;
import java.util.Optional;

public interface InAppNotificationRepository {

    InAppNotification save(InAppNotification notification);

    Optional<InAppNotification> findById(NotificationId id);

    List<InAppNotification> findAllByRecipientId(Long recipientId);

    List<InAppNotification> findAllByRecipientIdAndIsRead(Long recipientId, boolean isRead);
}
