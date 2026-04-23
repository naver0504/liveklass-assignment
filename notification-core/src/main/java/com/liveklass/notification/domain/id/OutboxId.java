package com.liveklass.notification.domain.id;

public record OutboxId(Long id) {

    public OutboxId {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("OutboxId must be positive, got: " + id);
        }
    }

    public static OutboxId of(final Long id) {
        return new OutboxId(id);
    }
}
