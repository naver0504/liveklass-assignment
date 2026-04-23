package com.liveklass.notification.domain;

import com.liveklass.notification.domain.id.NotificationId;

import java.time.LocalDateTime;
import java.util.Objects;

public record InAppNotification(
        NotificationId id,
        Long outboxId,
        Long recipientId,
        String title,
        String body,
        boolean isRead,
        LocalDateTime publishedAt,
        LocalDateTime createdAt
) {
    public InAppNotification {
        Objects.requireNonNull(outboxId,    "outboxId must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(title,       "title must not be null");
        Objects.requireNonNull(body,        "body must not be null");
        Objects.requireNonNull(publishedAt, "publishedAt must not be null");
        Objects.requireNonNull(createdAt,   "createdAt must not be null");
    }

    public static InAppNotification create(
            final Long outboxId,
            final Long recipientId,
            final String title,
            final String body,
            final LocalDateTime publishedAt,
            final LocalDateTime createdAt
    ) {
        return new InAppNotification(new NotificationId(null), outboxId, recipientId, title, body, false, publishedAt, createdAt);
    }

    public InAppNotification markRead() {
        return new InAppNotification(id, outboxId, recipientId, title, body, true, publishedAt, createdAt);
    }
}
