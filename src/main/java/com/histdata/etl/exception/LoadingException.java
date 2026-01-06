package com.histdata.etl.exception;

/**
 * Exception thrown when data loading into target system fails.
 * Indicates failure to load data into DolphinDB.
 */
public class LoadingException extends RuntimeException {

    /**
     * Constructs LoadingException with specified message.
     *
     * @param message Error message
     */
    public LoadingException(String message) {
        super(message);
    }

    /**
     * Constructs LoadingException with message and cause.
     *
     * @param message Error message
     * @param cause Root cause
     */
    public LoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
