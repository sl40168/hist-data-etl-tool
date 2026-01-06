# Quickstart Guide: ETL CLI Tool

**Feature**: 003-etl-cli-tool
**Date**: 2026-01-06

This guide provides step-by-step instructions for setting up and running the ETL CLI Tool.

---

## Prerequisites

### System Requirements

- **Java**: JDK 1.8 or higher (Java 8 required)
- **Maven**: 3.6.3 or higher (for building from source)
- **Memory**: Minimum 512MB RAM, recommended 4GB+ for processing large datasets
- **Disk Space**: Minimum 10GB free space (for downloaded CSV files and logs)
- **Operating System**: Linux or Windows

### External Systems

- **COS (Cloud Object Storage)**: Access to COS bucket containing historical CSV files
- **MySQL**: Access to MySQL database containing future tick data
- **DolphinDB**: Access to DolphinDB server for loading data

---

## Installation

### Option 1: Build from Source

#### 1. Clone Repository

```bash
git clone https://github.com/your-org/hist-data-etl-tool.git
cd hist-data-etl-tool
```

#### 2. Build with Maven

```bash
mvn clean package
```

This will generate the executable JAR file at `target/etl-tool-1.0.0.jar`.

#### 3. Verify Installation

```bash
java -jar target/etl-tool-1.0.0.jar --version
```

Expected output:
```
ETL CLI Tool v1.0.0
Built: 2026-01-06
Java: 1.8
```

---

### Option 2: Use Pre-built JAR

1. Download the pre-built JAR file from releases
2. Place JAR file in your working directory
3. Verify installation:

```bash
java -jar etl-tool-1.0.0.jar --version
```

---

## Configuration

### Step 1: Create Configuration File

Create an INI configuration file (e.g., `config.ini`) in your working directory:

```ini
[xbond]
domain = cos.ap-beijing.myqcloud.com
region = ap-beijing
bucket = your-bucket-name

[future]
host = localhost
port = 3306
database = bond
username = etl_user
password = etl_password

[ddb]
host = localhost
port = 8848
username = admin
password = admin
```

### Step 2: Configure COS Credentials

COS credentials are passed via JVM parameters for security:

```bash
java -Dcos.secret.id=YOUR_SECRET_ID \
     -Dcos.secret.key=YOUR_SECRET_KEY \
     -Dcos.trust.key=YOUR_TRUST_KEY \
     -jar etl-tool.jar <START_DATE> <END_DATE> [CONFIG_FILE]
```

**Security Note**: Never hardcode credentials in configuration files. Use environment variables or secure credential managers.

### Step 3: Validate Configuration

Test connectivity to all systems:

```bash
# You can create a simple test script to verify connections
# This is just an example - actual test script would be part of the tool
```

---

## Running the ETL Tool

### Basic Usage

#### Single-Day ETL

Process data for a single business date:

```bash
java -Dcos.secret.id=YOUR_SECRET_ID \
     -Dcos.secret.key=YOUR_SECRET_KEY \
     -Dcos.trust.key=YOUR_TRUST_KEY \
     -jar etl-tool.jar 20250101 20250101 config.ini
```

#### Multi-Day ETL

Process data for a date range:

```bash
java -Dcos.secret.id=YOUR_SECRET_ID \
     -Dcos.secret.key=YOUR_SECRET_KEY \
     -Dcos.trust.key=YOUR_TRUST_KEY \
     -jar etl-tool.jar 20250101 20250131 config.ini
```

### Advanced Usage

#### Increase Memory Allocation

For processing large datasets, increase heap memory:

```bash
java -Xmx4g \
     -Dcos.secret.id=YOUR_SECRET_ID \
     -Dcos.secret.key=YOUR_SECRET_KEY \
     -Dcos.trust.key=YOUR_TRUST_KEY \
     -jar etl-tool.jar 20250101 20250131 config.ini
```

#### Use Environment Variables for Credentials

Store credentials in environment variables:

```bash
export COS_SECRET_ID=your_secret_id
export COS_SECRET_KEY=your_secret_key
export COS_TRUST_KEY=your_trust_key

java -Dcos.secret.id=$COS_SECRET_ID \
     -Dcos.secret.key=$COS_SECRET_KEY \
     -Dcos.trust.key=$COS_TRUST_KEY \
     -jar etl-tool.jar 20250101 20250131 config.ini
```

