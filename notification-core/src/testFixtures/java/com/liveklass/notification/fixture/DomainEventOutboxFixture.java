package com.liveklass.notification.fixture;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.vo.IdempotencyKey;
import com.liveklass.notification.domain.vo.EventRef;

import java.time.LocalDateTime;

public final class DomainEventOutboxFixture {

    private static final LocalDateTime DEFAULT_NOW = LocalDateTime.of(2026, 4, 24, 12, 0);
    private static final Long DEFAULT_REQUESTER_ID = 1L;
    private static final Long DEFAULT_RECIPIENT_ID = 1L;
    private static final EventRef DEFAULT_EMAIL_EVENT =
            new EventRef(Topic.PAYMENT_CONFIRMED, ChannelType.EMAIL, "pay-001");
    private static final EventRef DEFAULT_IN_APP_EVENT =
            new EventRef(Topic.LECTURE_ENROLLMENT_COMPLETED, ChannelType.IN_APP, "enroll-100");

    private DomainEventOutboxFixture() {}

    public static IdempotencyKey defaultIdempotencyKey() {
        return IdempotencyKey.of(Topic.PAYMENT_CONFIRMED, DEFAULT_RECIPIENT_ID, ChannelType.EMAIL, "pay-001");
    }

    public static DomainEventOutbox pending() {
        return DomainEventOutbox.create(
                IdempotencyKey.of(Topic.PAYMENT_CONFIRMED, DEFAULT_RECIPIENT_ID, ChannelType.EMAIL, "pay-001"),
                DEFAULT_REQUESTER_ID,
                DEFAULT_RECIPIENT_ID,
                DEFAULT_EMAIL_EVENT,
                "{\"subject\":\"결제 완료\",\"body\":\"49,000원\",\"metadata\":{\"recipientEmail\":\"user@example.com\"}}",
                DEFAULT_NOW,
                3
        );
    }

    public static DomainEventOutbox pendingInApp() {
        return DomainEventOutbox.create(
                IdempotencyKey.of(Topic.LECTURE_ENROLLMENT_COMPLETED, DEFAULT_RECIPIENT_ID, ChannelType.IN_APP, "enroll-100"),
                DEFAULT_REQUESTER_ID,
                DEFAULT_RECIPIENT_ID,
                DEFAULT_IN_APP_EVENT,
                "{\"title\":\"수강 신청 완료\",\"body\":\"Spring Boot 수강 신청이 완료되었습니다.\",\"metadata\":{}}",
                DEFAULT_NOW,
                3
        );
    }

    public static DomainEventOutbox pendingInAppRequest() {
        final EventRef eventRef = new EventRef(Topic.IN_APP_NOTIFICATION_REQUEST, ChannelType.IN_APP, "100");
        return DomainEventOutbox.create(
                IdempotencyKey.of(Topic.IN_APP_NOTIFICATION_REQUEST, DEFAULT_RECIPIENT_ID, ChannelType.IN_APP, "100"),
                DEFAULT_REQUESTER_ID,
                DEFAULT_RECIPIENT_ID,
                eventRef,
                "{\"title\":\"수강 신청 완료\",\"body\":\"Spring Boot 수강 신청이 완료되었습니다.\",\"metadata\":{}}",
                DEFAULT_NOW,
                3
        );
    }

    public static DomainEventOutbox pendingWithMaxAttempts(final int maxAttempts, final String referenceId) {
        final EventRef eventRef = new EventRef(Topic.PAYMENT_CONFIRMED, ChannelType.EMAIL, referenceId);
        return DomainEventOutbox.create(
                IdempotencyKey.of(Topic.PAYMENT_CONFIRMED, DEFAULT_RECIPIENT_ID, ChannelType.EMAIL, referenceId),
                DEFAULT_REQUESTER_ID,
                DEFAULT_RECIPIENT_ID,
                eventRef,
                "{\"subject\":\"결제 완료\",\"body\":\"본문\",\"metadata\":{\"recipientEmail\":\"user@example.com\"}}",
                DEFAULT_NOW,
                maxAttempts
        );
    }

    public static DomainEventOutbox processing() {
        return pending().claim(DEFAULT_NOW);
    }

    public static DomainEventOutbox stuckProcessing() {
        return pending().claim(DEFAULT_NOW.minusMinutes(10));
    }

    public static DomainEventOutbox sent() {
        return processing().complete();
    }

    public static DomainEventOutbox deadLetter(final String referenceId) {
        final DomainEventOutbox outbox = pendingWithMaxAttempts(1, referenceId);
        return outbox.claim(DEFAULT_NOW).fail("fatal error", DEFAULT_NOW.plusMinutes(1));
    }
}
