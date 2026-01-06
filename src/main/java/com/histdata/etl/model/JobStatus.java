package com.histdata.etl.model;

/**
 * Enumeration representing the status of an ETL job.
 * Tracks state transitions throughout ETL process lifecycle.
 */
public enum JobStatus {

    INITIALIZED("Job initialized, connections established"),
    CONNECTING("Connecting to source and target systems"),
    EXTRACTING("Extracting data from sources"),
    TRANSFORMING("Transforming extracted data"),
    LOADING("Loading data into target system"),
    COMPLETED("ETL job completed successfully"),
    FAILED("ETL job failed");

    private final String description;

    JobStatus(String description) {
        this.description = description;
    }

    /**
     * Returns human-readable description of job status.
     *
     * @return Status description
     */
    public String getDescription() {
        return description;
    }
}
