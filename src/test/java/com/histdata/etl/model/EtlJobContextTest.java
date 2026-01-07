package com.histdata.etl.model;

import com.histdata.etl.config.Config;
import com.histdata.etl.exception.ConcurrentExecutionException;
import com.histdata.etl.exception.ConfigurationException;
import com.histdata.etl.exception.InsufficientMemoryException;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EtlJobContext.
 */
@RunWith(MockitoJUnitRunner.class)
public class EtlJobContextTest {

    @Mock
    private Config mockConfig;

    private EtlJobContext context;

    @Before
    public void setUp() {
        // Create context with valid dates
        context = new EtlJobContext("20250101", "20250131", "config.ini");
        context.setConfig(mockConfig);
    }

    @Test
    public void testConstructorWithValidDates() {
        EtlJobContext ctx = new EtlJobContext("20250101", "20250131", "config.ini");
        
        assertNotNull("Context should be created", ctx);
        assertEquals("Start date should match", LocalDate.of(2025, 1, 1), ctx.getStartDate());
        assertEquals("End date should match", LocalDate.of(2025, 1, 31), ctx.getEndDate());
        assertEquals("Config path should match", "config.ini", ctx.getConfigPath());
        assertNotNull("Job ID should not be null", ctx.getJobId());
        assertTrue("Start time should be set", ctx.getStartTime() > 0);
        assertEquals("Initial status should be INITIALIZED", JobStatus.INITIALIZED, ctx.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidDateRange() {
        // Start date after end date
        new EtlJobContext("20250201", "20250131", "config.ini");
    }

    @Test
    public void testConstructorWithLocalDates() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        String jobId = "test-job-id";
        
        EtlJobContext ctx = new EtlJobContext(start, end, "config.ini", mockConfig, jobId);
        
        assertEquals("Start date should match", start, ctx.getStartDate());
        assertEquals("End date should match", end, ctx.getEndDate());
        assertEquals("Config should match", mockConfig, ctx.getConfig());
        assertEquals("Job ID should match", jobId, ctx.getJobId());
        assertEquals("Status should be INITIALIZED", JobStatus.INITIALIZED, ctx.getStatus());
    }

    @Test
    public void testGettersAndSetters() {
        // Test basic getters
        assertEquals("Start date should match", LocalDate.of(2025, 1, 1), context.getStartDate());
        assertEquals("End date should match", LocalDate.of(2025, 1, 31), context.getEndDate());
        assertEquals("Config path should match", "config.ini", context.getConfigPath());
        assertNotNull("Job ID should not be null", context.getJobId());
        assertTrue("Start time should be positive", context.getStartTime() > 0);
        assertSame("Config should match mock", mockConfig, context.getConfig());
        assertEquals("Initial status should be INITIALIZED", JobStatus.INITIALIZED, context.getStatus());

        // Test setters
        LocalDate newDate = LocalDate.of(2025, 2, 1);
        context.setCurrentDate(newDate);
        assertEquals("Current date should be updated", newDate, context.getCurrentDate());

        JobStatus newStatus = JobStatus.EXTRACTING;
        context.setStatus(newStatus);
        assertEquals("Status should be updated", newStatus, context.getStatus());

        // Test job status alias methods
        context.setJobStatus(JobStatus.LOADING);
        assertEquals("Job status should be updated", JobStatus.LOADING, context.getJobStatus());
    }

    @Test
    public void testAcquireLockSuccess() throws Exception {
        // Mock FileLock to not throw exception
        try (MockedStatic<com.histdata.etl.util.FileLock> mockedFileLock = mockStatic(com.histdata.etl.util.FileLock.class)) {
            com.histdata.etl.util.FileLock mockLock = mock(com.histdata.etl.util.FileLock.class);
            mockedFileLock.when(() -> new com.histdata.etl.util.FileLock()).thenReturn(mockLock);
            
            context.acquireLock();
            
            // Should not throw
            verify(mockLock, times(1)).acquire();
        }
    }

    @Test(expected = ConcurrentExecutionException.class)
    public void testAcquireLockConcurrentExecution() throws Exception {
        // Mock FileLock to throw ConcurrentExecutionException
        try (MockedStatic<com.histdata.etl.util.FileLock> mockedFileLock = mockStatic(com.histdata.etl.util.FileLock.class)) {
            com.histdata.etl.util.FileLock mockLock = mock(com.histdata.etl.util.FileLock.class);
            mockedFileLock.when(() -> new com.histdata.etl.util.FileLock()).thenReturn(mockLock);
            
            doThrow(new ConcurrentExecutionException("Another instance is running"))
                    .when(mockLock).acquire();
            
            context.acquireLock();
        }
    }

    @Test(expected = ConcurrentExecutionException.class)
    public void testAcquireLockIOException() throws Exception {
        // Mock FileLock to throw IOException (wrapped in ConcurrentExecutionException)
        try (MockedStatic<com.histdata.etl.util.FileLock> mockedFileLock = mockStatic(com.histdata.etl.util.FileLock.class)) {
            com.histdata.etl.util.FileLock mockLock = mock(com.histdata.etl.util.FileLock.class);
            mockedFileLock.when(() -> new com.histdata.etl.util.FileLock()).thenReturn(mockLock);
            
            doThrow(new java.io.IOException("File lock error"))
                    .when(mockLock).acquire();
            
            context.acquireLock();
        }
    }

    @Test
    public void testValidatePrerequisitesSuccess() throws Exception {
        // Mock config validation to succeed
        doNothing().when(mockConfig).validate();
        
        // Mock FileLock
        try (MockedStatic<com.histdata.etl.util.FileLock> mockedFileLock = mockStatic(com.histdata.etl.util.FileLock.class)) {
            com.histdata.etl.util.FileLock mockLock = mock(com.histdata.etl.util.FileLock.class);
            mockedFileLock.when(() -> new com.histdata.etl.util.FileLock()).thenReturn(mockLock);
            
            // Should not throw
            context.validatePrerequisites();
            
            verify(mockLock, times(1)).acquire();
            verify(mockConfig, times(1)).validate();
        }
    }

    @Test(expected = ConfigurationException.class)
    public void testValidatePrerequisitesConfigNotLoaded() throws Exception {
        // Create context without setting config
        EtlJobContext ctx = new EtlJobContext("20250101", "20250131", "config.ini");
        
        // Should throw ConfigurationException
        ctx.validatePrerequisites();
    }

    @Test(expected = ConfigurationException.class)
    public void testValidatePrerequisitesConfigValidationFails() throws Exception {
        // Mock config validation to fail
        doThrow(new ConfigurationException("Invalid config"))
                .when(mockConfig).validate();
        
        // Mock FileLock
        try (MockedStatic<com.histdata.etl.util.FileLock> mockedFileLock = mockStatic(com.histdata.etl.util.FileLock.class)) {
            com.histdata.etl.util.FileLock mockLock = mock(com.histdata.etl.util.FileLock.class);
            mockedFileLock.when(() -> new com.histdata.etl.util.FileLock()).thenReturn(mockLock);
            
            context.validatePrerequisites();
        }
    }

    @Test(expected = ConcurrentExecutionException.class)
    public void testValidatePrerequisitesConcurrentExecution() throws Exception {
        // Mock FileLock to throw ConcurrentExecutionException
        try (MockedStatic<com.histdata.etl.util.FileLock> mockedFileLock = mockStatic(com.histdata.etl.util.FileLock.class)) {
            com.histdata.etl.util.FileLock mockLock = mock(com.histdata.etl.util.FileLock.class);
            mockedFileLock.when(() -> new com.histdata.etl.util.FileLock()).thenReturn(mockLock);
            
            doThrow(new ConcurrentExecutionException("Another instance is running"))
                    .when(mockLock).acquire();
            
            context.validatePrerequisites();
        }
    }

    @Test
    public void testGetProgressStatus() {
        ProgressStatus progress = context.getProgressStatus();
        
        assertNotNull("Progress status should not be null", progress);
        assertEquals("Current date should match start date", context.getStartDate(), progress.getCurrentDate());
        assertEquals("Total days should be 31", 31, progress.getTotalDays());
        assertEquals("Status should be INITIALIZED", JobStatus.INITIALIZED, progress.getStatus());
    }

    @Test
    public void testCleanup() {
        // Cleanup should not throw
        context.cleanup();
        
        // No assertions - just ensure no exception
    }

    @Test
    public void testLogger() {
        assertNotNull("Logger should not be null", context.logger());
    }

    @Test
    public void testSetConfig() {
        Config newConfig = mock(Config.class);
        context.setConfig(newConfig);
        
        assertSame("Config should be updated", newConfig, context.getConfig());
    }

    @Test
    public void testGetJobId() {
        String jobId = context.getJobId();
        assertNotNull("Job ID should not be null", jobId);
        assertFalse("Job ID should not be empty", jobId.isEmpty());
    }

    @Test
    public void testGetStartTime() {
        long startTime = context.getStartTime();
        assertTrue("Start time should be positive", startTime > 0);
        assertTrue("Start time should be recent", System.currentTimeMillis() - startTime < 10000);
    }
}