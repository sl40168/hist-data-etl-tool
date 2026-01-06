# Data Model: ETL CLI Tool

**Feature**: 003-etl-cli-tool
**Date**: 2026-01-06

This document defines the core entities, data structures, and validation rules for the ETL CLI Tool.

---

## Core Entities

### 1. EtlJobContext

Represents a single execution of the ETL tool with a specified date range.

**Fields**:
- `startDate: LocalDate` - Start date of the ETL job (format: YYYY-MM-DD)
- `endDate: LocalDate` - End date of the ETL job (format: YYYY-MM-DD)
- `configPath: String` - Path to INI configuration file (optional, defaults to embedded config.ini)
- `config: Config` - Loaded configuration object
- `currentDate: LocalDate` - Current business date being processed
- `dolphinDbConnection: DBConnection` - DolphinDB connection instance
- `mysqlConnection: Connection` - MySQL connection instance
- `cosClient: COSClient` - COS client instance
- `jobId: String` - Unique identifier for the ETL job (UUID)

**Validation Rules**:
- `startDate` must not be after `endDate`
- Both dates must be valid dates
- `startDate` and `endDate` must be in YYYYMMDD format when provided as CLI arguments
- All connections must be established before data extraction begins

**State Transitions**:
- `INITIALIZED` → `CONNECTING` → `EXTRACTING` → `TRANSFORMING` → `LOADING` → `COMPLETED` or `FAILED`

---

### 2. Config

Represents runtime configuration loaded from INI file.

**Fields**:
- `cosConfig: CosConfig` - COS connection configuration
- `mysqlConfig: MySqlConfig` - MySQL connection configuration
- `dolphinDbConfig: DolphinDbConfig` - DolphinDB connection configuration

**Validation Rules**:
- All required parameters must be present
- Connection parameters must be valid (e.g., ports must be positive integers)
- File paths must be accessible

---

### 3. CosConfig

Configuration for COS (Cloud Object Storage) connection.

**Fields**:
- `domain: String` - COS endpoint domain
- `region: String` - COS region
- `bucket: String` - COS bucket name
- `secretId: String` - COS secret ID (passed via JVM parameter)
- `secretKey: String` - COS secret key (passed via JVM parameter)
- `trustKey: String` - Trust key for COS authentication (passed via JVM parameter)

**Validation Rules**:
- All fields must be non-empty
- `domain` must be a valid URL format
- `port` (if specified) must be a positive integer between 1-65535

**INI Section**: `[xbond]`
```ini
[xbond]
domain = cos.ap-beijing.myqcloud.com
region = ap-beijing
bucket = my-bucket
```

---

### 4. MySqlConfig

Configuration for MySQL database connection.

**Fields**:
- `host: String` - MySQL server hostname or IP
- `port: int` - MySQL server port (default: 3306)
- `database: String` - Database name (e.g., "bond")
- `username: String` - MySQL username
- `password: String` - MySQL password

**Validation Rules**:
- All fields must be non-empty
- `host` must be a valid hostname or IP address
- `port` must be a positive integer between 1-65535
- `database` must be a valid database name

**INI Section**: `[future]`
```ini
[future]
host = localhost
port = 3306
database = bond
username = etl_user
password = etl_password
```

---

### 5. DolphinDbConfig

Configuration for DolphinDB connection.

**Fields**:
- `host: String` - DolphinDB server hostname or IP
- `port: int` - DolphinDB server port (default: 8848)
- `username: String` - DolphinDB username
- `password: String` - DolphinDB password

**Validation Rules**:
- All fields must be non-empty
- `host` must be a valid hostname or IP address
- `port` must be a positive integer between 1-65535

**INI Section**: `[ddb]`
```ini
[ddb]
host = localhost
port = 8848
username = admin
password = admin
```

---

### 6. XbondQuoteRecord

Represents a transformed XBond Market Quote record ready for loading into DolphinDB.

