package com.liveklass.common.error;

public interface ExceptionInterface {
    String getErrorCode();
    String getMessage();
    Class<?> getAClass();
}