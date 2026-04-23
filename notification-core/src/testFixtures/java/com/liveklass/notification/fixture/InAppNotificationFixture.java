package com.liveklass.notification.fixture;

import com.liveklass.notification.domain.InAppNotification;

import java.time.LocalDateTime;

public final class InAppNotificationFixture {

    private static final LocalDateTime DEFAULT_NOW = LocalDateTime.of(2026, 4, 24, 12, 0);

    private InAppNotificationFixture() {}

    public static InAppNotification unread() {
        return InAppNotification.create(
                10L,
                1L,
                "수강 신청 완료",
                "Spring Boot 수강 신청이 완료되었습니다.",
                DEFAULT_NOW,
                DEFAULT_NOW
        );
    }

    public static InAppNotification unread(final Long outboxId, final Long recipientId) {
        return InAppNotification.create(
                outboxId,
                recipientId,
                "수강 신청 완료",
                "Spring Boot 수강 신청이 완료되었습니다.",
                DEFAULT_NOW,
                DEFAULT_NOW
        );
    }

    public static InAppNotification read() {
        return unread().markRead();
    }
}
