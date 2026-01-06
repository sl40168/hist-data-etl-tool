# ETL CLI Tool

Historical ETL tool for extracting financial data from multiple source systems (COS files and MySQL database), transforming it according to business rules, and loading into DolphinDB temporary tables.

## Project Overview

This tool processes historical market data for XBond quotes, XBond trades, and Bond Future L2 quotes across specified date ranges. It supports:

- Single-day and multi-day batch ETL execution
- Parallel data extraction from COS and MySQL
- In-memory data transformation and sorting
- Real-time progress monitoring
- Graceful error handling and memory management

## Prerequisites

- Java 1.8 or higher (Java 8 required)
- Maven 3.6.3 or higher
- Access to COS (Tencent Cloud Object Storage)
- Access to MySQL database
- Access to DolphinDB database

## Building from Source

```bash
# Clone repository
git clone <repository-url>
cd hist-data-etl-tool

# Build with Maven
mvn clean package

# The executable JAR will be at: target/etl-tool-1.0.0.jar
```

## Configuration

Create a configuration file (e.g., `config.ini`) with connection details:

```ini
[xbond]
domain=cos.ap-beijing.myqcloud.com
region=ap-beijing
bucket=my-bucket

[future]
host=localhost
port=3306
database=bond
username=etl_user
password=etl_password

[ddb]
host=localhost
port=8848
username=admin
password=123456
database=dfs://Zing_MDS
```

## Usage

### Single-Day ETL (MVP)

```bash
java -jar target/etl-tool-1.0.0.jar 20250101 20250101 config.ini
```

### Multi-Day Batch ETL

```bash
java -jar target/etl-tool-1.0.0.jar 20250101 20250131 config.ini
```

### Using Default Configuration

```bash
java -jar target/etl-tool-1.0.0.jar 20250101 20250101
```

### Providing COS Credentials

```bash
java -Dcos.secret.id=YOUR_SECRET_ID -Dcos.secret.key=YOUR_SECRET_KEY -Dcos.trust.key=YOUR_TRUST_KEY \
     -jar target/etl-tool-1.0.0.jar 20250101 20250101 config.ini
```

## Command-Line Arguments

| Argument | Required | Description | Default |
|----------|-----------|-------------|---------|
| START_DATE | Yes | Start date in YYYYMMDD format | N/A |
| END_DATE | Yes | End date in YYYYMMDD format | N/A |
| CONFIG_FILE | No | Path to INI configuration file | `config.ini` (embedded) |

## Exit Codes

| Code | Meaning |
|------|---------|
| 0 | Success |
| 1 | Invalid Arguments |
| 2 | Configuration Error |
| 3 | Connection Error |
| 4 | Data Extraction Error |
| 5 | Data Transformation Error |
| 6 | Data Loading Error |
| 7 | Memory Error |
| 8 | Concurrent Execution |
| 9 | Unexpected Error |

## Output

### Standard Output (stdout)

Progress information:
```
[2026-01-06 10:00:00] Starting ETL for date range: 20250101 - 20250131
[2026-01-06 10:00:01] Processing business date: 20250101
[2026-01-06 10:00:02] Extracting XBond Market Quote... [====================] 100%
[2026-01-06 10:00:05] Extracting XBond Trade... [========] 100%
[2026-01-06 10:00:08] Extracting Bond Future L2 Quote... [======] 100%
[2026-01-06 10:00:12] Sorting records by receive_time...
[2026-01-06 10:00:15] Loading records into DolphinDB...
[2026-01-06 10:00:30] ETL completed successfully. Total records: 2500000
```

### Standard Error (stderr)

```
Error: Failed to connect to DolphinDB - Connection refused
```

## Performance Tuning

### Memory Allocation

For typical daily volumes (5M quotes, 500K trades, 25M futures):
- Minimum heap: `-Xmx2g`
- Recommended heap: `-Xmx4g`
- Large volumes (60M+ records): `-Xmx8g`

Example:
```bash
java -Xmx4g -jar target/etl-tool-1.0.0.jar 20250101 20250101 config.ini
```

### Batch Processing

The tool uses optimized batch sizes:
- Quote records: 10,000 per batch
- Trade records: 2,000 per batch
- Progress updates: Every 10,000 records or 5 seconds (whichever first)

## Troubleshooting

### Common Issues

**Memory Error (Exit Code 7)**
- Symptom: Tool exits with "Insufficient memory" error
- Solution: Increase JVM heap size with `-Xmx` parameter

**Connection Error (Exit Code 3)**
- Symptom: Tool exits with connection failure
- Solution: Verify configuration parameters, network connectivity, and service availability

**Concurrent Execution (Exit Code 8)**
- Symptom: Tool reports "Another instance is already running"
- Solution: Wait for current instance to complete or remove stale `.etl-tool.pid` file

**No Data Found**
- Symptom: Tool logs "No data found for business date {date}"
- Expected behavior: This is normal, not an error

## License

Copyright 2026. All rights reserved.
