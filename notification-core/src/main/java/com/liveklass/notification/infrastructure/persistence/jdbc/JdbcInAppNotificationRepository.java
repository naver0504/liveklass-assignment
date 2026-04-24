package com.liveklass.notification.infrastructure.persistence.jdbc;

import com.liveklass.notification.domain.InAppNotification;

import java.util.List;

public interface JdbcInAppNotificationRepository {

    void upsert(InAppNotification notification);

    void batchInsert(List<InAppNotification> notifications);
}
