package com.liveklass.lecture.domain.exception;

import com.liveklass.common.error.ExceptionInterface;
import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.common.error.exception.ConflictException;
import com.liveklass.common.error.exception.ForbiddenException;
import com.liveklass.common.error.exception.InternalServerErrorException;
import com.liveklass.common.error.exception.NotFoundException;

public enum EnrollmentException implements ExceptionInterface {
    ENROLLMENT_NOT_FOUND("ENROLLMENT_001", "수강 정보를 찾을 수 없습니다.", NotFoundException.class),
    ENROLLMENT_REENROLL_NOT_ALLOWED("ENROLLMENT_002", "취소된 수강은 재신청할 수 없습니다.", ConflictException.class),
    ENROLLMENT_OWNER_MISMATCH("ENROLLMENT_003", "본인 수강만 취소할 수 있습니다.", ForbiddenException.class),
    ENROLLMENT_ID_REQUIRED("ENROLLMENT_004", "수강 식별자는 필수입니다.", BadRequestException.class),
    ENROLLMENT_LECTURE_ID_REQUIRED("ENROLLMENT_005", "수강 정보에는 강의 식별자가 필요합니다.", BadRequestException.class),
    ENROLLMENT_USER_ID_REQUIRED("ENROLLMENT_006", "수강 정보에는 사용자 식별자가 필요합니다.", BadRequestException.class),
    ENROLLMENT_STATUS_REQUIRED("ENROLLMENT_007", "수강 상태는 필수입니다.", BadRequestException.class),
    ENROLLMENT_CREATED_AT_REQUIRED("ENROLLMENT_008", "수강 생성 시각은 필수입니다.", BadRequestException.class),
    ENROLLMENT_UPDATED_AT_REQUIRED("ENROLLMENT_009", "수강 수정 시각은 필수입니다.", BadRequestException.class),
    ENROLLMENT_CANCELLED_AT_REQUIRED("ENROLLMENT_010", "수강 취소 시각은 필수입니다.", BadRequestException.class),
    ENROLLMENT_PERSISTENCE_ID_REQUIRED("ENROLLMENT_011", "저장된 수강에는 식별자가 필요합니다.", InternalServerErrorException.class),
    ENROLLMENT_ALREADY_EXISTS("ENROLLMENT_012", "이미 수강 신청한 강의입니다.", ConflictException.class),
    ENROLLMENT_CANCEL_NOT_ALLOWED("ENROLLMENT_013", "수강 중인 상태만 취소할 수 있습니다.", ConflictException.class),
    USER_ID_REQUIRED("USER_001", "사용자 식별자는 필수입니다.", BadRequestException.class),
    ENROLLMENT_EVENT_REQUIRED("LECTURE-EVENT_001", "수강 이벤트 필수값이 누락되었습니다.", BadRequestException.class),
    ENROLLMENT_EVENT_BLANK("LECTURE-EVENT_002", "수강 이벤트 문자열 값은 공백일 수 없습니다.", BadRequestException.class),
    PAYMENT_EVENT_REQUIRED("PAYMENT-EVENT_001", "결제 이벤트 필수값이 누락되었습니다.", BadRequestException.class),
    PAYMENT_EVENT_BLANK("PAYMENT-EVENT_002", "결제 이벤트 문자열 값은 공백일 수 없습니다.", BadRequestException.class),
    PAYMENT_EVENT_AMOUNT_INVALID("PAYMENT-EVENT_003", "결제 금액은 음수일 수 없습니다.", BadRequestException.class);

    private final String errorCode;
    private final String message;
    private final Class<? extends RuntimeException> aClass;

    EnrollmentException(
            final String errorCode,
            final String message,
            final Class<? extends RuntimeException> aClass
    ) {
        this.errorCode = errorCode;
        this.message = message;
        this.aClass = aClass;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Class<? extends RuntimeException> getAClass() {
        return aClass;
    }
}