#### Run in Background with Logging

```bash
nohup java -Xmx4g \
     -Dcos.secret.id=$COS_SECRET_ID \
     -Dcos.secret.key=$COS_SECRET_KEY \
     -Dcos.trust.key=$COS_TRUST_KEY \
     -jar etl-tool.jar 20250101 20250131 config.ini \
     > etl-output.log 2>&1 &
```

---

## Understanding the Output

### Progress Display

During execution, the tool displays real-time progress:

```
[2026-01-06 10:00:00] Initializing ETL job: 20250101 to 20250101
[2026-01-06 10:00:01] Loading configuration from: config.ini
[2026-01-06 10:00:02] Establishing connections...
  ✓ COS connection established
  ✓ MySQL connection established
  ✓ DolphinDB connection established
[2026-01-06 10:00:03] Creating temporary stream tables...
  ✓ xbond_quote_stream_temp
  ✓ xbond_trade_stream_temp
  ✓ market_price_stream_temp
  ✓ fut_market_price_stream_temp
[2026-01-06 10:00:04] Processing: 20250101
  XBond Market Quote: [████████░░] 80% (800K/1M records) - 45s
  XBond Trade:       [██████████] 100% (500K/500K records) - 20s
  Bond Future L2:    [████░░░░░░] 40% (20M/50M records) - 120s
  Overall:           [████████░░] 73%
[2026-01-06 10:03:30] Processing: 20250101
  XBond Market Quote: [██████████] 100% (1M/1M records) - 60s
  XBond Trade:       [██████████] 100% (500K/500K records) - 22s
  Bond Future L2:    [██████████] 100% (50M/50M records) - 130s
  Overall:           [██████████] 100%
[2026-01-06 10:03:34] Cleaning up temporary tables...
[2026-01-06 10:03:35] ETL job completed successfully
  Total days processed: 1
  Total records loaded: 51,500,000
  Total execution time: 3m 35s
```

### Success Indicators

- ✓ All connections established
- ✓ Temporary tables created
- ✓ All data sources extracted
- ✓ Data sorted by receive_time
- ✓ Data loaded into DolphinDB
- ✓ Temporary tables cleaned up
- Exit code: 0

---

## Troubleshooting

### Common Issues

#### Issue 1: Invalid Date Format

**Error**:
```
ERROR: ETL-001 Invalid arguments: Start date '2025-01-01' is not in YYYYMMDD format
```

**Solution**:
Use YYYYMMDD format (without hyphens):
```bash
java -jar etl-tool.jar 20250101 20250101 config.ini
```

#### Issue 2: Configuration File Not Found

**Error**:
```
ERROR: ETL-002 Configuration file not found: production.ini
```

**Solution**:
- Verify the configuration file exists in the specified path
- Use absolute path if file is in a different directory:
```bash
java -jar etl-tool.jar 20250101 20250101 /full/path/to/config.ini
```

#### Issue 3: Connection Failed

**Error**:
```
ERROR: ETL-003 Failed to connect to MySQL
  Context: Host: localhost, Port: 3306, Database: bond, Username: etl_user
```

**Solution**:
- Verify MySQL server is running: `mysql -h localhost -u etl_user -p`
- Check credentials in configuration file
- Verify network connectivity: `telnet localhost 3306`
- Check firewall rules

#### Issue 4: Insufficient Memory

**Error**:
```
ERROR: ETL-007 Insufficient memory
  Context: Required: 500MB, Available: 400MB, Threshold: 90%
```

**Solution**:
Increase heap memory allocation:
```bash
java -Xmx4g -jar etl-tool.jar 20250101 20250101 config.ini
```

Or reduce date range to process fewer days at once.

#### Issue 5: Concurrent Execution

**Error**:
```
ERROR: ETL-008 ETL tool is already running
  Context: PID file: .etl-tool.pid
```

**Solution**:
- Wait for the current instance to complete
- Or remove the PID file if previous instance crashed:
```bash
rm .etl-tool.pid
```

#### Issue 6: No Data Found

**Warning**:
```
WARN: No XBond Market Quote data found for business date: 20250101
```

**Solution**:
- Verify data exists in COS at the specified path
- Check business date format in COS path
- This is expected for dates with no trading data

---

## Performance Tuning

### Memory Allocation

For different data volumes, adjust heap memory:

