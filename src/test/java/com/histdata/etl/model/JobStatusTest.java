package com.histdata.etl.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for JobStatus enum.
 */
public class JobStatusTest {

    @Test
    public void testEnumValues() {
        JobStatus[] values = JobStatus.values();
        
        assertEquals("Should have 7 enum values", 7, values.length);
        
        // Check each enum value exists
        assertEquals(JobStatus.INITIALIZED, JobStatus.valueOf("INITIALIZED"));
        assertEquals(JobStatus.CONNECTING, JobStatus.valueOf("CONNECTING"));
        assertEquals(JobStatus.EXTRACTING, JobStatus.valueOf("EXTRACTING"));
        assertEquals(JobStatus.TRANSFORMING, JobStatus.valueOf("TRANSFORMING"));
        assertEquals(JobStatus.LOADING, JobStatus.valueOf("LOADING"));
        assertEquals(JobStatus.COMPLETED, JobStatus.valueOf("COMPLETED"));
        assertEquals(JobStatus.FAILED, JobStatus.valueOf("FAILED"));
    }

    @Test
    public void testGetDescription() {
        assertEquals("Job initialized, connections established", 
                JobStatus.INITIALIZED.getDescription());
        assertEquals("Connecting to source and target systems", 
                JobStatus.CONNECTING.getDescription());
        assertEquals("Extracting data from sources", 
                JobStatus.EXTRACTING.getDescription());
        assertEquals("Transforming extracted data", 
                JobStatus.TRANSFORMING.getDescription());
        assertEquals("Loading data into target system", 
                JobStatus.LOADING.getDescription());
        assertEquals("ETL job completed successfully", 
                JobStatus.COMPLETED.getDescription());
        assertEquals("ETL job failed", 
                JobStatus.FAILED.getDescription());
    }

    @Test
    public void testEnumOrdinal() {
        assertEquals(0, JobStatus.INITIALIZED.ordinal());
        assertEquals(1, JobStatus.CONNECTING.ordinal());
        assertEquals(2, JobStatus.EXTRACTING.ordinal());
        assertEquals(3, JobStatus.TRANSFORMING.ordinal());
        assertEquals(4, JobStatus.LOADING.ordinal());
        assertEquals(5, JobStatus.COMPLETED.ordinal());
        assertEquals(6, JobStatus.FAILED.ordinal());
    }

    @Test
    public void testEnumComparison() {
        // Test compareTo
        assertTrue(JobStatus.INITIALIZED.compareTo(JobStatus.COMPLETED) < 0);
        assertTrue(JobStatus.COMPLETED.compareTo(JobStatus.INITIALIZED) > 0);
        assertEquals(0, JobStatus.INITIALIZED.compareTo(JobStatus.INITIALIZED));
        
        // Test equals
        assertTrue(JobStatus.INITIALIZED.equals(JobStatus.INITIALIZED));
        assertFalse(JobStatus.INITIALIZED.equals(JobStatus.COMPLETED));
        
        // Test == operator
        assertTrue(JobStatus.INITIALIZED == JobStatus.INITIALIZED);
        assertFalse(JobStatus.INITIALIZED == JobStatus.COMPLETED);
    }

    @Test
    public void testEnumToString() {
        assertEquals("INITIALIZED", JobStatus.INITIALIZED.toString());
        assertEquals("CONNECTING", JobStatus.CONNECTING.toString());
        assertEquals("EXTRACTING", JobStatus.EXTRACTING.toString());
        assertEquals("TRANSFORMING", JobStatus.TRANSFORMING.toString());
        assertEquals("LOADING", JobStatus.LOADING.toString());
        assertEquals("COMPLETED", JobStatus.COMPLETED.toString());
        assertEquals("FAILED", JobStatus.FAILED.toString());
    }

    @Test
    public void testEnumSwitch() {
        // Test that enum can be used in switch statement
        JobStatus status = JobStatus.EXTRACTING;
        String result = "";
        
        switch (status) {
            case INITIALIZED:
                result = "initialized";
                break;
            case CONNECTING:
                result = "connecting";
                break;
            case EXTRACTING:
                result = "extracting";
                break;
            case TRANSFORMING:
                result = "transforming";
                break;
            case LOADING:
                result = "loading";
                break;
            case COMPLETED:
                result = "completed";
                break;
            case FAILED:
                result = "failed";
                break;
        }
        
        assertEquals("extracting", result);
    }
}