**Fields**:
- `businessDate: Date` - Trading date of the quote (DolphinDB DATE type)
- `exchProductId: String` - Exchange product ID (e.g., "210210.IB")
- `productType: String` - Product type (always "BOND")
- `exchange: String` - Exchange code (always "CFETS")
- `source: String` - Data source (always "XBOND")
- `settleSpeed: int` - Settlement speed (0 or 1)
- `level: String` - Quote level (always "L2")
- `status: String` - Quote status (always "Normal")
- `preClosePrice: Double` - Previous close price (null for AllPriceDepth)
- `preSettlePrice: Double` - Previous settle price (null for AllPriceDepth)
- `preInterest: Double` - Previous interest (null for AllPriceDepth)
- `openPrice: Double` - Open price (null for AllPriceDepth)
- `highPrice: Double` - High price (null for AllPriceDepth)
- `lowPrice: Double` - Low price (null for AllPriceDepth)
- `closePrice: Double` - Close price (null for AllPriceDepth)
- `settlePrice: Double` - Settle price (null for AllPriceDepth)
- `upperLimit: Double` - Upper limit price (null for AllPriceDepth)
- `lowerLimit: Double` - Lower limit price (null for AllPriceDepth)
- `totalVolume: Long` - Total volume (null for AllPriceDepth)
- `totalTurnover: Double` - Total turnover (null for AllPriceDepth)
- `openInterest: Long` - Open interest (null for AllPriceDepth)
- `bid0Price: Double` - Best bid price
- `bid0Yield: Double` - Best bid yield
- `bid0YieldType: String` - Best bid yield type ("MATURITY" or "EXERCISE")
- `bid0TradableVolume: Long` - Best bid tradable volume (always 0)
- `bid0Volume: Long` - Best bid volume
- `offer0Price: Double` - Best offer price
- `offer0Yield: Double` - Best offer yield
- `offer0YieldType: String` - Best offer yield type ("MATURITY" or "EXERCISE")
- `offer0TradableVolume: Long` - Best offer tradable volume (always 0)
- `offer0Volume: Long` - Best offer volume
- `bid1Price through bid5Price: Double` - Bid prices for levels 1-5
- `bid1Yield through bid5Yield: Double` - Bid yields for levels 1-5
- `bid1YieldType through bid5YieldType: String` - Bid yield types for levels 1-5
- `bid1TradableVolume through bid5TradableVolume: Long` - Tradable volumes for levels 1-5
- `bid1Volume through bid5Volume: Long` - Volumes for levels 1-5 (null)
- `offer1Price through offer5Price: Double` - Offer prices for levels 1-5
- `offer1Yield through offer5Yield: Double` - Offer yields for levels 1-5
- `offer1YieldType through offer5YieldType: String` - Offer yield types for levels 1-5
- `offer1TradableVolume through offer5TradableVolume: Long` - Tradable volumes for levels 1-5
- `offer1Volume through offer5Volume: Long` - Volumes for levels 1-5 (null)
- `eventTime: Timestamp` - Event time (from transact_time)
- `receiveTime: Timestamp` - Receive time (from recv_time)
- `createTime: Timestamp` - Create time (auto-populated by DolphinDB, not set by application)

**Validation Rules**:
- `businessDate`, `exchProductId`, `eventTime`, `receiveTime` are required
- `receiveTime` must not be null (records with null receive_time are skipped during transformation)
- `settleSpeed` must be 0 or 1
- `bid0YieldType` and `offer0YieldType` must be "MATURITY" or "EXERCISE"
- All price fields must be non-negative

---

### 7. XbondTradeRecord

Represents a transformed XBond Trade record ready for loading into DolphinDB.

**Fields**:
- `businessDate: Date` - Trading date of the trade (DolphinDB DATE type)
- `exchProductId: String` - Exchange product ID (e.g., "210210.IB")
- `productType: String` - Product type (always "BOND")
- `exchange: String` - Exchange code (always "CFETS")
- `source: String` - Data source (always "XBOND")
- `settleSpeed: int` - Settlement speed (0 or 1)
- `lastTradePrice: Double` - Last trade price (clean price)
- `lastTradeYield: Double` - Last trade yield
- `lastTradeYieldType: String` - Last trade yield type ("MATURITY" or "EXERCISE")
- `lastTradeVolume: Long` - Last trade volume
- `lastTradeTurnover: Double` - Last trade turnover (null)
- `lastTradeInterest: Double` - Last trade interest (null)
- `lastTradeSide: String` - Last trade side ("TKN", "GVN", "TRD", or "DONE")
- `eventTime: Timestamp` - Event time (from deal_time)
- `receiveTime: Timestamp` - Receive time (from recv_time, fallback to event_time if null)
- `createTime: Timestamp` - Create time (auto-populated by DolphinDB, not set by application)

**Validation Rules**:
- All fields except `lastTradeTurnover` and `lastTradeInterest` are required
- `lastTradeSide` must be one of: "TKN", "GVN", "TRD", "DONE"
- `settleSpeed` must be 0 or 1
- `lastTradeYieldType` must be "MATURITY" or "EXERCISE"
- `lastTradePrice` must be non-negative
- `lastTradeVolume` must be positive

