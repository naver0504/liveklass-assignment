package com.liveklass.notification.domain.exception;

import com.liveklass.common.error.ExceptionInterface;
import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.common.error.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationException implements ExceptionInterface {

    INVALID_STATUS_TRANSITION("NOTIFICATION_001", "유효하지 않은 outbox 상태 전이입니다.",     BadRequestException.class),
    OUTBOX_NOT_FOUND(         "NOTIFICATION_002", "outbox를 찾을 수 없습니다.",               NotFoundException.class),
    NOTIFICATION_NOT_FOUND(   "NOTIFICATION_003", "알림을 찾을 수 없습니다.",                 NotFoundException.class),
    INVALID_PAYLOAD(          "NOTIFICATION_004", "채널 payload 최소 계약을 위반했습니다.",    BadRequestException.class),
    INVALID_DEDUP_KEY(        "NOTIFICATION_005", "DedupKey가 허용 길이를 초과했습니다.",      BadRequestException.class),
    INVALID_RETRY_STATE(      "NOTIFICATION_006", "RetryState 값이 유효하지 않습니다.",       BadRequestException.class),
    INVALID_PROCESSING_LOCK(  "NOTIFICATION_007", "ProcessingLock 불변식을 위반했습니다.",    BadRequestException.class),
    INVALID_OUTBOX(           "NOTIFICATION_008", "DomainEventOutbox 불변식을 위반했습니다.", BadRequestException.class);

    private final String errorCode;
    private final String message;
    private final Class<? extends RuntimeException> aClass;
}
