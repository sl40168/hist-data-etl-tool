# Implementation Plan: ETL CLI Tool

**Branch**: `003-etl-cli-tool` | **Date**: 2026-01-06 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/003-etl-cli-tool/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

The ETL CLI Tool is a command-line application that extracts financial data from multiple source systems (COS CSV files and MySQL database), transforms it according to business rules, and loads it into DolphinDB temporary tables. The tool processes data day-by-day within a specified date range, ensuring data integrity through sequential processing and proper resource management.

## Technical Context

**Language/Version**: Java 1.8 (Mandatory per constitution)
**Primary Dependencies**: COS SDK (com.qcloud:cos_api:5.6.3), DolphinDB Java API (com.dolphindb:dolphindb-javaapi:3.00.0.2), MySQL JDBC, INI parsing library
**Storage**: DolphinDB (temporary stream tables), MySQL (source)
**Testing**: JUnit (Mandatory per constitution)
**Target Platform**: Linux/Windows servers
**Project Type**: Single project (CLI application)
**Performance Goals**: Process typical daily volumes (1-10M quote records, 100K-1M trade records, 10-50M future records) within 5 minutes per day
**Constraints**: Java 8 compatibility, single-instance execution, memory-based caching with graceful failure on OOM
**Scale/Scope**: Historical data processing with multi-day batch capabilities, handling up to ~60M records per day across three sources

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Core Principles Compliance

✅ **I. Maven Build Tool**: Project will use Maven 3.6.3 for build and dependency management. All dependencies will be declared in `pom.xml`.

✅ **II. Java 8 Compatibility**: Project targets Java 1.8. All dependencies selected (COS SDK, DolphinDB API, MySQL Connector/J) are Java 8 compatible.

✅ **III. CLI Interface**: All functionality is accessible through CLI with command-line arguments, stdout for output, and stderr for errors.

✅ **IV. INI Configuration**: Runtime configuration uses `.ini` files with standard format. Configuration validation occurs at startup.

✅ **V. Unit Test Coverage**: Every class will have corresponding unit tests written in parallel with production code using JUnit.

✅ **VI. Well-Known Open Source Components**: All selected dependencies (Apache Commons, SLF4J, Logback, Jackson, MySQL Connector/J, COS SDK, DolphinDB API) are well-known, open-source components with active maintenance and Java 8 compatibility.

✅ **VII. Interface-Based Component Boundaries**: All component boundaries will be defined through Java interfaces (e.g., DataSourceExtractor, DataTransformer, DataLoader), with concrete implementations hidden behind these abstractions.

### Quality Standards Compliance

✅ **Testing Discipline**: Unit tests mandatory for every class.

✅ **Configuration Management**: All runtime behavior configurable via INI files.

✅ **Error Handling**: Clear, actionable error messages on stderr with appropriate exit codes.

✅ **Code Organization**: Following Maven directory structure (`src/main/java` and `src/test/java`).

✅ **Library Selection**: All dependencies meet well-known, open-source criteria.

✅ **Component Design**: Interface-based boundaries for all major components.

**CONCLUSION**: All constitutional requirements are met. No violations detected. Proceed to Phase 0 research.

---

**Phase 0 Status**: ✅ Complete - Research findings consolidated in `research.md`

**Phase 1 Status**: ✅ Complete - Design artifacts generated:
- `data-model.md` - Entity definitions and data transformation rules
- `contracts/cli-contract.md` - CLI interface specification
- `quickstart.md` - Setup and usage instructions
- `CODEBUDDY.md` - Agent context updated with technology stack

**Constitution Check Post-Design**: ✅ All requirements still met. Design aligns with constitutional principles.

## Project Structure

### Documentation (this feature)

```text
specs/003-etl-cli-tool/
├── spec.md              # Feature specification (existing)
├── plan.md              # This file (Phase 0 & 1 complete)
├── research.md          # Phase 0 output ✅ Complete
├── data-model.md        # Phase 1 output ✅ Complete
├── quickstart.md        # Phase 1 output ✅ Complete
├── contracts/           # Phase 1 output ✅ Complete
│   └── cli-contract.md  # CLI interface specification
└── tasks.md             # Phase 2 output (created by /speckit.tasks command - TODO)
```

### Source Code (repository root)

