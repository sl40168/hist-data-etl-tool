package com.histdata.etl.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import com.histdata.etl.exception.ConcurrentExecutionException;

import static org.junit.Assert.*;

/**
 * Unit tests for FileLock.
 */
public class FileLockTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private FileLock fileLock;
    private File lockFile;

    private String originalUserDir;

    @Before
    public void setUp() throws IOException {
        // Save original user directory
        originalUserDir = System.getProperty("user.dir");
        // Use temporary folder to avoid conflicts with existing lock file
        System.setProperty("user.dir", tempFolder.getRoot().getAbsolutePath());
        fileLock = new FileLock();
    }

    @After
    public void tearDown() {
        // Ensure lock is released
        if (fileLock != null) {
            fileLock.release();
        }
        // Restore original user directory
        if (originalUserDir != null) {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    public void testAcquireSuccess() throws Exception {
        boolean acquired = fileLock.acquire();
        assertTrue("Lock should be acquired successfully", acquired);
    }

    @Test(expected = ConcurrentExecutionException.class)
    public void testAcquireThrowsWhenLockFileExists() throws Exception {
        // Simulate existing lock file by creating it manually
        lockFile = new File(".etl-tool.pid");
        // Don't actually lock the file, just create it with a valid PID
        try (RandomAccessFile raf = new RandomAccessFile(lockFile, "rw")) {
            raf.writeLong(java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode());
        }
        // Now try to acquire lock
        fileLock.acquire();
    }

    @Test
    public void testRelease() throws Exception {
        fileLock.acquire();
        fileLock.release();
        // After release, lock file should be deleted
        assertFalse("Lock file should be deleted after release", new File(".etl-tool.pid").exists());
    }

    @Test
    public void testReleaseWhenNotAcquired() {
        // Should not throw when release called without acquire
        fileLock.release();
    }

    @Test
    public void testStaleLockDetection() throws Exception {
        // Create a lock file with non-existing PID
        lockFile = new File(".etl-tool.pid");
        try (RandomAccessFile raf = new RandomAccessFile(lockFile, "rw")) {
            raf.writeLong(999999999L); // Non-existing PID
        }
        // Acquire should succeed because lock is stale
        boolean acquired = fileLock.acquire();
        assertTrue("Should acquire lock after detecting stale lock", acquired);
    }

    @Test
    public void testLockFileWrittenWithPid() throws Exception {
        fileLock.acquire();
        lockFile = new File(".etl-tool.pid");
        assertTrue("Lock file should exist", lockFile.exists());
        try (RandomAccessFile raf = new RandomAccessFile(lockFile, "r")) {
            long pid = raf.readLong();
            assertTrue("PID should be positive", pid > 0);
        }
    }
}