package com.xtc.log.exception;

/** Thrown when a log record contains data that must be masked before logging. */
public class LogSecurityException extends RuntimeException {

    private String message;

    public LogSecurityException(String message) {
        super(message);
        this.message = message;
    }
}
