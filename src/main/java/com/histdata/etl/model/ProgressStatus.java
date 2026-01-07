package com.histdata.etl.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model tracking progress and status of ETL job execution.
 * Provides context for progress monitoring and error reporting.
 */
public class ProgressStatus {

    private LocalDate currentDate;
    private int currentDayNumber;
    private int totalDays;

    // Record counts
    private long quoteRecords = 0;
    private long tradeRecords = 0;
    private long futureRecords = 0;
    private long totalRecords = 0;

    // Status tracking
    private JobStatus status;
    private String errorMessage;

    // Timestamps
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Creates ProgressStatus with initial values.
     *
     * @param startDate Start date of ETL job
     * @param endDate End date of ETL job
     */
    public ProgressStatus(LocalDate startDate, LocalDate endDate) {
        this.currentDate = startDate;
        this.currentDayNumber = 1;
        this.totalDays = calculateTotalDays(startDate, endDate);
        this.status = JobStatus.INITIALIZED;
        this.startTime = LocalDateTime.now();
    }

    /**
     * Calculates total number of days between start and end (inclusive).
     */
    private int calculateTotalDays(LocalDate start, LocalDate end) {
        return (int) (end.toEpochDay() - start.toEpochDay()) + 1;
    }

    /**
     * Advances to next business day.
     */
    public void advanceToNextDay(LocalDate nextDate) {
        this.currentDate = nextDate;
        this.currentDayNumber++;
        // Reset record counts for new day
        this.quoteRecords = 0;
        this.tradeRecords = 0;
        this.futureRecords = 0;
        this.totalRecords = 0;
    }

    /**
     * Updates status of ETL job.
     *
     * @param status New job status
     */
    public void setStatus(JobStatus status) {
        this.status = status;
        if (status == JobStatus.FAILED) {
            this.endTime = LocalDateTime.now();
        } else if (status == JobStatus.COMPLETED) {
            this.endTime = LocalDateTime.now();
        }
    }

    /**
     * Sets error message for failed jobs.
     *
     * @param errorMessage Error description
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Updates quote record count.
     *
     * @param count Number of quote records extracted
     */
    public void setQuoteRecords(long count) {
        this.quoteRecords = count;
        this.totalRecords = quoteRecords + tradeRecords + futureRecords;
    }

    /**
     * Updates trade record count.
     *
     * @param count Number of trade records extracted
     */
    public void setTradeRecords(long count) {
        this.tradeRecords = count;
        this.totalRecords = quoteRecords + tradeRecords + futureRecords;
    }

    /**
     * Updates future quote record count.
     *
     * @param count Number of future quote records extracted
     */
    public void setFutureRecords(long count) {
        this.futureRecords = count;
        this.totalRecords = quoteRecords + tradeRecords + futureRecords;
    }

    /**
     * Updates loaded record count.
     *
     * @param count Number of records loaded into DolphinDB
     */
    public void setLoadedRecords(long count) {
        this.totalRecords = count;
    }

    // Getters
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public int getCurrentDayNumber() {
        return currentDayNumber;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public long getQuoteRecords() {
        return quoteRecords;
    }

    public long getTradeRecords() {
        return tradeRecords;
    }

    public long getFutureRecords() {
        return futureRecords;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
