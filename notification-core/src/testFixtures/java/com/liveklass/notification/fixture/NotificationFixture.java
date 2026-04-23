package com.liveklass.notification.fixture;

import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.Notification;

import java.time.LocalDateTime;

public final class NotificationFixture {

    private static final LocalDateTime DEFAULT_NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    private NotificationFixture() {}

    public static Notification unread() {
        return Notification.create(
                10L,
                1L,
                Topic.LECTURE_ENROLLMENT_COMPLETED,
                "{\"title\":\"수강 신청 완료\",\"body\":\"Spring Boot 수강 신청이 완료되었습니다.\",\"metadata\":{}}",
                DEFAULT_NOW
        );
    }

    public static Notification unread(final Long outboxId, final Long recipientId) {
        return Notification.create(
                outboxId,
                recipientId,
                Topic.LECTURE_ENROLLMENT_COMPLETED,
                "{\"title\":\"수강 신청 완료\",\"body\":\"본문\",\"metadata\":{}}",
                DEFAULT_NOW
        );
    }

    public static Notification read() {
        final Notification notification = unread();
        notification.markRead();
        return notification;
    }
}
