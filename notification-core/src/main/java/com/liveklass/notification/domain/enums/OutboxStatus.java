package com.liveklass.notification.domain.enums;

import com.liveklass.common.error.ExceptionCreator;
import com.liveklass.notification.domain.exception.OutboxException;

import java.util.Map;
import java.util.Set;

public enum OutboxStatus {

    PENDING, PROCESSING, SENT, DEAD_LETTER;

    private static final Map<OutboxStatus, Set<OutboxStatus>> ALLOWED = Map.of(
            PENDING,      Set.of(PROCESSING),
            PROCESSING,   Set.of(PENDING, SENT, DEAD_LETTER),
            SENT,         Set.of(),
            DEAD_LETTER,  Set.of(PENDING)
    );

    public void validateTransitionTo(final OutboxStatus next) {
        if (!ALLOWED.get(this).contains(next)) {
            throw ExceptionCreator.create(OutboxException.INVALID_STATUS_TRANSITION,
                    this + " → " + next + " 전이는 허용되지 않습니다.");
        }
    }
}
