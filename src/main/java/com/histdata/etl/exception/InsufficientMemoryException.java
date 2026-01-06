package com.histdata.etl.exception;

/**
 * Exception thrown when available JVM heap memory is insufficient
 * to cache all transformed data for the current ETL job.
 */
public class InsufficientMemoryException extends RuntimeException {

    /**
     * Constructs InsufficientMemoryException with specified message.
     *
     * @param message Error message
     */
    public InsufficientMemoryException(String message) {
        super(message);
    }

    /**
     * Constructs InsufficientMemoryException with message and cause.
     *
     * @param message Error message
     * @param cause Root cause
     */
    public InsufficientMemoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
