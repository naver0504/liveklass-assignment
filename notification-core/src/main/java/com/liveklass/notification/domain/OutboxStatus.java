package com.liveklass.notification.domain;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.notification.domain.exception.OutboxException;

public enum OutboxStatus {

    PENDING,
    PROCESSING,
    SENT,
    DEAD_LETTER;

    public void validateTransitionTo(final OutboxStatus next) {
        final boolean valid = switch (this) {
            case PENDING     -> next == PROCESSING;
            case PROCESSING  -> next == SENT || next == PENDING || next == DEAD_LETTER;
            case DEAD_LETTER -> next == PENDING;
            case SENT        -> false;
        };
        if (!valid) {
            throw ExceptionCreator.create(OutboxException.INVALID_STATUS_TRANSITION,
                    this + " -> " + next);
        }
    }
}
