# Bug Fix Log

## 2026-01-07

### XBond Quote File Pattern Issue

**File**: `src/main/java/com/histdata/etl/datasource/XbondQuoteExtractor.java`

**Issue**:
- The FILE_PATTERN was incorrectly set to `"xbond/AllPriceDepth_YYYYMMDD.csv"`
- This pattern assumes a single CSV file per day with the date embedded in the filename
- The correct COS structure has files organized as `/AllPriceDepth/YYYY-MM-DD/*.csv` where multiple CSV files can exist in each date directory
- Example real path: `/AllPriceDepth/2025-01-07/*.csv`

**Fix**:
```java
// Before:
private static final String FILE_PATTERN = "xbond/AllPriceDepth_YYYYMMDD.csv";
protected String getFilePath(LocalDate businessDate) {
    return FILE_PATTERN.replace("YYYYMMDD", businessDate.toString().replace("-", ""));
}

// After:
private static final String FILE_PATTERN = "/AllPriceDepth/YYYY-MM-DD/*.csv";
protected String getFilePath(LocalDate businessDate) {
    return FILE_PATTERN.replace("YYYY-MM-DD", businessDate.toString());
}
```

**Root Cause**:
- Misunderstanding of the COS directory structure during initial implementation
- File pattern didn't match the actual file organization on COS
- Date format was incorrect (YYYYMMDD vs YYYY-MM-DD)
- getFilePath() method was replacing "YYYYMMDD" but pattern used "YYYY-MM-DD", causing no replacement to occur

**Lessons Learned**:
1. Always verify the actual COS bucket structure before implementing file patterns
2. Test file pattern matching early in development with actual COS data
3. Document expected directory structure clearly in specification files
4. Pay attention to date formats in directory names (YYYY-MM-DD vs YYYYMMDD)
5. Ensure placeholder string in FILE_PATTERN matches the replace() target string

**Related Files**:
- `XbondQuoteExtractor.java` - Fixed the file pattern constant and getFilePath() method

---

### XBond Trade File Pattern Issue

**File**: `src/main/java/com/histdata/etl/datasource/XbondTradeExtractor.java`

**Issue**:
- The FILE_PATTERN was incorrectly set to `"xbond/XbondCfetsDeal_YYYYMMDD.csv"`
- This pattern assumes a single CSV file per day with the date embedded in the filename
- The correct COS structure has files organized as `/XbondCfetsDeal/YYYY-MM-DD/*.csv` where multiple CSV files can exist in each date directory
- Example real path: `/XbondCfetsDeal/2025-01-07/250205.IB.csv`
- getFilePath() method was replacing "YYYYMMDD" but pattern used "YYYY-MM-DD", causing no replacement to occur

**Fix**:
```java
// Before:
private static final String FILE_PATTERN = "xbond/XbondCfetsDeal_YYYYMMDD.csv";
protected String getFilePath(LocalDate businessDate) {
    return FILE_PATTERN.replace("YYYYMMDD", businessDate.toString().replace("-", ""));
}

// After:
private static final String FILE_PATTERN = "/XbondCfetsDeal/YYYY-MM-DD/*.csv";
protected String getFilePath(LocalDate businessDate) {
    return FILE_PATTERN.replace("YYYY-MM-DD", businessDate.toString());
}
```

**Root Cause**:
- Same misunderstanding as XBond Quote issue
- File pattern didn't match the actual file organization on COS
- Date format was incorrect (YYYYMMDD vs YYYY-MM-DD)
- **Critical bug**: getFilePath() attempted to replace "YYYYMMDD" but FILE_PATTERN contained "YYYY-MM-DD", resulting in no replacement and returning the placeholder string unchanged

**Example of the bug**:
```java
// With businessDate = LocalDate.of(2026, 1, 7):
// FILE_PATTERN = "/XbondCfetsDeal/YYYY-MM-DD/*.csv"
// businessDate.toString() = "2026-01-07"
// businessDate.toString().replace("-", "") = "20260107"
// FILE_PATTERN.replace("YYYYMMDD", "20260107") = "/XbondCfetsDeal/YYYY-MM-DD/*.csv" (NO CHANGE!)
```

**Lessons Learned**:
1. Consistent COS directory structure across different data types (AllPriceDepth, XbondCfetsDeal)
2. Real paths example: `/XbondCfetsDeal/2025-01-07/250205.IB.csv` shows actual file naming pattern (timecode.IB.csv format)
3. Verify directory structure with real examples before implementation
4. **CRITICAL**: Always test getFilePath() method to ensure the replacement string actually matches the placeholder in FILE_PATTERN
5. Use consistent date formats in both the pattern and the replacement target

