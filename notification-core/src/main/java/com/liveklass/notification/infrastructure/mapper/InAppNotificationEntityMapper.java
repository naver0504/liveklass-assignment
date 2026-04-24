package com.liveklass.notification.infrastructure.mapper;

import com.liveklass.notification.domain.InAppNotification;
import com.liveklass.notification.domain.id.NotificationId;
import com.liveklass.notification.infrastructure.persistence.entity.InAppNotificationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationEntityMapper {

    public InAppNotification toDomain(final InAppNotificationJpaEntity entity) {
        return new InAppNotification(
                new NotificationId(entity.getId()),
                entity.getOutboxId(),
                entity.getRecipientId(),
                entity.getTitle(),
                entity.getBody(),
                entity.isRead(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }
}
