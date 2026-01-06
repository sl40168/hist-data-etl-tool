package com.histdata.etl.exception;

/**
 * Exception thrown when data transformation fails.
 * Indicates failure to transform extracted data according to business rules.
 */
public class TransformationException extends RuntimeException {

    /**
     * Constructs TransformationException with specified message.
     *
     * @param message Error message
     */
    public TransformationException(String message) {
        super(message);
    }

    /**
     * Constructs TransformationException with message and cause.
     *
     * @param message Error message
     * @param cause Root cause
     */
    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
