package com.liveklass.notification.domain.vo;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.exception.OutboxException;

import java.time.LocalDateTime;

public record ProcessingLock(
        OutboxStatus status,
        LocalDateTime lockedAt
) {
    public ProcessingLock {
        if (status == null) throw new NullPointerException("status must not be null");
        if (status == OutboxStatus.PROCESSING && lockedAt == null) {
            throw ExceptionCreator.create(OutboxException.INVALID_PROCESSING_LOCK,
                    "lockedAt must not be null when status is PROCESSING");
        }
        if (status != OutboxStatus.PROCESSING && lockedAt != null) {
            throw ExceptionCreator.create(OutboxException.INVALID_PROCESSING_LOCK,
                    "lockedAt must be null when status is " + status);
        }
    }

    public static ProcessingLock pending()                             { return new ProcessingLock(OutboxStatus.PENDING, null); }
    public static ProcessingLock processing(final LocalDateTime lockedAt) { return new ProcessingLock(OutboxStatus.PROCESSING, lockedAt); }
    public static ProcessingLock sent()                                { return new ProcessingLock(OutboxStatus.SENT, null); }
    public static ProcessingLock deadLetter()                          { return new ProcessingLock(OutboxStatus.DEAD_LETTER, null); }

    public static ProcessingLock of(final OutboxStatus status, final LocalDateTime lockedAt) {
        return switch (status) {
            case PROCESSING  -> processing(lockedAt);
            case SENT        -> sent();
            case DEAD_LETTER -> deadLetter();
            case PENDING     -> pending();
        };
    }
}
