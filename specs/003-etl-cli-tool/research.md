# Research Findings: ETL CLI Tool

**Feature**: 003-etl-cli-tool
**Date**: 2026-01-06

This document consolidates research findings for unknowns identified in the Technical Context of the implementation plan.

---

## Research Topics

### 1. INI Parsing Library for Java 8

**Decision**: Use **Apache Commons Configuration** (version 2.x) or **ini4j** (version 0.5.x)

**Rationale**:
- **Apache Commons Configuration** is well-maintained, part of the Apache Commons ecosystem, provides comprehensive INI parsing capabilities, and is Java 8 compatible
- **ini4j** is a lightweight, specialized INI parsing library with active maintenance and good Java 8 compatibility
- Both libraries are well-known, open-source components that meet constitutional requirements

**Alternatives Considered**:
- Custom INI parser: Rejected due to maintenance burden and potential bugs
- Other smaller libraries: Rejected due to less active maintenance or fewer features

**Recommendation**: Use **Apache Commons Configuration 2.9.0** for comprehensive configuration management with validation capabilities

---

### 2. CSV Parsing Library

**Decision**: Use **Apache Commons CSV** (version 1.10.x or 1.11.x)

**Rationale**:
- Part of Apache Commons ecosystem, well-maintained and widely adopted
- High-performance streaming API suitable for large files (millions of rows)
- Java 8 compatible
- Supports various CSV formats and provides robust error handling
- Memory-efficient design can process files line-by-line without loading entire file into memory

**Alternatives Considered**:
- OpenCSV: Good alternative but Commons CSV is preferred due to ecosystem consistency
- Super CSV: Less actively maintained
- Custom parser: Rejected due to edge cases and maintenance burden

**Recommendation**: Use **Apache Commons CSV 1.10.0** for CSV parsing

---

### 3. MySQL JDBC Driver Compatibility

**Decision**: Use **MySQL Connector/J** version 8.0.x (latest 8.0.33+)

**Rationale**:
- Official MySQL JDBC driver with active maintenance
- Java 8 compatible (requires Java 8+)
- Supports connection pooling for efficient resource management
- Well-documented with comprehensive features
- Widely adopted in production environments

**Alternatives Considered**:
- MariaDB JDBC driver: Compatible but MySQL Connector/J is preferred for MySQL-specific optimizations
- c3p0/HikariCP: These are connection pool libraries, not JDBC drivers (can be used in conjunction)

**Recommendation**: Use **MySQL Connector/J 8.0.33** for MySQL connectivity, potentially combined with **HikariCP** for connection pooling if needed

---

### 4. DolphinDB Java API Best Practices

**Decision**: Use DolphinDB Java API 3.00.0.2 as specified in plan, following best practices for bulk loading

**Rationale**:
- Official DolphinDB Java API with version 3.00.0.2 specified in requirements
- Comprehensive documentation available at https://docs.dolphindb.cn/zh/javadoc/quickstart.html
- Supports bulk data insertion with `table.append!()` method
- Provides connection management and error handling capabilities

**Best Practices Identified**:
- Use `BasicTable` or `BasicVector` for efficient data transfer
- Batch insert records (e.g., 10,000 records per batch) to reduce network overhead
- Reuse connections across operations within the same ETL job
- Implement proper connection cleanup in try-finally or try-with-resources blocks
- Use `setStreamTableTimestamp()` for proper timestamp handling in stream tables
- Handle `COMException` for connection errors and retry logic

**Alternatives Considered**:
- None - DolphinDB API is required by the specification

**Recommendation**: Follow DolphinDB Java API documentation, use batch inserts with 10,000 records per batch, implement connection pooling if processing multiple days

---

### 5. COS SDK Connection Patterns

**Decision**: Use **COS Java SDK 5.6.3** as specified in requirements, with streaming download for large files

**Rationale**:
- Version 5.6.3 specified in requirements
- Official Tencent Cloud COS SDK with active maintenance
- Supports prefix-based object filtering for finding CSV files by date
- Provides streaming download capabilities to avoid loading entire files into memory

**Best Practices Identified**:
- Use `listObjects()` with `prefix` parameter to filter CSV files by business date
- Use `GetObjectRequest` with streaming `getObjectContent()` to download large files
- Implement retry logic for transient network failures
- Use multi-threading for parallel downloads of multiple CSV files
- Validate file integrity after download (check file size, MD5 if available)
- Use connection pooling for COS client

**Key API Methods**:
- `cosClient.listObjects(bucketName, prefix)`: List objects matching prefix
- `cosClient.getObject(bucketName, key)`: Download object
- `getObjectMetadata()`: Get file metadata before download for validation

**Alternatives Considered**:
- None - COS SDK is required by the specification

**Recommendation**: Use COS SDK 5.6.3 with streaming downloads, implement parallel download for multiple CSV files, add retry logic for network failures

---

### 6. Memory Management Strategies for Large Datasets

**Decision**: Implement streaming processing with configurable memory limits and graceful degradation

**Rationale**:
- Typical daily volume: 1-10M quote records + 100K-1M trade records + 10-50M future records = ~11-61M total records
- Estimated memory requirement: ~50-300MB assuming ~5 bytes per record (minimal)
- Caching all transformed data in memory before sorting is required by specification
- Need to handle OOM scenarios gracefully

**Memory Management Strategies**:

