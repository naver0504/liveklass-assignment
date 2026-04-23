package com.liveklass.notification.domain.vo;

public record OutboxId(Long id) {

    public OutboxId {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("OutboxId must be positive: " + id);
        }
    }

    public static OutboxId of(final Long id) {
        return new OutboxId(id);
    }

    @Override
    public String toString() {
        return id == null ? "null" : String.valueOf(id);
    }
}
