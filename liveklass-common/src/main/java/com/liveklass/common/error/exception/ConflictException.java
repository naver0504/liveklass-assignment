package com.liveklass.common.error.exception;

public class ConflictException extends AbstractException {
    public ConflictException(final String errorCode, final String message) {
        super(errorCode, message);
    }

    public ConflictException(final String errorCode, final String message, final String errorLog) {
        super(errorCode, message, errorLog);
    }
}