1. **Streaming CSV Parsing**: Process CSV files line-by-line without loading entire file
2. **Efficient Data Structures**: Use compact data structures for transformed records (primitive arrays vs. objects)
3. **Memory Thresholds**: Implement pre-flight memory check before ETL job starts
4. **Graceful Failure**: Clear error message with memory requirements if insufficient
5. **Configurable Batching**: Sort and load in batches if memory is constrained (alternative design)

**Implementation Approach**:
- Calculate estimated memory requirement based on record count
- Check available system memory using `Runtime.getRuntime()`
- If estimated > 90% of available memory, fail with clear error
- If estimated < 90%, proceed with in-memory caching
- Consider off-heap storage if needed for very large datasets

**Code Pattern**:
```java
long estimatedMemory = (quoteRecords + tradeRecords + futureRecords) * RECORD_SIZE_BYTES;
long availableMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory();
if (estimatedMemory > availableMemory * 0.9) {
    throw new InsufficientMemoryException(
        String.format("Insufficient memory. Required: %dMB, Available: %dMB",
            estimatedMemory / (1024 * 1024), availableMemory / (1024 * 1024)));
}
```

**Alternatives Considered**:
- External sorting (disk-based): Rejected due to complexity and performance overhead
- Database-side sorting: Rejected as specification requires sorting by receive_time before loading
- Streaming load without sorting: Rejected as specification requires sorted data

**Recommendation**: Implement in-memory caching with pre-flight memory check and graceful failure on OOM

---

### 7. Progress Monitoring Patterns for CLI

**Decision**: Use **Console progress bars** with percentage completion and status updates

**Rationale**:
- Standard CLI practice for long-running operations
- Provides visibility into ETL progress
- Multiple data sources require individual status tracking
- Multi-day processing requires day-level progress

**Progress Display Format**:
```
[YYYY-MM-DD HH:mm:ss] Processing: 20250101
  XBond Market Quote: [████████░░] 80% (800K/1M records)
  XBond Trade:       [██████████] 100% (500K/500K records)
  Bond Future L2:    [████░░░░░░] 40% (20M/50M records)
  Overall:           [████████░░] 73%
```

**Libraries Considered**:
- **Simple Console Output**: Use `System.out.print()` with carriage return (`\r`) for in-place updates
- **Progressbar4j**: External library but adds dependency
- **Apache Commons CLI**: Good for argument parsing, not progress display

**Recommendation**: Use simple console output with carriage return for in-place progress updates, implement custom progress monitoring class

---

### 8. Single-Instance Enforcement (File Locking/PID Check)

**Decision**: Use **Java NIO FileLock** mechanism with PID file creation

**Rationale**:
- Platform-independent solution (works on Linux and Windows)
- Standard Java API, no external dependencies
- Automatic lock release on JVM termination (most scenarios)
- Simple to implement and reliable

**Implementation Approach**:
1. Create PID file in working directory (e.g., `.etl-tool.pid`)
2. Acquire exclusive file lock on PID file using `FileChannel.lock()`
3. Write current process PID to PID file
4. On startup, attempt to acquire lock - fail if lock is held
5. Implement clean shutdown hook to release lock

**Code Pattern**:
```java
File pidFile = new File(".etl-tool.pid");
RandomAccessFile raf = new RandomAccessFile(pidFile, "rw");
FileChannel channel = raf.getChannel();
FileLock lock = channel.tryLock();

if (lock == null) {
    throw new ConcurrentExecutionException("ETL tool is already running. PID file: " + pidFile.getAbsolutePath());
}

// Write PID
raf.writeBytes(String.valueOf(ProcessHandle.current().pid()));

// Register shutdown hook
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    try {
        lock.release();
        channel.close();
        raf.close();
        pidFile.delete();
    } catch (IOException e) {
        logger.warn("Failed to release lock", e);
    }
}));
```

**Alternatives Considered**:
- Socket-based locking: More complex, requires port management
- Database-based locking: External dependency on database
- Process name checking: Platform-specific and unreliable

**Edge Cases**:
- Force kill of JVM: Lock file may remain, need stale lock detection
- File system issues: Handle IOException gracefully
- Permission issues: Clear error message

**Recommendation**: Use Java NIO FileLock with PID file, add stale lock detection (check if PID is still running)

---

## Summary of Technology Choices

| Component | Technology | Version | Rationale |
|-----------|-----------|---------|-----------|
| Build Tool | Maven | 3.6.3 | Constitutional requirement |
| Language | Java | 1.8 | Constitutional requirement |
| INI Parsing | Apache Commons Configuration | 2.9.0 | Well-maintained, ecosystem consistency |
| CSV Parsing | Apache Commons CSV | 1.10.0 | High-performance streaming API |
| MySQL JDBC | MySQL Connector/J | 8.0.33 | Official driver, Java 8 compatible |
| DolphinDB API | dolphindb-javaapi | 3.00.0.2 | Required by specification |
| COS SDK | cos_api | 5.6.3 | Required by specification |
| Logging | SLF4J + Logback | 1.7.x / 1.2.x | Constitutional requirement |
| JSON (if needed) | Jackson | 2.13.x | Well-maintained, Java 8 compatible |

---

## Next Steps

With research complete, proceed to Phase 1: Design & Contracts
- Generate data-model.md with entity definitions
- Generate contracts/ directory with CLI interface specification
- Generate quickstart.md with setup and usage instructions
- Update agent context with new technology stack
- Re-evaluate Constitution Check to ensure design alignment