---

### 8. FutureQuoteRecord

Represents a transformed Bond Future L2 Quote record ready for loading into DolphinDB.

**Fields**:
- `businessDate: Date` - Trading date of the quote (DolphinDB DATE type)
- `exchProductId: String` - Exchange product ID (e.g., "T2503")
- `productType: String` - Product type (always "FUTURE")
- `exchange: String` - Exchange code (always "CFFEX")
- `source: String` - Data source (always "FUTURE")
- `settleSpeed: int` - Settlement speed (always 1 for futures)
- `level: String` - Quote level (always "L2")
- `status: String` - Quote status (always "Normal")
- `preClosePrice: Double` - Previous close price
- `preSettlePrice: Double` - Previous settle price
- `preInterest: Long` - Previous open interest
- `openPrice: Double` - Open price
- `highPrice: Double` - High price
- `lowPrice: Double` - Low price
- `closePrice: Double` - Close price (last trade price)
- `settlePrice: Double` - Settle price (0 during trading hours)
- `upperLimit: Double` - Upper limit price
- `lowerLimit: Double` - Lower limit price
- `totalVolume: Long` - Total volume
- `totalTurnover: Double` - Total turnover
- `openInterest: Long` - Open interest
- `bid0Price through bid4Price: Double` - Bid prices for levels 0-4
- `bid0TradableVolume through bid4TradableVolume: Long` - Bid tradable volumes for levels 0-4
- `bid0Volume through bid4Volume: Long` - Bid volumes for levels 0-4 (null, same as tradable)
- `offer0Price through offer4Price: Double` - Offer prices for levels 0-4
- `offer0TradableVolume through offer4TradableVolume: Long` - Offer tradable volumes for levels 0-4
- `offer0Volume through offer4Volume: Long` - Offer volumes for levels 0-4 (null, same as tradable)
- `eventTime: Timestamp` - Event time (from action_date + action_time)
- `receiveTime: Timestamp` - Receive time (from receive_time, fallback to event_time if null)
- `createTime: Timestamp` - Create time (auto-populated by DolphinDB, not set by application)

**Validation Rules**:
- All fields are required except where noted as null
- `receiveTime` must not be null (records with null receive_time are skipped during transformation)
- `settleSpeed` is always 1 for futures
- `closePrice` (last trade price) must be non-negative
- All volume fields must be non-negative

---

### 9. ProgressStatus

Represents the progress status of an ETL operation.

**Fields**:
- `currentDate: String` - Current business date being processed (YYYYMMDD format)
- `quoteRecordsExtracted: long` - Number of XBond Market Quote records extracted
- `tradeRecordsExtracted: long` - Number of XBond Trade records extracted
- `futureRecordsExtracted: long` - Number of Future Quote records extracted
- `quoteRecordsTransformed: long` - Number of XBond Market Quote records transformed
- `tradeRecordsTransformed: long` - Number of XBond Trade records transformed
- `futureRecordsTransformed: long` - Number of Future Quote records transformed
- `recordsLoaded: long` - Number of records loaded into DolphinDB
- `startTime: long` - Start timestamp (milliseconds)
- `currentTimestamp: long` - Current timestamp (milliseconds)
- `status: JobStatus` - Overall job status

**Validation Rules**:
- All counts must be non-negative
- `currentTimestamp` must be >= `startTime`
- `recordsLoaded` must be <= sum of transformed records

---

## Enums

### JobStatus

Enumeration of possible ETL job statuses.

**Values**:
- `INITIALIZED` - Job initialized, connections established
- `CONNECTING` - Establishing connections to source and target systems
- `EXTRACTING` - Extracting data from source systems
- `TRANSFORMING` - Transforming extracted data
- `LOADING` - Loading transformed data into DolphinDB
- `COMPLETED` - Job completed successfully
- `FAILED` - Job failed with error

---

## Data Transformation Rules

### XbondQuoteRecord Transformation (AllPriceDepth CSV → XbondQuoteRecord)