**Related Files**:
- `XbondTradeExtractor.java` - Fixed the file pattern constant and getFilePath() method

---

### Business Date Should Come from EtlCli, Not Source Records

**Files**:
- `src/main/java/com/histdata/etl/transformer/DataTransformer.java`
- `src/main/java/com/histdata/etl/transformer/FutureQuoteTransformer.java`
- `src/main/java/com/histdata/etl/transformer/XbondQuoteTransformer.java`
- `src/main/java/com/histdata/etl/transformer/XbondTradeTransformer.java`
- `src/main/java/com/histdata/etl/cli/EtlCli.java`

**Issue**:
- Transformers were reading `business_date` from source records (CSV or database)
- This violates the ETL principle that business date should be determined by CLI command line arguments
- Source data may have incorrect or inconsistent business dates
- The ETL job should use the date passed by the user via CLI arguments

**Fix**:
```java
// Interface change - DataTransformer.java
public interface DataTransformer<T> {
    // Before:
    T transform(Object rawRecord) throws Exception;

    // After:
    T transform(Object rawRecord, LocalDate businessDate) throws Exception;
}

// FutureQuoteTransformer.java
// Before:
String businessDateStr = (String) record.get("business_date");
java.sql.Date businessDate = new java.sql.Date(DateUtils.parseDate(businessDateStr, "yyyyMMdd").getTime());

// After:
java.sql.Date businessDateSql = new java.sql.Date(java.sql.Date.valueOf(businessDate).getTime());

// XbondQuoteTransformer.java
// Before:
String businessDateStr = firstRecord.get("business_date");
Date businessDate = new Date(DateUtils.parseDateYYYYMMDD(businessDateStr).getTime());

// After:
Date businessDateSql = new Date(java.sql.Date.valueOf(businessDate).getTime());

// XbondTradeTransformer.java
// Before:
String businessDateStr = record.get("business_date");
java.sql.Date businessDate = new java.sql.Date(DateUtils.parseDate(businessDateStr, "yyyyMMdd").getTime());

// After:
java.sql.Date businessDateSql = new java.sql.Date(java.sql.Date.valueOf(businessDate).getTime());

// EtlCli.java - All three transformer methods updated:
// Before:
transformer.transform(record);

// After:
transformer.transform(record, date);
```

**Root Cause**:
- Initial design incorrectly assumed source records should determine business date
- Failed to follow standard ETL pattern where the ETL job controls the business date
- Source data may contain corrupted or incorrect business dates that should not be trusted

**Lessons Learned**:
1. **ETL principle**: Business date must always come from the orchestration layer (EtlCli), not source data
2. Source data may contain errors or inconsistencies - don't trust it for critical metadata
3. The CLI arguments (START_DATE, END_DATE) are the authoritative source of truth for business dates
4. Using CLI-passed business date ensures data integrity and traceability
5. Transformers should focus on data transformation, not date determination

---

### Missing LocalDate Import in Transformer Classes

**Files**:
- `src/main/java/com/histdata/etl/transformer/FutureQuoteTransformer.java`
- `src/main/java/com/histdata/etl/transformer/XbondQuoteTransformer.java`
- `src/main/java/com/histdata/etl/transformer/XbondTradeTransformer.java`

**Issue**:
- After updating the `DataTransformer` interface to include `LocalDate businessDate` parameter
- The three transformer implementation classes were missing the `java.time.LocalDate` import
- This caused compilation errors: "找不到符号 类 LocalDate" (cannot find symbol class LocalDate)

**Error Message**:
```
[ERROR] /d:/SpecDrivenDev/historical-etl/src/main/java/com/histdata/etl/transformer/XbondTradeTransformer.java:[20,57] 找不到符号
  符号:   类 LocalDate
  位置: 类 com.histdata.etl.transformer.XbondTradeTransformer
[ERROR] /d:/SpecDrivenDev/historical-etl/src/main/java/com/histdata/etl/transformer/FutureQuoteTransformer.java:[20,58] 找不到符号
  符号:   类 LocalDate
  位置: 类 com.histdata.etl.transformer.FutureQuoteTransformer
[ERROR] /d:/SpecDrivenDev/historical-etl/src/main/java/com/histdata/etl/transformer/XbondQuoteTransformer.java:[27,57] 找不到符号
  符号:   类 LocalDate
  位置: 类 com.histdata.etl.transformer.XbondQuoteTransformer
```

**Fix**:
```java
// Added to all three transformer classes:
import java.time.LocalDate;
```

**Root Cause**:
- When updating the interface signature to include `LocalDate` parameter
- Forgot to add the corresponding import statement in the implementation classes
- The compiler cannot resolve the LocalDate type without the import

