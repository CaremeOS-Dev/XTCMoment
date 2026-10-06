package com.xtc.assistantapi.exception;

import java.io.Serializable;

/**
 * 指令处理异常。
 */
public class HandleDirectiveException extends RuntimeException implements Serializable {

    /** 异常类型。 */
    public enum ExceptionType {
        UNEXPECTED_INFORMATION_RECEIVED,
        UNSUPPORTED_OPERATION,
        INTERNAL_ERROR
    }

    private final ExceptionType exceptionType;

    public HandleDirectiveException(ExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
}