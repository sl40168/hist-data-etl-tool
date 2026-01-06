package com.histdata.etl.exception;

/**
 * Exception thrown when another instance of ETL tool is already running.
 * Indicates violation of single-instance enforcement policy.
 */
public class ConcurrentExecutionException extends RuntimeException {

    /**
     * Constructs ConcurrentExecutionException with specified message.
     *
     * @param message Error message
     */
    public ConcurrentExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs ConcurrentExecutionException with message and cause.
     *
     * @param message Error message
     * @param cause Root cause
     */
    public ConcurrentExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