**Lessons Learned**:
1. When updating interface signatures, always verify all implementations are updated with necessary imports
2. Run Maven compilation immediately after interface changes to catch missing imports early
3. Use IDE import management or compile-time error checking to prevent these issues
4. Interface changes affect all implementations - systematic updates are required

**Build Result**: ✅ BUILD SUCCESS after adding imports

---

### Event Time Format Issue in FutureQuoteTransformer

**File**: `src/main/java/com/histdata/etl/transformer/FutureQuoteTransformer.java`

**Issue**:
- The timestamp parsing logic was creating an incorrect event time string format
- With `action_date=20250107` and `action_time=93050090`:
  - Expected: `"2025-01-07 09:30:50.090"`
  - Previous actual: `"20250107-09:30:50.090"` (incorrect format)

**Root Cause**:
```java
// Previous incorrect code:
String actionDateStr = String.format("%08d", actionDate);  // "20250107"
String actionTimeStr = String.format("%09d", actionTime);  // "093050090"

// This produced: "20250107-09:30:50.090"
// But format string expected: "yyyyMMdd-HH:mm:ss.SSS"
// Which means: yyyy-MM-dd HH:mm:ss.SSS (with hyphens and spaces)
```

The previous code didn't properly format the actionDate into yyyy-MM-dd format. It kept the date as "20250107" (no hyphens) but the parsing format expected "yyyy-MM-dd" (with hyphens).

**Fix**:
```java
// Before:
String actionDateStr = String.format("%08d", actionDate);
String actionTimeStr = String.format("%09d", actionTime);
String eventTimeStr = actionDateStr + "-" + 
                actionTimeStr.substring(0, 2) + ":" + 
                actionTimeStr.substring(2, 4) + ":" + 
                actionTimeStr.substring(4, 6) + "." + 
                actionTimeStr.substring(6);
java.sql.Timestamp eventTime = new java.sql.Timestamp(DateUtils.parseTimestamp(eventTimeStr, "yyyyMMdd-HH:mm:ss.SSS").getTime());

// After:
int actionDate = getInteger(record, "action_date");
int actionTime = getInteger(record, "action_time");
String actionDateStr = String.format("%08d", actionDate);
String actionTimeStr = String.format("%09d", actionTime);
String eventTimeStr = String.format("%s-%s:%s:%s.%s",
                actionDateStr.substring(0, 4) + actionDateStr.substring(4, 6) + actionDateStr.substring(6, 8),
                actionTimeStr.substring(0, 2),
                actionTimeStr.substring(2, 4),
                actionTimeStr.substring(4, 6) + actionTimeStr.substring(6));
java.sql.Timestamp eventTime = new java.sql.Timestamp(DateUtils.parseTimestamp(eventTimeStr, "yyyy-MM-dd HH:mm:ss.SSS").getTime());
```

**Correct Logic**:
```java
// With action_date=20250107 and action_time=93050090:

// Step 1: Format dates
actionDateStr = String.format("%08d", 20250107) = "20250107"

// Step 2: Format time
actionTimeStr = String.format("%09d", 93050090) = "093050090"

// Step 3: Construct eventTimeStr = "2025-01-07 09:30:50.090"
// actionDateStr.substring(0,4) = "2025"
// actionDateStr.substring(4,6) = "01"
// actionDateStr.substring(6,8) = "07"
// → Date part: "2025-01-07"

// actionTimeStr.substring(0,2) = "09"  // Hours
// actionTimeStr.substring(2,4) = "30"  // Minutes
// actionTimeStr.substring(4,6) = "50"  // Seconds
// actionTimeStr.substring(6) = "090"  // Milliseconds
// → Time part: "09:30:50.090"

// Full: "2025-01-07 09:30:50.090" ✓ CORRECT
```

**Lessons Learned**:
1. When formatting timestamps, ensure the constructed string matches the parser's expected format exactly
2. Use `yyyy-MM-dd` format (with hyphens) for date parsing, not `yyyyMMdd` (no hyphens)
3. Action time format is HHmmssSSS (9 digits total), not HHmmssSS (8 digits)
4. The last 3 digits of action_time are milliseconds (SSS), not seconds with decimal (S)
5. Test timestamp parsing logic with actual data values to verify correctness
6. Format string "yyyy-MM-dd HH:mm:ss.SSS" expects hyphens and spaces in specific positions

**Build Result**: ✅ BUILD SUCCESS

**Verification**:
- Input: action_date=20250107, action_time=93050090
- Output: eventTimeStr = "2025-01-07 09:30:50.090"
- Matches expected format ✓
