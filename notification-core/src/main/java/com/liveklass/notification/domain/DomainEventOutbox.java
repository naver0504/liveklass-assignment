package com.liveklass.notification.domain;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.exception.OutboxException;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.domain.vo.IdempotencyKey;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.domain.vo.ProcessingLock;
import com.liveklass.notification.domain.vo.RetryState;
import com.liveklass.notification.domain.vo.SentResult;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder(toBuilder = true)
public record DomainEventOutbox(
        OutboxId id,
        String idempotencyKey,
        Long recipientId,
        EventRef eventRef,
        String payload,
        ProcessingLock lock,
        RetryState retryState,
        LocalDateTime scheduledAt,
        SentResult sentResult
) {
    public DomainEventOutbox {
        Objects.requireNonNull(idempotencyKey,   "idempotencyKey must not be null");
        Objects.requireNonNull(recipientId,"recipientId must not be null");
        Objects.requireNonNull(eventRef,   "eventRef must not be null");
        Objects.requireNonNull(payload,    "payload must not be null");
        Objects.requireNonNull(lock,       "lock must not be null");
        Objects.requireNonNull(retryState, "retryState must not be null");

        if (lock.status() == OutboxStatus.SENT && sentResult == null) {
            throw ExceptionCreator.create(OutboxException.INVALID_OUTBOX,
                    "sentResult must not be null when status is SENT");
        }
        if (lock.status() != OutboxStatus.SENT && sentResult != null) {
            throw ExceptionCreator.create(OutboxException.INVALID_OUTBOX,
                    "sentResult must be null when status is " + lock.status());
        }
    }

    public static DomainEventOutbox create(
            final IdempotencyKey idempotencyKey,
            final Long recipientId,
            final EventRef eventRef,
            final String payload,
            final LocalDateTime nextAttemptAt,
            final LocalDateTime scheduledAt,
            final int maxAttempts
    ) {
        return DomainEventOutbox.builder()
                .id(new OutboxId(null))
                .idempotencyKey(idempotencyKey.value())
                .recipientId(recipientId)
                .eventRef(eventRef)
                .payload(payload)
                .lock(ProcessingLock.pending())
                .retryState(RetryState.initial(maxAttempts, nextAttemptAt))
                .scheduledAt(scheduledAt)
                .sentResult(null)
                .build();
    }

    /** PENDING → PROCESSING */
    public DomainEventOutbox claim(final LocalDateTime lockedAt) {
        lock.status().validateTransitionTo(OutboxStatus.PROCESSING);
        return toBuilder()
                .lock(ProcessingLock.processing(Objects.requireNonNull(lockedAt)))
                .retryState(retryState.increment(retryState.nextAttemptAt()))
                .build();
    }

    /** PROCESSING → SENT */
    public DomainEventOutbox complete(final String providerMessageId, final LocalDateTime sentAt) {
        lock.status().validateTransitionTo(OutboxStatus.SENT);
        return toBuilder()
                .lock(ProcessingLock.sent())
                .sentResult(SentResult.of(Objects.requireNonNull(sentAt), providerMessageId))
                .build();
    }

    /** PROCESSING → PENDING (retry) or DEAD_LETTER */
    public DomainEventOutbox fail(final String error, final LocalDateTime nextAttemptAt) {
        if (!retryState.isRetryable()) {
            return markDeadLetter(error);
        }
        lock.status().validateTransitionTo(OutboxStatus.PENDING);
        return toBuilder()
                .lock(ProcessingLock.pending())
                .retryState(retryState.withFailure(error, Objects.requireNonNull(nextAttemptAt)))
                .build();
    }

    /** PROCESSING → DEAD_LETTER */
    public DomainEventOutbox markDeadLetter(final String error) {
        lock.status().validateTransitionTo(OutboxStatus.DEAD_LETTER);
        return toBuilder()
                .lock(ProcessingLock.deadLetter())
                .retryState(retryState.withFailure(error, retryState.nextAttemptAt()))
                .build();
    }

    /** DEAD_LETTER → PENDING (수동 재시도, attempt_count 초기화) */
    public DomainEventOutbox recover(final LocalDateTime nextAttemptAt) {
        lock.status().validateTransitionTo(OutboxStatus.PENDING);
        return toBuilder()
                .lock(ProcessingLock.pending())
                .retryState(retryState.reset(Objects.requireNonNull(nextAttemptAt)))
                .build();
    }

    /** stuck recovery: PROCESSING → SENT 보정 */
    public DomainEventOutbox correctToSent(final LocalDateTime sentAt, final String providerMessageId) {
        lock.status().validateTransitionTo(OutboxStatus.SENT);
        return toBuilder()
                .lock(ProcessingLock.sent())
                .sentResult(SentResult.of(Objects.requireNonNull(sentAt), providerMessageId))
                .build();
    }

    /** stuck recovery: PROCESSING → PENDING 회수 */
    public DomainEventOutbox releaseToRetry(final LocalDateTime nextAttemptAt) {
        lock.status().validateTransitionTo(OutboxStatus.PENDING);
        return toBuilder()
                .lock(ProcessingLock.pending())
                .retryState(retryState.withFailure(retryState.lastError(), Objects.requireNonNull(nextAttemptAt)))
                .build();
    }

    public OutboxStatus status() {
        return lock.status();
    }
}