```text
# Maven Standard Directory Structure
src/
├── main/
│   ├── java/
│   │   └── com/histdata/etl/
│   │       ├── cli/               # CLI entry point
│   │       │   └── EtlCli.java
│   │       ├── config/            # Configuration management
│   │       │   ├── Config.java
│   │       │   └── IniConfigLoader.java
│   │       ├── datasource/        # Data source extraction (interface-based)
│   │       │   ├── DataSourceExtractor.java
│   │       │   ├── CosExtractor.java
│   │       │   ├── XbondQuoteExtractor.java
│   │       │   ├── XbondTradeExtractor.java
│   │       │   └── MySqlFutureExtractor.java
│   │       ├── transformer/       # Data transformation (interface-based)
│   │       │   ├── DataTransformer.java
│   │       │   ├── XbondQuoteTransformer.java
│   │       │   ├── XbondTradeTransformer.java
│   │       │   └── FutureQuoteTransformer.java
│   │       ├── loader/            # Data loading (interface-based)
│   │       │   ├── DataLoader.java
│   │       │   └── DolphinDbLoader.java
│   │       ├── model/             # Domain models
│   │       │   ├── XbondQuoteRecord.java
│   │       │   ├── XbondTradeRecord.java
│   │       │   ├── FutureQuoteRecord.java
│   │       │   └── EtlJobContext.java
│   │       └── util/              # Utilities
│   │           ├── DateUtils.java
│   │           ├── FileLock.java
│   │           └── ProgressMonitor.java
│   └── resources/
│       ├── config.ini             # Default configuration
│       └── logback.xml            # Logging configuration
└── test/
    └── java/
        └── com/histdata/etl/
            ├── cli/
            │   └── EtlCliTest.java
            ├── config/
            │   ├── ConfigTest.java
            │   └── IniConfigLoaderTest.java
            ├── datasource/
            │   ├── CosExtractorTest.java
            │   ├── XbondQuoteExtractorTest.java
            │   ├── XbondTradeExtractorTest.java
            │   └── MySqlFutureExtractorTest.java
            ├── transformer/
            │   ├── XbondQuoteTransformerTest.java
            │   ├── XbondTradeTransformerTest.java
            │   └── FutureQuoteTransformerTest.java
            ├── loader/
            │   └── DolphinDbLoaderTest.java
            └── util/
                ├── DateUtilsTest.java
                ├── FileLockTest.java
                └── ProgressMonitorTest.java

pom.xml                        # Maven build configuration
README.md                      # Project documentation (existing)
```

**Structure Decision**: Single project with Maven standard directory structure. This aligns with the CLI application type and constitutional requirements for Maven-based builds with clear separation of concerns (cli, config, datasource, transformer, loader, model, util packages). All component boundaries are defined through interfaces:

- **DataSourceExtractor** (datasource package): Defines `extract(LocalDate businessDate)` method for data extraction
- **DataTransformer** (transformer package): Defines `transform(Object rawRecord)` method for data transformation
- **DataLoader** (loader package): Defines `load(List<?> records)` method for data loading

Concrete implementations (CosExtractor, XbondQuoteExtractor, etc.) are hidden behind these interfaces, enabling testability through mocking and supporting interchangeable implementations.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No constitutional violations detected. This section remains empty.

## Memory Pre-Flight Check Algorithm

To implement FR-023 (memory graceful failure), use the following algorithm:

1. **Estimate record count**: Query each data source for expected record count for business date
   - COS: List files and estimate based on average file size (10KB per record)
   - MySQL: Execute `SELECT COUNT(*) FROM bond.fut_tick WHERE trading_date = {date}`

2. **Calculate memory requirement** (per record):
   - XbondQuoteRecord: ~500 bytes
   - XbondTradeRecord: ~300 bytes
   - FutureQuoteRecord: ~400 bytes

3. **Total estimated memory**:
   ```
   total_bytes = (quote_count * 500) + (trade_count * 300) + (future_count * 400)
   estimated_mb = total_bytes / (1024 * 1024)
   ```

4. **Available JVM heap**:
   ```
   max_heap = Runtime.getRuntime().maxMemory() / (1024 * 1024)
   available_mb = max_heap * 0.90  // 90% threshold
   ```

5. **Validation**:
   ```
   if estimated_mb > available_mb:
       throw InsufficientMemoryException(
           "Insufficient memory. Required: {estimated_mb} MB, Available: {available_mb} MB. " +
           "Increase JVM heap size with -Xmx parameter (e.g., -Xmx4g)."
       )
   ```

6. **Display progress**: Log "Memory check passed: Estimated {estimated_mb} MB, Available {available_mb} MB"
