# Feature Specification: ETL CLI Tool

**Feature Branch**: `003-etl-cli-tool`
**Created**: January 6, 2026
**Status**: Draft
**Input**: User description: "Build ETL tool to extract data from multiple source systems (COS files and MySQL) and load into DolphinDB"

## Clarifications

### Session 2026-01-06

**Note**: These clarifications have been formalized into Functional Requirements (FR-XXX) and Edge Cases. This section is retained for historical reference.

- Q: What should happen when multiple instances of the ETL tool attempt to run simultaneously? → A: Prevent concurrent executions (enforce single instance via file lock/PID check with clear error) [→ FR-020]
- Q: What is the typical daily data volume for performance and memory targets? → A: Approximate ranges per data source: XBond Market Quote: 1-10M records, XBond Trade: 100K-1M records, Bond Future L2 Quote: 10-50M records [→ SC-001, SC-009]
- Q: How should the tool handle records with missing or null receive_time values? → A: Skip records with missing/null receive_time and log a warning for each skipped record [→ FR-021]
- Q: How should the tool behave when a business date has no data in any source system? → A: Continue processing and log a warning (treat as expected scenario, not error) [→ FR-022]
- Q: How should the tool handle situations where available memory is insufficient to cache all transformed data in memory? → A: Fail gracefully with clear error message indicating memory requirements (prevents data corruption) [→ FR-023]

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Single Day ETL Execution (Priority: P1)

A data engineer runs the ETL tool to extract, transform, and load data from multiple source systems (COS CSV files and MySQL database) into DolphinDB for a single business day. The tool validates input parameters, reads configuration files, connects to source and target systems, extracts data from all three data sources in parallel, caches the transformed data, sorts by receive time, loads into temporary DolphinDB tables, and displays progress to the console.

**Why this priority**: This is the core functionality that delivers immediate value - enabling daily data extraction and loading for business operations. Without this, the tool provides no utility.

**Independent Test**: Can be fully tested by executing the tool with a single date range (start date = end date) with valid configuration and source data, verifying all three data sources are extracted, combined, sorted by receive time, and loaded into DolphinDB with progress displayed.

**Acceptance Scenarios**:

1. **Given** the tool is invoked with valid start date (20250101), end date (20250101), and configuration file, **When** the ETL process completes, **Then** all data from XBond Market Quote, XBond Trade, and Bond Future L2 Quote sources is extracted, combined, sorted by receive_time, and loaded into DolphinDB temporary tables
2. **Given** the tool is running, **When** extracting data, **Then** progress messages are displayed to the console showing current date, data source status, and completion percentage
3. **Given** all data for the day is extracted and loaded, **When** the process completes, **Then** DolphinDB temporary tables are created, populated with sorted data, and marked for cleanup at the end

---

### User Story 2 - Multi-Day Batch ETL Execution (Priority: P1)

A data engineer runs the ETL tool to process data across a date range (e.g., 20250101 to 20250131). The tool processes each business day sequentially - completing all ETL operations for day 1 before starting day 2 - ensuring data integrity and maintainability.

**Why this priority**: This enables historical data loading and batch processing, which is essential for initial system setup and periodic catch-up operations. It extends the single-day capability to support real-world use cases.

**Independent Test**: Can be fully tested by executing the tool with a multi-day date range (e.g., 3 consecutive dates) and verifying that each day is processed completely in sequence, with each day's data extracted, transformed, sorted, and loaded before the next day begins.

**Acceptance Scenarios**:

1. **Given** the tool is invoked with start date 20250101 and end date 20250103, **When** the process runs, **Then** day 20250101 completes fully before day 20250102 begins, and day 20250102 completes fully before day 20250103 begins
2. **Given** the tool is processing multiple days, **When** an error occurs on day 20250102, **Then** the tool stops processing and does not proceed to day 20250103, and reports the error with clear context
3. **Given** multi-day processing completes successfully, **Then** all data for all dates is loaded into DolphinDB temporary tables with proper receive_time ordering within each day's dataset

---

### User Story 3 - Configuration Management (Priority: P2)

A data engineer provides connection information for source systems (COS endpoint, bucket, MySQL credentials) and target system (DolphinDB connection details) through an INI configuration file. The tool reads and validates this configuration, establishing connections to all required systems.

**Why this priority**: Configuration management is critical for flexibility and security - allowing different environments (dev, test, production) without code changes. However, it can be hardcoded initially if needed, making it P2.

**Independent Test**: Can be tested by providing a valid INI file with connection details and verifying the tool successfully connects to all source systems and target system. Also tested with invalid/missing configuration to ensure proper error handling.

**Acceptance Scenarios**:

