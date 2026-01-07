package com.histdata.etl.model;

import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests for ProgressStatus.
 */
public class ProgressStatusTest {

    private ProgressStatus progress;
    private LocalDate startDate;
    private LocalDate endDate;

    @Before
    public void setUp() {
        startDate = LocalDate.of(2025, 1, 1);
        endDate = LocalDate.of(2025, 1, 31);
        progress = new ProgressStatus(startDate, endDate);
    }

    @Test
    public void testConstructor() {
        assertEquals("Current date should match start date", startDate, progress.getCurrentDate());
        assertEquals("Current day number should be 1", 1, progress.getCurrentDayNumber());
        assertEquals("Total days should be 31", 31, progress.getTotalDays());
        assertEquals("Initial status should be INITIALIZED", JobStatus.INITIALIZED, progress.getStatus());
        assertNotNull("Start time should be set", progress.getStartTime());
        assertNull("End time should be null initially", progress.getEndTime());
        assertNull("Error message should be null initially", progress.getErrorMessage());
        
        // Verify record counts are zero
        assertEquals("Quote records should be 0", 0, progress.getQuoteRecords());
        assertEquals("Trade records should be 0", 0, progress.getTradeRecords());
        assertEquals("Future records should be 0", 0, progress.getFutureRecords());
        assertEquals("Total records should be 0", 0, progress.getTotalRecords());
    }

    @Test
    public void testConstructorSingleDay() {
        LocalDate singleDate = LocalDate.of(2025, 1, 1);
        ProgressStatus singleDay = new ProgressStatus(singleDate, singleDate);
        
        assertEquals("Current date should match start date", singleDate, singleDay.getCurrentDate());
        assertEquals("Current day number should be 1", 1, singleDay.getCurrentDayNumber());
        assertEquals("Total days should be 1", 1, singleDay.getTotalDays());
    }

    @Test
    public void testConstructorReverseDates() {
        // Start date after end date - should still calculate negative total days?
        // The calculateTotalDays method handles this internally
        LocalDate lateDate = LocalDate.of(2025, 1, 31);
        LocalDate earlyDate = LocalDate.of(2025, 1, 1);
        
        ProgressStatus reversed = new ProgressStatus(lateDate, earlyDate);
        
        // calculateTotalDays will return negative value + 1
        // end.toEpochDay() - start.toEpochDay() + 1 = -30 + 1 = -29
        assertEquals("Total days should be negative", -29, reversed.getTotalDays());
    }

    @Test
    public void testAdvanceToNextDay() {
        LocalDate nextDate = LocalDate.of(2025, 1, 2);
        
        // Set some record counts first
        progress.setQuoteRecords(100);
        progress.setTradeRecords(50);
        progress.setFutureRecords(30);
        
        progress.advanceToNextDay(nextDate);
        
        assertEquals("Current date should be updated", nextDate, progress.getCurrentDate());
        assertEquals("Current day number should be 2", 2, progress.getCurrentDayNumber());
        
        // Record counts should be reset
        assertEquals("Quote records should be reset to 0", 0, progress.getQuoteRecords());
        assertEquals("Trade records should be reset to 0", 0, progress.getTradeRecords());
        assertEquals("Future records should be reset to 0", 0, progress.getFutureRecords());
        assertEquals("Total records should be reset to 0", 0, progress.getTotalRecords());
    }

    @Test
    public void testSetStatus() {
        progress.setStatus(JobStatus.EXTRACTING);
        assertEquals("Status should be EXTRACTING", JobStatus.EXTRACTING, progress.getStatus());
        
        progress.setStatus(JobStatus.COMPLETED);
        assertEquals("Status should be COMPLETED", JobStatus.COMPLETED, progress.getStatus());
        assertNotNull("End time should be set for COMPLETED", progress.getEndTime());
        
        // Reset for FAILED test
        progress = new ProgressStatus(startDate, endDate);
        progress.setStatus(JobStatus.FAILED);
        assertEquals("Status should be FAILED", JobStatus.FAILED, progress.getStatus());
        assertNotNull("End time should be set for FAILED", progress.getEndTime());
    }

    @Test
    public void testSetErrorMessage() {
        String errorMsg = "Connection timeout";
        progress.setErrorMessage(errorMsg);
        
        assertEquals("Error message should match", errorMsg, progress.getErrorMessage());
    }

