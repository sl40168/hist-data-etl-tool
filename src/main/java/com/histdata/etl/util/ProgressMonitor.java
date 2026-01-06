package com.histdata.etl.util;

import com.histdata.etl.model.EtlJobContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for monitoring and displaying ETL progress to console.
 * Provides real-time status updates with completion percentages.
 */
public class ProgressMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ProgressMonitor.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDate businessDate;
    private final int totalDays;

    private int currentDayNumber = 0;
    private long startTime;
    private long lastUpdate = 0;
    private long recordsProcessed = 0;
    private long totalRecords = 0;

    // Data source progress tracking
    private long quoteRecords = 0;
    private long tradeRecords = 0;
    private long futureRecords = 0;

    // Progress update frequency: update every 10,000 records or 5 seconds
    private static final long RECORD_UPDATE_INTERVAL = 10000;
    private static final long TIME_UPDATE_INTERVAL_MS = 5000; // 5 seconds

    /**
     * Creates a ProgressMonitor for the specified date range.
     *
     * @param startDate Start date
     * @param endDate End date
     */
    public ProgressMonitor(LocalDate startDate, LocalDate endDate) {
        this.businessDate = startDate;
        this.totalDays = calculateTotalDays(startDate, endDate);
        this.startTime = System.currentTimeMillis();
        this.lastUpdate = startTime;

        logger.info("Starting ETL for date range: {} - {} ({})",
                startDate, endDate, totalDays);
    }

    /**
     * Creates a ProgressMonitor for an EtlJobContext.
     *
     * @param context EtlJobContext instance
     */
    public ProgressMonitor(EtlJobContext context) {
        this(context.getStartDate(), context.getEndDate());
    }

    /**
     * Calculates total number of days between start and end (inclusive).
     */
    private int calculateTotalDays(LocalDate start, LocalDate end) {
        return (int) (end.toEpochDay() - start.toEpochDay()) + 1;
    }

    /**
     * Starts processing a new business day.
     *
     * @param date Business date being processed
     */
    public void startDay(LocalDate date) {
        currentDayNumber++;
        quoteRecords = 0;
        tradeRecords = 0;
        futureRecords = 0;
        recordsProcessed = 0;

        String dayInfo = String.format("[%s] Processing business date: %s",
                LocalDateTime.now().format(TIME_FORMATTER), date);
        System.out.println(dayInfo);
        logger.info(dayInfo);
    }

    /**
     * Updates progress for XBond Market Quote data source.
     *
     * @param recordsCount Number of records extracted
     * @param complete Whether extraction is complete
     */
    public void updateQuoteProgress(long recordsCount, boolean complete) {
        quoteRecords = recordsCount;
        updateProgress("XBond Market Quote", recordsCount, complete);
    }

    /**
     * Updates progress for XBond Trade data source.
     *
     * @param recordsCount Number of records extracted
     * @param complete Whether extraction is complete
     */
    public void updateTradeProgress(long recordsCount, boolean complete) {
        tradeRecords = recordsCount;
        updateProgress("XBond Trade", recordsCount, complete);
    }

    /**
     * Updates progress for Bond Future L2 Quote data source.
     *
     * @param recordsCount Number of records extracted
     * @param complete Whether extraction is complete
     */
    public void updateFutureProgress(long recordsCount, boolean complete) {
        futureRecords = recordsCount;
        updateProgress("Bond Future L2 Quote", recordsCount, complete);
    }

    /**
     * Updates progress for a data source with visual bar.
     *
     * @param sourceName Name of data source
     * @param currentRecords Current record count
     * @param complete Whether extraction is complete
     */
    private void updateProgress(String sourceName, long currentRecords, boolean complete) {
        long now = System.currentTimeMillis();
        boolean shouldUpdate = complete ||
                (currentRecords > 0 && (currentRecords - recordsProcessed >= RECORD_UPDATE_INTERVAL)) ||
                (now - lastUpdate >= TIME_UPDATE_INTERVAL_MS);

        if (shouldUpdate) {
            recordsProcessed = currentRecords;
            lastUpdate = now;

            int percentage = totalRecords > 0 ? (int) ((currentRecords * 100) / totalRecords) : 100;
            String bar = createProgressBar(percentage);

            System.out.printf("Extracting %s... %s %d%%%n",
                    sourceName, bar, percentage);
        }
    }

    /**
     * Creates a visual progress bar (50 characters wide).
     *
     * @param percentage Completion percentage (0-100)
     * @return Progress bar string
     */
    private String createProgressBar(int percentage) {
        int filled = (percentage * 50) / 100;
        StringBuilder bar = new StringBuilder(50);
        for (int i = 0; i < filled; i++) {
            bar.append("=");
        }
        for (int i = filled; i < 50; i++) {
            bar.append(" ");
        }
        return bar.toString();
    }

    /**
     * Reports sorting phase.
     */
    public void reportSorting() {
        System.out.println("Sorting records by receive_time...");
        logger.info("Sorting records by receive_time");
    }

    /**
     * Reports loading phase with total record count.
     *
     * @param totalRecords Total records to load
     */
    public void reportLoading(long totalRecords) {
        this.totalRecords = totalRecords;
        this.recordsProcessed = 0;
        String message = String.format("Loading %,d records into DolphinDB...", totalRecords);
        System.out.println(message);
        logger.info(message);
    }

    /**
     * Reports loading progress during batch inserts.
     *
     * @param recordsLoaded Number of records loaded so far
     */
    public void reportLoadingProgress(long recordsLoaded) {
        long now = System.currentTimeMillis();
        boolean shouldUpdate = (recordsLoaded - recordsProcessed >= RECORD_UPDATE_INTERVAL) ||
                (now - lastUpdate >= TIME_UPDATE_INTERVAL_MS);

        if (shouldUpdate) {
            recordsProcessed = recordsLoaded;
            lastUpdate = now;
            int percentage = (int) ((recordsLoaded * 100) / totalRecords);
            String bar = createProgressBar(percentage);
            System.out.printf("Loading... %s %d%%%n", bar, percentage);
        }
    }

    /**
     * Reports successful completion of ETL for current day.
     */
    public void reportDayComplete() {
        long elapsed = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsed / 1000;
        String message = String.format(
                "Completed day %d/%d. Records: quotes=%d, trades=%d, futures=%d (elapsed: %ds)",
                currentDayNumber, totalDays, quoteRecords, tradeRecords, futureRecords, elapsedSeconds
        );
        System.out.println(message);
        logger.info(message);
    }

    /**
     * Reports successful completion of entire ETL job.
     *
     * @param totalRecords Total records processed across all days
     */
    public void reportComplete(long totalRecords) {
        long elapsed = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsed / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;

        String message = String.format(
                "[%s] ETL completed successfully. Total records: %,d (elapsed: %dm %ds)",
                LocalDateTime.now().format(TIME_FORMATTER), totalRecords, minutes, seconds
        );
        System.out.println(message);
        logger.info(message);
    }

    /**
     * Reports warning for no data found.
     *
     * @param date Business date with no data
     */
    public void reportNoData(LocalDate date) {
        String message = String.format(
                "Warning: No data found for business date %s in any source system",
                date
        );
        System.out.println(message);
        logger.warn(message);
    }

    /**
     * Reports error with clear context.
     *
     * @param errorMessage Error message
     * @param exception Optional exception for logging
     */
    public void reportError(String errorMessage, Exception exception) {
        System.err.println("Error: " + errorMessage);
        if (exception != null) {
            logger.error(errorMessage, exception);
        } else {
            logger.error(errorMessage);
        }
    }

    /**
     * Displays a summary of the ETL job.
     */
    public void displaySummary() {
        long elapsed = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsed / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;

        String message = String.format(
                "ETL Summary - Records: quotes=%d, trades=%d, futures=%d, total=%d (elapsed: %dm %ds)",
                quoteRecords, tradeRecords, futureRecords,
                quoteRecords + tradeRecords + futureRecords, minutes, seconds
        );
        System.out.println(message);
        logger.info(message);
    }
}
