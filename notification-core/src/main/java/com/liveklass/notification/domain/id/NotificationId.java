package com.liveklass.notification.domain.id;

public record NotificationId(Long id) {

    public static NotificationId of(final Long id) {
        return new NotificationId(id);
    }
}