1. **Given** a valid INI configuration file is provided, **When** the tool starts, **Then** connections are established to COS, MySQL, and DolphinDB without errors
2. **Given** no configuration file path is provided, **When** the tool starts, **Then** it uses the embedded default config.ini file
3. **Given** the configuration file contains invalid connection details, **When** the tool starts, **Then** it fails with a clear error message indicating which connection failed and why
4. **Given** the configuration file is missing required parameters, **When** the tool reads it, **Then** it fails with a clear error indicating which parameters are missing

---

### User Story 4 - DolphinDB Temporary Table Lifecycle (Priority: P2)

The ETL tool automatically creates temporary stream tables in DolphinDB at the start of execution (one table per data source) and deletes them when processing completes. This ensures clean resource management and prevents leftover data from previous runs.

**Why this priority**: Proper resource management prevents database bloat and confusion between ETL runs. While important, the core value is in data extraction/loading, making this P2.

**Independent Test**: Can be tested by running the ETL tool and verifying that temporary tables exist during execution and are cleaned up after completion, both in success and failure scenarios.

**Acceptance Scenarios**:

1. **Given** the ETL tool starts execution, **When** initialization completes, **Then** three temporary stream tables exist in DolphinDB (one for each data source)
2. **Given** the ETL process completes successfully, **When** the tool finishes, **Then** all temporary tables are deleted from DolphinDB
3. **Given** the ETL process encounters an error during execution, **When** the tool terminates, **Then** all temporary tables are deleted from DolphinDB (cleanup on error)

---

### Edge Cases

- **No data in date range**: Tool continues processing and logs a warning (treats as expected scenario, not error). Each data source is queried independently; if all sources return zero records for a business date, the tool logs "Warning: No data found for business date {date} in any source system" and proceeds to next day.
- **Connection failures**: Tool fails gracefully with clear error message indicating which connection failed and why. No retry logic is implemented; the tool immediately terminates with exit code 3 (Connection Error) and displays: "Error: Failed to connect to {system} - {specific error reason}". System is one of: COS, MySQL, or DolphinDB.
- **Malformed or missing CSV data**: Tool logs the malformed record details (file path, line number, reason) and skips the record. Processing continues with next record. Error format: "Warning: Skipping malformed record at {file}:{line} - {reason}". For CSV parsing errors, the reason includes validation details (e.g., "missing required field: receive_time").
- **Concurrent executions**: Tool prevents concurrent executions by enforcing single instance via file lock or PID check. On startup, tool attempts to acquire exclusive lock on `.etl-tool.pid` file. If lock cannot be acquired, tool terminates with exit code 8 (Concurrent Execution) and displays: "Error: Another instance of the ETL tool is already running (PID: {pid}). Only one instance can run at a time."
- **Insufficient memory**: Tool performs pre-flight memory check before extraction. If estimated memory requirement exceeds 90% of available JVM heap memory, tool fails gracefully with exit code 7 (Memory Error) and displays: "Error: Insufficient memory. Required: {required} MB, Available: {available} MB. Increase JVM heap size with -Xmx parameter."
- **Timezone handling**: Source data timestamps and business dates are in the same timezone (Asia/Shanghai). No timezone conversion is required. Business date YYYYMMDD format is directly comparable with date portions of timestamps.
- **Missing receive_time field**: Tool skips records with missing/null receive_time and logs a warning for each skipped record. Warning format: "Warning: Skipping record with missing/null receive_time in {source} (file: {file}, line: {line})". This behavior applies to all three data sources.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST accept two mandatory parameters: start date and end date in YYYYMMDD format
- **FR-002**: System MUST accept one optional parameter: path to INI configuration file (defaulting to embedded config.ini if not provided)
- **FR-003**: System MUST validate that start date is not after end date and both dates are in valid YYYYMMDD format. Validation criteria:
  - String must be exactly 8 characters
  - Characters at positions 0-3 must be digits forming valid year (1900-2100)
  - Characters at positions 4-5 must be digits forming valid month (01-12)
  - Characters at positions 6-7 must be digits forming valid day (01-31)
  - Invalid dates (e.g., 20250230, 20251301) must be rejected with clear error message: "Error: Invalid date format {date}. Expected format: YYYYMMDD (e.g., 20250101)"
