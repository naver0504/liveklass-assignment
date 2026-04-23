package com.liveklass.notification.fixture;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.DedupKey;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.RetryState;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.domain.vo.OutboxId;
import com.liveklass.notification.domain.vo.ProcessingLock;
import com.liveklass.notification.domain.vo.SentResult;

import java.time.LocalDateTime;

public final class DomainEventOutboxFixture {

    private static final LocalDateTime DEFAULT_NOW = LocalDateTime.of(2026, 4, 24, 12, 0);
    private static final EventRef DEFAULT_EMAIL_EVENT =
            new EventRef(Topic.PAYMENT_CONFIRMED, ChannelType.EMAIL, "pay-001");
    private static final EventRef DEFAULT_IN_APP_EVENT =
            new EventRef(Topic.LECTURE_ENROLLMENT_COMPLETED, ChannelType.IN_APP, "enroll-100");

    private DomainEventOutboxFixture() {}

    public static DomainEventOutbox pending() {
        return DomainEventOutbox.create(
                DedupKey.of(Topic.PAYMENT_CONFIRMED, 1L, ChannelType.EMAIL, "pay-001"),
                1L,
                DEFAULT_EMAIL_EVENT,
                "{\"subject\":\"결제 완료\",\"body\":\"49,000원\",\"metadata\":{\"recipientEmail\":\"user@example.com\"}}",
                DEFAULT_NOW,
                null,
                3
        );
    }

    public static DomainEventOutbox pendingInApp() {
        return DomainEventOutbox.create(
                DedupKey.of(Topic.LECTURE_ENROLLMENT_COMPLETED, 1L, ChannelType.IN_APP, "enroll-100"),
                1L,
                DEFAULT_IN_APP_EVENT,
                "{\"title\":\"수강 신청 완료\",\"body\":\"Spring Boot 수강 신청이 완료되었습니다.\",\"metadata\":{}}",
                DEFAULT_NOW,
                null,
                3
        );
    }

    public static DomainEventOutbox pendingWithMaxAttempts(final int maxAttempts, final String referenceId) {
        final EventRef eventRef = new EventRef(Topic.PAYMENT_CONFIRMED, ChannelType.EMAIL, referenceId);
        return DomainEventOutbox.create(
                DedupKey.of(Topic.PAYMENT_CONFIRMED, 1L, ChannelType.EMAIL, referenceId),
                1L,
                eventRef,
                "{\"subject\":\"결제 완료\",\"body\":\"본문\",\"metadata\":{\"recipientEmail\":\"user@example.com\"}}",
                DEFAULT_NOW,
                null,
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
        return processing().complete("msg-abc123", DEFAULT_NOW.plusSeconds(1));
    }

    public static DomainEventOutbox deadLetter(final String referenceId) {
        final DomainEventOutbox outbox = pendingWithMaxAttempts(1, referenceId);
        return outbox.claim(DEFAULT_NOW).fail("fatal error", DEFAULT_NOW.plusMinutes(1));
    }
}
