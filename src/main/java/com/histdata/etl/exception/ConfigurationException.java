package com.histdata.etl.exception;

/**
 * Exception thrown when configuration file is missing, invalid, or
 * contains errors that prevent ETL tool from starting.
 */
public class ConfigurationException extends RuntimeException {

    /**
     * Constructs ConfigurationException with specified message.
     *
     * @param message Error message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs ConfigurationException with message and cause.
     *
     * @param message Error message
     * @param cause Root cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
