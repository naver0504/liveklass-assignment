package com.liveklass.notification.domain.exception;

import com.liveklass.common.error.ExceptionInterface;
import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.common.error.exception.ForbiddenException;
import com.liveklass.common.error.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationException implements ExceptionInterface {

    NOTIFICATION_NOT_FOUND(    "NOTIFICATION_001", "알림을 찾을 수 없습니다.",              NotFoundException.class),
    NOTIFICATION_ACCESS_DENIED("NOTIFICATION_002", "해당 알림에 접근 권한이 없습니다.",     ForbiddenException.class),
    UNSUPPORTED_CHANNEL_TYPE(  "NOTIFICATION_003", "지원하지 않는 발송 채널입니다.",        BadRequestException.class);

    private final String errorCode;
    private final String message;
    private final Class<? extends RuntimeException> aClass;
}