| Daily Volume | Recommended Heap Size |
|---------------|----------------------|
| Small (<10M records) | -Xmx512m |
| Medium (10-50M records) | -Xmx2g |
| Large (>50M records) | -Xmx4g or higher |

### Multi-Day Processing

For processing multiple days:
- Process sequentially (default behavior)
- Consider processing overnight for large date ranges
- Monitor disk space for downloaded CSV files

---

## Monitoring and Logging

### Log Files

The tool generates detailed logs in `etl-tool.log`:

```bash
# View real-time logs
tail -f etl-tool.log

# Search for errors
grep ERROR etl-tool.log

# View last 100 lines
tail -n 100 etl-tool.log
```

### Monitoring Progress

Monitor progress via:
- Console output (stdout)
- Log file (etl-tool.log)
- DolphinDB temporary tables (during execution)

---

## Best Practices

### 1. Security

- Never hardcode credentials in configuration files
- Use environment variables or secure credential managers
- Restrict file permissions on configuration files: `chmod 600 config.ini`
- Rotate credentials regularly

### 2. Error Handling

- Always check exit codes: `echo $?`
- Review log files for detailed error information
- Monitor disk space during execution
- Set up alerts for failed jobs

### 3. Performance

- Allocate sufficient memory for your data volume
- Process during off-peak hours for large datasets
- Monitor system resources during execution
- Clean up old log files regularly

### 4. Data Integrity

- Verify data loaded into DolphinDB after ETL
- Compare record counts before and after ETL
- Check for receive_time ordering
- Monitor for skipped records (warnings in logs)

---

## Advanced Topics

### Custom Configuration Profiles

Create multiple configuration files for different environments:

```bash
# Development
java -jar etl-tool.jar 20250101 20250101 dev-config.ini

# Production
java -jar etl-tool.jar 20250101 20250101 prod-config.ini
```

### Integration with Schedulers

#### Cron (Linux)

Add to crontab for daily execution:

```cron
# Run ETL daily at 2 AM
0 2 * * * /path/to/run-etl.sh 20250101 $(date +\%Y\%m\%d --date='yesterday')
```

#### Windows Task Scheduler

Create a scheduled task to run ETL at specific times.

### CI/CD Integration

Integrate ETL tool into CI/CD pipelines:

```bash
#!/bin/bash
# Example CI/CD script

# Set environment variables
export COS_SECRET_ID=$CI_COS_SECRET_ID
export COS_SECRET_KEY=$CI_COS_SECRET_KEY
export COS_TRUST_KEY=$CI_COS_TRUST_KEY

# Run ETL
java -Xmx4g \
     -Dcos.secret.id=$COS_SECRET_ID \
     -Dcos.secret.key=$COS_SECRET_KEY \
     -Dcos.trust.key=$COS_TRUST_KEY \
     -jar etl-tool.jar 20250101 20250101 config.ini

# Check exit code
if [ $? -eq 0 ]; then
    echo "ETL completed successfully"
else
    echo "ETL failed with exit code: $?"
    exit 1
fi
```

---

## Support

### Getting Help

- Display help: `java -jar etl-tool.jar --help`
- Check version: `java -jar etl-tool.jar --version`
- Review logs: `etl-tool.log`

### Reporting Issues

When reporting issues, include:
- ETL tool version
- Java version
- Operating system
- Full error message
- Configuration file (with sensitive data redacted)
- Relevant log file excerpts

---

## Next Steps

1. Complete installation and configuration
2. Test with a single-day ETL
3. Verify data loaded into DolphinDB
4. Scale up to multi-day processing
5. Set up monitoring and alerts
6. Integrate with your workflow (cron, task scheduler, CI/CD)

---

## Appendix: Example Configuration Files

### Development Configuration

```ini
[xbond]
domain = cos.ap-beijing.myqcloud.com
region = ap-beijing
bucket = dev-bucket

[future]
host = localhost
port = 3306
database = bond
username = dev_user
password = dev_password

[ddb]
host = localhost
port = 8848
username = admin
password = admin
```

### Production Configuration

```ini
[xbond]
domain = cos.ap-beijing.myqcloud.com
region = ap-beijing
bucket = prod-bucket

[future]
host = prod-db.example.com
port = 3306
database = bond
username = prod_user
password = prod_password

[ddb]
host = prod-ddb.example.com
port = 8848
username = admin
password = admin
```
