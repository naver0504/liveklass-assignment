package com.liveklass.lecture.domain.exception;

import com.liveklass.common.error.ExceptionInterface;
import com.liveklass.common.error.exception.BadRequestException;
import com.liveklass.common.error.exception.InternalServerErrorException;
import com.liveklass.common.error.exception.NotFoundException;

public enum LectureException implements ExceptionInterface {
    LECTURE_NOT_FOUND("LECTURE_001", "강의를 찾을 수 없습니다.", NotFoundException.class),
    LECTURE_ID_REQUIRED("LECTURE_002", "강의 식별자는 필수입니다.", BadRequestException.class),
    LECTURE_TITLE_REQUIRED("LECTURE_003", "강의 제목은 필수입니다.", BadRequestException.class),
    LECTURE_TITLE_BLANK("LECTURE_004", "강의 제목은 공백일 수 없습니다.", BadRequestException.class),
    LECTURE_START_AT_REQUIRED("LECTURE_005", "강의 시작 시각은 필수입니다.", BadRequestException.class),
    LECTURE_CREATED_AT_REQUIRED("LECTURE_006", "강의 생성 시각은 필수입니다.", BadRequestException.class),
    LECTURE_UPDATED_AT_REQUIRED("LECTURE_007", "강의 수정 시각은 필수입니다.", BadRequestException.class),
    LECTURE_PERSISTENCE_ID_REQUIRED("LECTURE_008", "저장된 강의에는 식별자가 필요합니다.", InternalServerErrorException.class);

    private final String errorCode;
    private final String message;
    private final Class<? extends RuntimeException> aClass;

    LectureException(
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
