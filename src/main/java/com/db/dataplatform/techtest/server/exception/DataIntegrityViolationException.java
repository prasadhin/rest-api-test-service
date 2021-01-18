package com.db.dataplatform.techtest.server.exception;

/**
 * Exception for capturing an invalid data.
 */
public class DataIntegrityViolationException extends RuntimeException {
    public DataIntegrityViolationException(String message) {
        super(message);
    }
}