**Source CSV Fields**:
- `underlying_security_id: String` → `exchProductId` (append ".IB" suffix)
- `underlying_settlement_type: int` → `settleSpeed` (1 = 0, 2 = 1)
- `transact_time: String` → `eventTime` (parse "yyyyMMdd-HH:mm:ss.SSS")
- `recv_time: String` → `receiveTime` (parse "yyyyMMdd-HH:mm:ss.SSS")
- `underlying_md_entry_type: int` → bid/offer mapping (0 = bid, 1 = offer)
- `underlying_md_price_level: int` → level mapping (1-6 → 0-5)
- `underlying_md_entry_px: double` → price field
- `underlying_md_yield: double` → yield field
- `underlying_md_yield_type: String` → yield_type field
- `underlying_md_entry_size: long` → volume field

**Grouping**:
- Group records by `mq_offset` and `underlying_security_id`
- Sort within group by `underlying_md_price_level` and `underlying_md_entry_type`
- One group → One XbondQuoteRecord

**Mapping Logic**:
- `underlying_md_entry_type = 0` (bid) and `level = 1` → `bid_0_*` fields
- `underlying_md_entry_type = 1` (offer) and `level = 1` → `offer_0_*` fields
- `underlying_md_entry_type = 0` (bid) and `level = 2-6` → `bid_1_*` to `bid_5_*` fields
- `underlying_md_entry_type = 1` (offer) and `level = 2-6` → `offer_1_*` to `offer_5_*` fields

---

### XbondTradeRecord Transformation (XbondCfetsDeal CSV → XbondTradeRecord)

**Source CSV Fields**:
- `bond_key: String` → `exchProductId`
- `net_price: double` → `lastTradePrice`
- `set_days: String` → `settleSpeed` ("T+0" = 0, "T+1" = 1)
- `yield: double` → `lastTradeYield`
- `yield_type: int` → `lastTradeYieldType` (0 = "MATURITY", 1 = "EXERCISE")
- `deal_size: long` → `lastTradeVolume`
- `side: String` → `lastTradeSide` ("X" = "TKN", "Y" = "GVN", "Z" = "TRD", "D" = "DONE")
- `deal_time: String` → `eventTime` (parse "yyyy-MM-dd HH:mm:ss.SSS")
- `recv_time: String` → `receiveTime` (parse "yyyy-MM-dd HH:mm:ss.SSS", fallback to `eventTime` if null)

**Mapping Logic**:
- One source record → One XbondTradeRecord
- Direct field mapping with type conversions as noted above

---

### FutureQuoteRecord Transformation (MySQL fut_tick → FutureQuoteRecord)

**Source MySQL Fields**:
- `code: String` → `exchProductId` (e.g., "T2503" → "T2503.CFFEX")
- `exchg: String` → `exchange` (always "CFFEX")
- `open: double` → `openPrice`
- `high: double` → `highPrice`
- `low: double` → `lowPrice`
- `price: double` → `closePrice` (last trade price)
- `settle_price: double` → `settlePrice`
- `upper_limit: double` → `upperLimit`
- `lower_limit: double` → `lowerLimit`
- `total_volume: long` → `totalVolume`
- `total_turnover: double` → `totalTurnover`
- `open_interest: long` → `openInterest`
- `pre_close: double` → `preClosePrice`
- `pre_settle: double` → `preSettlePrice`
- `pre_interest: long` → `preInterest`
- `bid_prices: double[]` → `bid0Price` through `bid4Price`
- `ask_prices: double[]` → `offer0Price` through `offer4Price`
- `bid_qty: long[]` → `bid0TradableVolume` through `bid4TradableVolume`
- `ask_qty: long[]` → `offer0TradableVolume` through `offer4TradableVolume`
- `action_date: int` + `action_time: int` → `eventTime` (parse "yyyyMMdd" + "HHmmssSSS")
- `receive_time: String` → `receiveTime` (parse if present, fallback to `eventTime` if null)

**Mapping Logic**:
- One source record → One FutureQuoteRecord
- Direct field mapping with type conversions and array unwinding

---

## Validation Summary

### Input Validation (CLI Arguments)
- Start date must be valid YYYYMMDD format
- End date must be valid YYYYMMDD format
- Start date must not be after end date
- Config file path (if provided) must be readable

### Configuration Validation (INI File)
- All required sections ([xbond], [future], [ddb]) must be present
- All required fields in each section must be present and valid
- Connection parameters must be valid (e.g., ports, hosts)

### Data Validation (During Transformation)
- `receiveTime` must not be null (skip record with warning if null)
- `settleSpeed` must be 0 or 1
- `lastTradeSide` must be valid enum value
- Price fields must be non-negative
- Volume fields must be non-negative

### Resource Validation (Before Processing)
- Sufficient memory must be available (90% threshold)
- Single instance check must pass
- All connections must be established
