# CLI Interface Specification

**Feature**: 003-etl-cli-tool
**Date**: 2026-01-06

This document defines the CLI interface contract for the ETL CLI Tool.

---

## Command Overview

```bash
java -jar etl-tool.jar <START_DATE> <END_DATE> [CONFIG_FILE]
```

### Parameters

| Parameter | Type | Required | Description | Default |
|-----------|------|----------|-------------|---------|
| `START_DATE` | String | Yes | Start business date in YYYYMMDD format | N/A |
| `END_DATE` | String | Yes | End business date in YYYYMMDD format | N/A |
| `CONFIG_FILE` | String | No | Path to INI configuration file | `config.ini` (embedded) |

---

## Arguments

### Positional Arguments

#### `START_DATE`

The start date of the ETL job in `YYYYMMDD` format.

**Constraints**:
- Must be exactly 8 digits
- Must represent a valid date
- Must not be after `END_DATE`

**Examples**:
- `20250101` - January 1, 2025
- `20261231` - December 31, 2026

---

#### `END_DATE`

The end date of the ETL job in `YYYYMMDD` format.

**Constraints**:
- Must be exactly 8 digits
- Must represent a valid date
- Must not be before `START_DATE`

**Examples**:
- `20250101` - Same day as start date (single-day ETL)
- `20250131` - January 31, 2025 (multi-day ETL)

---

### Optional Arguments

#### `CONFIG_FILE`

Path to the INI configuration file containing connection details for COS, MySQL, and DolphinDB.

**Constraints**:
- Must be a valid file path
- File must be readable
- File must be in INI format with required sections

**Default**: Embedded `config.ini` in the application resources

**Examples**:
- `production.ini` - Configuration file in current directory
- `/etc/etl/config.ini` - Configuration file in system directory
- `C:\etl\config.ini` - Configuration file on Windows (absolute path)

---

## Exit Codes

| Exit Code | Meaning | Description |
|-----------|---------|-------------|
| `0` | Success | ETL job completed successfully |
| `1` | Invalid Arguments | Invalid command-line arguments provided |
| `2` | Configuration Error | Missing or invalid configuration file |
| `3` | Connection Error | Failed to connect to one or more systems (COS, MySQL, DolphinDB) |
| `4` | Data Extraction Error | Failed to extract data from source systems |
| `5` | Data Transformation Error | Failed to transform extracted data |
| `6` | Data Loading Error | Failed to load data into DolphinDB |
| `7` | Memory Error | Insufficient memory to process data |
| `8` | Concurrent Execution | Another instance of the tool is already running |
| `9` | Unexpected Error | Unexpected error during execution |

---

## Output Format

### Standard Output (stdout)

The tool outputs human-readable progress information and success messages to stdout.

#### Progress Output

Progress is displayed in real-time during execution with the following format:

```
[YYYY-MM-DD HH:mm:ss] Initializing ETL job: 20250101 to 20250131
[YYYY-MM-DD HH:mm:ss] Loading configuration from: config.ini
[YYYY-MM-DD HH:mm:ss] Establishing connections...
  ✓ COS connection established
  ✓ MySQL connection established
  ✓ DolphinDB connection established
[YYYY-MM-DD HH:mm:ss] Creating temporary stream tables...
  ✓ xbond_quote_stream_temp
  ✓ xbond_trade_stream_temp
  ✓ market_price_stream_temp
  ✓ fut_market_price_stream_temp
[YYYY-MM-DD HH:mm:ss] Processing: 20250101
  XBond Market Quote: [████████░░] 80% (800K/1M records) - 45s
  XBond Trade:       [██████████] 100% (500K/500K records) - 20s
  Bond Future L2:    [████░░░░░░] 40% (20M/50M records) - 120s
  Overall:           [████████░░] 73%
[YYYY-MM-DD HH:mm:ss] Processing: 20250102
  XBond Market Quote: [██████████] 100% (900K/900K records) - 50s
  XBond Trade:       [██████████] 100% (450K/450K records) - 18s
  Bond Future L2:    [██████████] 100% (48M/48M records) - 125s
  Overall:           [██████████] 100%
...
[YYYY-MM-DD HH:mm:ss] ETL job completed successfully
  Total days processed: 31
  Total records loaded: 1,875,500,000
  Total execution time: 2h 15m 30s
```

#### Success Output

On successful completion, the tool outputs a summary:

```
[YYYY-MM-DD HH:mm:ss] ETL job completed successfully
  Total days processed: 31
  Total records loaded: 1,875,500,000
  Total execution time: 2h 15m 30s
```

---

### Standard Error (stderr)

The tool outputs error messages and diagnostics to stderr.

#### Error Output Format

Errors are formatted as:

```
ERROR: [ERROR_CODE] [ERROR_MESSAGE]
  Context: [ADDITIONAL_CONTEXT]
  Suggestion: [SUGGESTION]
```

#### Error Examples

**Invalid Arguments**:
```
ERROR: ETL-001 Invalid arguments: Start date '20250132' is not a valid date
  Context: Provided start date: 20250132, end date: 20250131
  Suggestion: Ensure start and end dates are in YYYYMMDD format and represent valid dates
```

**Configuration Error**:
```
ERROR: ETL-002 Configuration file not found: production.ini
  Context: Config file path: production.ini
  Suggestion: Provide a valid configuration file path or omit to use embedded config.ini
```

**Connection Error**:
```
ERROR: ETL-003 Failed to connect to MySQL
  Context: Host: localhost, Port: 3306, Database: bond, Username: etl_user
  Suggestion: Verify MySQL server is running and credentials are correct
```