- **FR-004**: System MUST read INI configuration file to retrieve connection details for COS (endpoint, bucket), MySQL (host, port, database, username, password), and DolphinDB
- **FR-005**: System MUST establish connections to all source systems and target system before starting ETL process
- **FR-006**: System MUST process ETL day by day, completing all operations for the current day before proceeding to the next day
- **FR-007**: System MUST pass the current processing date as BUSINESS_DATE parameter to all data source extractors
- **FR-008**: System MUST extract data from all three data sources (XBond Market Quote, XBond Trade, Bond Future L2 Quote) in parallel for each business day
- **FR-009**: System MUST extract XBond Market Quote data from COS at path /AllPriceDepth/{BUSINESS_DATE}/*.csv
- **FR-010**: System MUST extract XBond Trade data from COS at path /XbondCfetsDeal/{BUSINESS_DATE}/*.csv
- **FR-011**: System MUST extract Bond Future L2 Quote data from MySQL table bond.fut_tick with filter condition trading_date = {BUSINESS_DATE}
- **FR-012**: System MUST cache all transformed data in memory until all data sources for the current day are extracted
- **FR-013**: System MUST sort all cached data by the receive_time field before loading into target system
- **FR-014**: System MUST load sorted data into target system in receive_time order
- **FR-015**: System MUST create three temporary stream tables in DolphinDB at the start of execution (one for each data source)
- **FR-016**: System MUST delete all temporary stream tables in DolphinDB at the end of execution (including on error)
- **FR-017**: System MUST display execution progress to console including current date being processed, extraction status for each data source, and completion percentage
- **FR-018**: System MUST halt execution and report clear error messages if any connection fails or data extraction encounters errors
- **FR-024**: System MUST terminate with exit code 3 (Connection Error) when connection to COS, MySQL, or DolphinDB fails, displaying specific error reason and system name. No retry logic is implemented; connection failures cause immediate tool termination to prevent data corruption.
- **FR-025**: System MUST log malformed CSV records with file path, line number, and validation reason, then skip the record and continue processing
- **FR-019**: System MUST validate that all extracted data contains the receive_time field and handle missing/null values appropriately
- **FR-021**: System MUST skip records with missing or null receive_time values and log a warning for each skipped record
- **FR-020**: System MUST prevent concurrent executions by enforcing single instance via file lock or PID check, and report a clear error message if another instance is already running
- **FR-022**: System MUST continue processing and log a warning when a business date contains no data in any source system (treat as expected scenario, not error)
- **FR-023**: System MUST fail gracefully with a clear error message indicating memory requirements when available memory is insufficient to cache all transformed data
- **FR-026**: System MUST assume all timestamps and business dates are in Asia/Shanghai timezone; no timezone conversion is required

### Key Entities *(include if feature involves data)*

- **Business Date**: The date for which data is being extracted (format: YYYYMMDD for CLI input/storage; internally represented as LocalDate), used to parameterize data source queries and file paths
- **ETL Job**: Represents a single execution of the tool with a specified date range, managing the lifecycle of connections, data extraction, transformation, and loading
- **XBond Market Quote Data**: Financial market quote data extracted from CSV files in COS containing price and depth information with receive_time field (typical volume: 1-10M records per day)
- **XBond Trade Data**: Financial trade data extracted from CSV files in COS containing transaction information with receive_time field (typical volume: 100K-1M records per day)
- **Bond Future L2 Quote Data**: Level 2 quote data for bond futures extracted from MySQL table containing tick-level data with receive_time field (typical volume: 10-50M records per day)
- **Temporary Stream Table**: Short-lived tables in DolphinDB created for each ETL job to receive loaded data, deleted after job completion
- **Configuration**: Connection settings and parameters stored in INI format for COS, MySQL, and DolphinDB systems

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully complete a single-day ETL process for all three data sources within 5 minutes for typical daily data volumes (XBond Market Quote: 5M records, XBond Trade: 500K records, Bond Future L2 Quote: 25M records). Performance should scale linearly: processing time for 2x volume should not exceed 2x baseline time.
- **SC-002**: System can process and load data from all three sources (COS CSVs and MySQL) without data loss or corruption
- **SC-003**: 100% of loaded records are correctly ordered by receive_time field within each day's dataset
- **SC-004**: Temporary tables are created and deleted successfully in 100% of ETL job executions (both success and failure scenarios)
- **SC-005**: Error messages clearly indicate the source of failure (connection, data extraction, validation, or loading) in 100% of failure scenarios
- **SC-006**: Progress displays update at least once per data source per day, providing visibility into extraction and loading status. Update frequency: progress must be displayed at least every 10,000 records processed or every 5 seconds elapsed, whichever occurs first.
- **SC-007**: Multi-day processing processes each day sequentially without overlapping or interleaving data between days
- **SC-008**: Configuration validation catches 100% of missing or invalid parameters before attempting connections
- **SC-009**: Memory usage remains within 90% of available JVM heap memory during normal operation with typical daily data volumes (XBond Market Quote: 5M records, XBond Trade: 500K records, Bond Future L2 Quote: 25M records). Recommended JVM heap size: -Xmx4g for typical volumes. Pre-flight check must fail gracefully if estimated memory exceeds 90% of -Xmx setting.
