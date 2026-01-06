package com.histdata.etl.exception;

/**
 * Exception thrown when data extraction from source systems fails.
 * Indicates failure to extract data from COS, MySQL, or other sources.
 */
public class ExtractionException extends RuntimeException {

    /**
     * Constructs ExtractionException with specified message.
     *
     * @param message Error message
     */
    public ExtractionException(String message) {
        super(message);
    }

    /**
     * Constructs ExtractionException with message and cause.
     *
     * @param message Error message
     * @param cause Root cause
     */
    public ExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