**Data Extraction Error**:
```
ERROR: ETL-004 Failed to extract XBond Market Quote data
  Context: Business date: 20250101, COS path: /AllPriceDepth/2025-01-01/*.csv
  Suggestion: Verify COS bucket and path are correct, and files exist
```

**Memory Error**:
```
ERROR: ETL-007 Insufficient memory
  Context: Required: 500MB, Available: 400MB, Threshold: 90%
  Suggestion: Increase available memory or reduce date range
```

**Concurrent Execution**:
```
ERROR: ETL-008 ETL tool is already running
  Context: PID file: .etl-tool.pid
  Suggestion: Wait for the current instance to complete or manually remove the PID file if the previous instance crashed
```

**Unexpected Error**:
```
ERROR: ETL-009 Unexpected error during execution
  Context: NullPointerException at com.histdata.etl.loader.DolphinDbLoader.load(DolphinDbLoader.java:123)
  Suggestion: Check logs for detailed stack trace
```

---

## JVM Parameters

The tool accepts JVM parameters for configuration and security:

### Trust Key Parameter

**Parameter**: `-Dcos.trust.key`

**Description**: Trust key for COS authentication

**Example**:
```bash
java -Dcos.trust.key=your_trust_key -jar etl-tool.jar 20250101 20250101
```

---

## Usage Examples

### Single-Day ETL with Default Configuration

```bash
java -jar etl-tool.jar 20250101 20250101
```

### Multi-Day ETL with Default Configuration

```bash
java -jar etl-tool.jar 20250101 20250131
```

### Single-Day ETL with Custom Configuration

```bash
java -jar etl-tool.jar 20250101 20250101 production.ini
```

### Multi-Day ETL with Custom Configuration and Trust Key

```bash
java -Dcos.trust.key=your_trust_key -jar etl-tool.jar 20250101 20250131 /etc/etl/config.ini
```

### With Increased Memory Allocation

```bash
java -Xmx4g -jar etl-tool.jar 20250101 20250131
```

---

## Configuration File Format

The INI configuration file must contain the following sections:

### `[xbond]` Section

```ini
[xbond]
domain = cos.ap-beijing.myqcloud.com
region = ap-beijing
bucket = my-bucket
```

### `[future]` Section

```ini
[future]
host = localhost
port = 3306
database = bond
username = etl_user
password = etl_password
```

### `[ddb]` Section

```ini
[ddb]
host = localhost
port = 8848
username = admin
password = admin
```

---

## Logging

The tool logs detailed execution information to a log file (default: `etl-tool.log`) in addition to stdout/stderr output.

### Log Format

```
YYYY-MM-DD HH:mm:ss.SSS [LEVEL] [THREAD] [CLASS] - MESSAGE
```

### Log Levels

| Level | Description |
|-------|-------------|
| `DEBUG` | Detailed debugging information |
| `INFO` | General information about execution progress |
| `WARN` | Warning messages for non-critical issues |
| `ERROR` | Error messages for failures |

---

## Performance Characteristics

### Execution Time

- **Single-day ETL**: ~5 minutes for typical daily volumes
- **Multi-day ETL**: ~5 minutes per day (sequential processing)

### Memory Usage

- **Typical daily volumes** (1-10M quotes + 100K-1M trades + 10-50M futures): ~50-300MB
- **Memory threshold**: 90% of available system memory

### Data Volume

- **XBond Market Quote**: 1-10M records per day
- **XBond Trade**: 100K-1M records per day
- **Bond Future L2 Quote**: 10-50M records per day
- **Total**: ~11-61M records per day

---

## Error Recovery

### Automatic Recovery

The tool does not support automatic recovery. If an error occurs, the tool stops processing and reports the error.

### Manual Recovery

To resume after a failed ETL job:
1. Investigate and fix the error (e.g., fix configuration, increase memory)
2. Re-run the tool from the failed date onwards
3. Temporary tables are cleaned up automatically on error, so no manual cleanup is required

### Data Integrity

The tool ensures data integrity through:
- Single-instance execution (prevents concurrent runs)
- Day-by-day processing (ensures each day completes before the next)
- Transaction-like behavior (temporary tables are cleaned up on error)
- Validation at each stage (connection, extraction, transformation, loading)

---

## Version Information

To display version information, run:

```bash
java -jar etl-tool.jar --version
```

**Output**:
```
ETL CLI Tool v1.0.0
Built: 2026-01-06
Java: 1.8
```

---

## Help Information

To display help information, run:

```bash
java -jar etl-tool.jar --help
```

**Output**:
```
ETL CLI Tool - Extract, Transform, and Load financial data

Usage: java -jar etl-tool.jar <START_DATE> <END_DATE> [CONFIG_FILE]

Arguments:
  START_DATE    Start business date in YYYYMMDD format (required)
  END_DATE      End business date in YYYYMMDD format (required)
  CONFIG_FILE   Path to INI configuration file (optional, default: config.ini)

Options:
  --version     Display version information
  --help        Display this help message

Examples:
  java -jar etl-tool.jar 20250101 20250101
  java -jar etl-tool.jar 20250101 20250131 production.ini

Exit Codes:
  0 - Success
  1 - Invalid Arguments
  2 - Configuration Error
  3 - Connection Error
  4 - Data Extraction Error
  5 - Data Transformation Error
  6 - Data Loading Error
  7 - Memory Error
  8 - Concurrent Execution
  9 - Unexpected Error
```
