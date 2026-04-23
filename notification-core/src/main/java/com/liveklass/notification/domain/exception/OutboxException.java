package com.liveklass.notification.domain.exception;

import com.liveklass.common.error.ExceptionInterface;
import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.common.error.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OutboxException implements ExceptionInterface {

    INVALID_STATUS_TRANSITION("OUTBOX_001", "유효하지 않은 outbox 상태 전이입니다.",        BadRequestException.class),
    OUTBOX_NOT_FOUND(         "OUTBOX_002", "outbox를 찾을 수 없습니다.",                  NotFoundException.class),
    INVALID_RETRY_STATE(      "OUTBOX_003", "RetryState 값이 유효하지 않습니다.",          BadRequestException.class),
    INVALID_PROCESSING_LOCK(  "OUTBOX_005", "ProcessingLock 불변식을 위반했습니다.",       BadRequestException.class),
    INVALID_OUTBOX(           "OUTBOX_006", "DomainEventOutbox 불변식을 위반했습니다.",    BadRequestException.class);

    private final String errorCode;
    private final String message;
    private final Class<? extends RuntimeException> aClass;
}
