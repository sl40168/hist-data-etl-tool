package com.histdata.etl.model;

import com.histdata.etl.config.Config;
import com.histdata.etl.exception.ConcurrentExecutionException;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Context representing a single execution of the ETL tool.
 * Manages lifecycle of connections, data extraction, transformation, and loading.
 */
public class EtlJobContext {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String configPath;
    private final String jobId;
    private final long startTime;

    private Config config;
    private JobStatus status;
    private LocalDate currentDate;

    /**
     * Creates EtlJobContext for specified parameters.
     *
     * @param startDate Start date (format: YYYYMMDD)
     * @param endDate End date (format: YYYYMMDD)
     * @param configPath Path to configuration file, or null for embedded default
     * @throws IllegalArgumentException if date range is invalid
     */
    public EtlJobContext(String startDate, String endDate, String configPath) {
        // Validate date range
        if (!com.histdata.etl.util.DateUtils.isValidDateRange(startDate, endDate)) {
            throw new IllegalArgumentException(
                    "Invalid date range: start date must not be after end date"
            );
        }

        this.startDate = LocalDate.parse(startDate, java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        this.endDate = LocalDate.parse(endDate, java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        this.configPath = configPath;
        this.jobId = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
        this.status = JobStatus.INITIALIZED;
    }

    public EtlJobContext(LocalDate startDate, LocalDate endDate, String configPath, Config config, String jobId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.configPath = configPath;
        this.config = config;
        this.jobId = jobId;
        this.startTime = System.currentTimeMillis();
        this.status = JobStatus.INITIALIZED;
    }

    /**
     * Returns start date of ETL job.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns end date of ETL job.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns path to configuration file.
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Returns unique job ID.
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Returns start timestamp of job.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Returns loaded configuration.
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Sets loaded configuration.
     *
     * @param config Configuration object
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Returns current status of job.
     */
    public JobStatus getStatus() {
        return status;
    }

    /**
     * Updates status of job.
     *
     * @param status New job status
     */
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /**
     * Returns current business date being processed.
     */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * Sets current business date being processed.
     *
     * @param currentDate Business date
     */
    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * Acquires file lock to enforce single-instance execution.
     *
     * @throws ConcurrentExecutionException if another instance is running
     */
    public void acquireLock() throws ConcurrentExecutionException {
        try {
            com.histdata.etl.util.FileLock lock = new com.histdata.etl.util.FileLock();
            lock.acquire();
        } catch (ConcurrentExecutionException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw new ConcurrentExecutionException("Failed to acquire lock: " + e.getMessage(), e);
        }
    }

    /**
     * Releases file lock.
     */
    public void releaseLock() {
        // Note: File lock is released in the FileLock.release() method
        // This is a placeholder for any additional cleanup needed
    }

    /**
     * Validates that all prerequisites are met before starting ETL.
     *
     * @throws ConcurrentExecutionException If another instance is running
     * @throws com.histdata.etl.exception.ConfigurationException If configuration is invalid
     * @throws com.histdata.etl.exception.InsufficientMemoryException If memory is insufficient
     */
    public void validatePrerequisites() throws ConcurrentExecutionException,
                                                       com.histdata.etl.exception.ConfigurationException,
                                                       com.histdata.etl.exception.InsufficientMemoryException {
        // Acquire file lock
        acquireLock();

        // Validate configuration
        if (config == null) {
            throw new com.histdata.etl.exception.ConfigurationException("Configuration not loaded");
        }
        config.validate();

        // Memory check is done in CLI orchestration
        logger().info("Prerequisites validated successfully");
    }

    /**
     * Sets the job status.
     *
     * @param status New job status
     */
    public void setJobStatus(JobStatus status) {
        this.status = status;
    }

    /**
     * Gets the job status.
     *
     * @return Current job status
     */
    public JobStatus getJobStatus() {
        return this.status;
    }

    /**
     * Gets the progress status.
     *
     * @return Progress status
     */
    public ProgressStatus getProgressStatus() {
        return new ProgressStatus(startDate, endDate);
    }

    /**
     * Cleans up resources on job completion or failure.
     */
    public void cleanup() {
        releaseLock();
        logger().info("Cleanup completed");
    }

    /**
     * Returns logger instance for this context.
     */
    public org.slf4j.Logger logger() {
        return org.slf4j.LoggerFactory.getLogger(EtlJobContext.class);
    }
}
