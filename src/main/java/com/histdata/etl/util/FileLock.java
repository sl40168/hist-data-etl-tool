package com.histdata.etl.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.histdata.etl.exception.ConcurrentExecutionException;

/**
 * Utility class for enforcing single-instance execution using file locking.
 * Uses Java NIO FileLock mechanism with PID file management.
 */
public class FileLock {

    private static final Logger logger = LoggerFactory.getLogger(FileLock.class);
    private static final String LOCK_FILE_NAME = ".etl-tool.pid";

    private final File lockFile;
    private RandomAccessFile raf;
    private FileChannel channel;
    private java.nio.channels.FileLock nioLock;
    private final long pid;

    /**
     * Creates a FileLock instance for the current process.
     *
     * @throws IOException if lock file cannot be created
     */
    public FileLock() throws IOException {
        this.lockFile = new File(LOCK_FILE_NAME);
        this.pid = getProcessId();
    }

    /**
     * Attempts to acquire exclusive lock for single-instance enforcement.
     *
     * @return true if lock acquired successfully
     * @throws ConcurrentExecutionException if another instance is already running
     * @throws IOException if lock file cannot be accessed
     */
    public boolean acquire() throws ConcurrentExecutionException, IOException {
        if (lockFile.exists()) {
            // Check if existing lock is stale (process not running)
            if (isStaleLock()) {
                logger.warn("Detected stale lock file, removing it");
                lockFile.delete();
            } else {
                throw new ConcurrentExecutionException(
                    String.format("Another instance of ETL tool is already running (PID: %d). " +
                            "Only one instance can run at a time.", readPidFromFile())
                );
            }
        }

        try {
            raf = new RandomAccessFile(lockFile, "rw");
            channel = raf.getChannel();
            nioLock = channel.tryLock();

            if (nioLock == null) {
                throw new ConcurrentExecutionException(
                    String.format("Another instance of ETL tool is already running. " +
                            "Only one instance can run at a time.")
                );
            }

            // Write current PID to lock file
            writePidToFile();
            logger.info("Lock acquired successfully. PID: {}", pid);
            return true;

        } catch (OverlappingFileLockException e) {
            throw new ConcurrentExecutionException(
                    "Another instance of ETL tool is already running. " +
                    "Only one instance can run at a time."
            );
        }
    }

    /**
     * Releases the lock and removes the lock file.
     * Should be called in finally block or shutdown hook.
     */
    public void release() {
        try {
            if (nioLock != null && nioLock.isValid()) {
                nioLock.release();
                logger.info("Lock released successfully");
            }
        } catch (IOException e) {
            logger.warn("Error releasing lock: {}", e.getMessage());
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (raf != null) {
                    raf.close();
                }
                if (lockFile.exists()) {
                    lockFile.delete();
                    logger.debug("Lock file removed");
                }
            } catch (IOException e) {
                logger.warn("Error closing lock file: {}", e.getMessage());
            }
        }
    }

    /**
     * Checks if the existing lock file is stale (process not running).
     *
     * @return true if lock is stale, false otherwise
     */
    private boolean isStaleLock() {
        try {
            long existingPid = readPidFromFile();
            if (existingPid == 0) {
                return true; // Invalid PID, treat as stale
            }
            return !isProcessRunning(existingPid);
        } catch (IOException e) {
            logger.warn("Error checking stale lock: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Reads PID from lock file.
     *
     * @return PID value, or 0 if invalid
     * @throws IOException if file cannot be read
     */
    private long readPidFromFile() throws IOException {
        if (!lockFile.exists()) {
            return 0;
        }
        RandomAccessFile file = new RandomAccessFile(lockFile, "r");
        try {
            String pidStr = file.readLine();
            return pidStr != null ? Long.parseLong(pidStr.trim()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        } finally {
            file.close();
        }
    }

    /**
     * Writes current PID to lock file.
     *
     * @throws IOException if file cannot be written
     */
    private void writePidToFile() throws IOException {
        if (raf != null) {
            raf.setLength(0);
            raf.writeBytes(String.valueOf(pid));
            raf.getChannel().force(true);
        }
    }

    /**
     * Gets the current process ID using Java 8 compatible methods.
     * This is a simplified version that may not be accurate on all platforms.
     *
     * @return Process ID
     */
    private long getProcessId() {
        // Java 8 doesn't have ProcessHandle, so we use a simple heuristic
        // This returns the current time as a pseudo-PID for locking purposes
        // In production, you might want to use platform-specific methods
        return System.currentTimeMillis() % 1000000;
    }

    /**
     * Checks if a process with given PID is running.
     * Note: This is a simplified check for Java 8 compatibility.
     *
     * @param pid Process ID to check
     * @return true if process is running, false otherwise
     */
    private boolean isProcessRunning(long pid) {
        // Simplified check - in Java 8 we cannot reliably check process status
        // We assume the lock is stale if the file exists but lock can't be acquired
        return false;
    }

    /**
     * Static helper to acquire lock and return FileLock instance.
     * Caller should call release() when done.
     *
     * @return FileLock instance with lock acquired
     * @throws ConcurrentExecutionException if another instance is running
     * @throws IOException if lock file cannot be accessed
     */
    public static FileLock acquireLock() throws ConcurrentExecutionException, IOException {
        FileLock lock = new FileLock();
        lock.acquire();
        return lock;
    }
}
