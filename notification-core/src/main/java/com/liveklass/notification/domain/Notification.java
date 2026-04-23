package com.liveklass.notification.domain;

import com.liveklass.common.event.Topic;

import java.time.LocalDateTime;
import java.util.Objects;

public record Notification(
        Long id,
        Long outboxId,
        Long recipientId,
        Topic topic,
        String payload,
        boolean isRead,
        LocalDateTime createdAt
) {
    public Notification {
        Objects.requireNonNull(outboxId,    "outboxId must not be null");
        Objects.requireNonNull(recipientId, "recipientId must not be null");
        Objects.requireNonNull(topic,       "topic must not be null");
        Objects.requireNonNull(payload,     "payload must not be null");
        Objects.requireNonNull(createdAt,   "createdAt must not be null");
    }

    public static Notification create(
            final Long outboxId,
            final Long recipientId,
            final Topic topic,
            final String payload,
            final LocalDateTime createdAt
    ) {
        return new Notification(null, outboxId, recipientId, topic, payload, false, createdAt);
    }

    public Notification markRead() {
        return new Notification(id, outboxId, recipientId, topic, payload, true, createdAt);
    }
}