    @Test
    public void testSetQuoteRecords() {
        progress.setQuoteRecords(100);
        assertEquals("Quote records should be 100", 100, progress.getQuoteRecords());
        assertEquals("Total records should be 100", 100, progress.getTotalRecords());
        
        // Add trade records
        progress.setTradeRecords(50);
        assertEquals("Total records should be 150", 150, progress.getTotalRecords());
    }

    @Test
    public void testSetTradeRecords() {
        progress.setTradeRecords(200);
        assertEquals("Trade records should be 200", 200, progress.getTradeRecords());
        assertEquals("Total records should be 200", 200, progress.getTotalRecords());
        
        // Add future records
        progress.setFutureRecords(75);
        assertEquals("Total records should be 275", 275, progress.getTotalRecords());
    }

    @Test
    public void testSetFutureRecords() {
        progress.setFutureRecords(300);
        assertEquals("Future records should be 300", 300, progress.getFutureRecords());
        assertEquals("Total records should be 300", 300, progress.getTotalRecords());
        
        // Add quote records
        progress.setQuoteRecords(150);
        assertEquals("Total records should be 450", 450, progress.getTotalRecords());
    }

    @Test
    public void testSetLoadedRecords() {
        // Note: setLoadedRecords overrides the total records calculation
        progress.setQuoteRecords(100);
        progress.setTradeRecords(50);
        progress.setFutureRecords(30);
        
        // Initially total should be 180
        assertEquals("Total records should be 180", 180, progress.getTotalRecords());
        
        // Override with loaded count
        progress.setLoadedRecords(500);
        assertEquals("Total records should be overridden to 500", 500, progress.getTotalRecords());
    }

    @Test
    public void testRecordCountUpdatesTotal() {
        // Test that setting individual record counts updates total
        progress.setQuoteRecords(100);
        assertEquals("Total should be 100", 100, progress.getTotalRecords());
        
        progress.setTradeRecords(50);
        assertEquals("Total should be 150", 150, progress.getTotalRecords());
        
        progress.setFutureRecords(30);
        assertEquals("Total should be 180", 180, progress.getTotalRecords());
        
        // Update quote records
        progress.setQuoteRecords(200);
        assertEquals("Total should be 280", 280, progress.getTotalRecords());
    }

    @Test
    public void testGetters() {
        // Test all getters
        assertNotNull("getCurrentDate should return value", progress.getCurrentDate());
        assertTrue("getCurrentDayNumber should be positive", progress.getCurrentDayNumber() > 0);
        assertTrue("getTotalDays should be positive", progress.getTotalDays() > 0);
        assertNotNull("getStatus should return value", progress.getStatus());
        assertNotNull("getStartTime should return value", progress.getStartTime());
        
        // Initially null
        assertNull("getEndTime should be null initially", progress.getEndTime());
        assertNull("getErrorMessage should be null initially", progress.getErrorMessage());
        
        // Record count getters
        assertEquals("getQuoteRecords should return 0 initially", 0, progress.getQuoteRecords());
        assertEquals("getTradeRecords should return 0 initially", 0, progress.getTradeRecords());
        assertEquals("getFutureRecords should return 0 initially", 0, progress.getFutureRecords());
        assertEquals("getTotalRecords should return 0 initially", 0, progress.getTotalRecords());
    }

    @Test
    public void testEndTimeSetOnCompleted() {
        progress.setStatus(JobStatus.COMPLETED);
        assertNotNull("End time should be set when status is COMPLETED", progress.getEndTime());
        // endTime should not be before startTime (allows equal times due to fast execution)
        assertFalse("End time should not be before start time", 
                progress.getEndTime().isBefore(progress.getStartTime()));
    }

    @Test
    public void testEndTimeSetOnFailed() {
        progress.setStatus(JobStatus.FAILED);
        assertNotNull("End time should be set when status is FAILED", progress.getEndTime());
        // endTime should not be before startTime (allows equal times due to fast execution)
        assertFalse("End time should not be before start time", 
                progress.getEndTime().isBefore(progress.getStartTime()));
    }

    @Test
    public void testEndTimeNotSetOnOtherStatus() {
        progress.setStatus(JobStatus.EXTRACTING);
        assertNull("End time should be null when status is not COMPLETED or FAILED", 
                progress.getEndTime());
        
        progress.setStatus(JobStatus.TRANSFORMING);
        assertNull("End time should be null", progress.getEndTime());
        
        progress.setStatus(JobStatus.LOADING);
        assertNull("End time should be null", progress.getEndTime());
    }
}