package com.histdata.etl.util;

import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for ProgressMonitor.
 */
public class ProgressMonitorTest {

    @Test
    public void testConstructorWithDateRange() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 3);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        // Should not throw
        assertNotNull(monitor);
    }

    @Test
    public void testCalculateTotalDays() {
        // Indirectly test via constructor
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        // No assertion, just ensure no exception
    }

    @Test
    public void testStartDay() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 3);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.startDay(LocalDate.of(2025, 1, 2));
        // Should not throw
    }

    @Test
    public void testUpdateQuoteProgress() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.updateQuoteProgress(1000, false);
        monitor.updateQuoteProgress(2000, true);
        // Should not throw
    }

    @Test
    public void testUpdateTradeProgress() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.updateTradeProgress(500, false);
        monitor.updateTradeProgress(1000, true);
        // Should not throw
    }

    @Test
    public void testUpdateFutureProgress() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.updateFutureProgress(10000, false);
        monitor.updateFutureProgress(20000, true);
        // Should not throw
    }

    @Test
    public void testReportSorting() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.reportSorting();
        // Should not throw
    }

    @Test
    public void testReportLoading() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.reportLoading(50000);
        // Should not throw
    }

    @Test
    public void testReportLoadingProgress() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.reportLoading(100000);
        monitor.reportLoadingProgress(50000);
        monitor.reportLoadingProgress(100000);
        // Should not throw
    }

    @Test
    public void testReportDayComplete() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.reportDayComplete();
        // Should not throw
    }

    @Test
    public void testReportComplete() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.reportComplete(150000);
        // Should not throw
    }

    @Test
    public void testReportNoData() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);
        ProgressMonitor monitor = new ProgressMonitor(start, end);
        monitor.reportNoData(LocalDate.of(2025, 1, 2));
        // Should not throw
    }
}