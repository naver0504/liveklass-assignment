package com.liveklass.notification.domain.vo;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.notification.domain.exception.OutboxException;

import java.time.LocalDateTime;
import java.util.Objects;

public record RetryState(
        int attemptCount,
        int maxAttempts,
        LocalDateTime nextAttemptAt,
        String lastError
) {
    public RetryState {
        if (attemptCount < 0) {
            throw ExceptionCreator.create(OutboxException.INVALID_RETRY_STATE,
                    "attemptCount must be >= 0, got: " + attemptCount);
        }
        if (maxAttempts < 1) {
            throw ExceptionCreator.create(OutboxException.INVALID_RETRY_STATE,
                    "maxAttempts must be >= 1, got: " + maxAttempts);
        }
        Objects.requireNonNull(nextAttemptAt, "nextAttemptAt must not be null");
    }

    public static RetryState initial(final int maxAttempts, final LocalDateTime nextAttemptAt) {
        return new RetryState(0, maxAttempts, nextAttemptAt, null);
    }

    public boolean isRetryable() {
        return attemptCount < maxAttempts;
    }

    public RetryState increment(final LocalDateTime nextAttemptAt) {
        return new RetryState(attemptCount + 1, maxAttempts,
                Objects.requireNonNull(nextAttemptAt), lastError);
    }

    public RetryState recordSuccess() {
        return new RetryState(attemptCount + 1, maxAttempts, nextAttemptAt, null);
    }

    public RetryState recordFailure(final String error, final LocalDateTime nextAttemptAt) {
        return new RetryState(attemptCount + 1, maxAttempts,
                Objects.requireNonNull(nextAttemptAt), error);
    }

    public RetryState withFailure(final String error, final LocalDateTime nextAttemptAt) {
        return new RetryState(attemptCount, maxAttempts,
                Objects.requireNonNull(nextAttemptAt), error);
    }

    public RetryState reset(final LocalDateTime nextAttemptAt) {
        return new RetryState(0, maxAttempts,
                Objects.requireNonNull(nextAttemptAt), null);
    }
}
