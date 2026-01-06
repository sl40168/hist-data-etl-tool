---
description: "Task list for feature implementation"
---

# Tasks: ETL CLI Tool

**Input**: Design documents from `/specs/003-etl-cli-tool/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/cli-contract.md
**Tests**: Unit tests are MANDATORY per constitution - every class must have corresponding tests

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Single project**: `src/main/java`, `src/test/java` at repository root

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create Maven project structure with standard directories (src/main/java, src/test/java, src/main/resources)
- [ ] T002 [P] Create pom.xml with all dependencies (COS SDK 5.6.3, DolphinDB API 3.00.0.2, MySQL Connector/J 8.0.33, Apache Commons CSV 1.10.0, Apache Commons Configuration 2.9.0, SLF4J 1.7.x, Logback 1.2.x, JUnit 4.x)
- [ ] T003 [P] Create default config.ini in src/main/resources with [xbond], [future], [ddb] sections
- [ ] T004 [P] Create logback.xml in src/main/resources for SLF4J logging configuration
- [ ] T005 [P] Create README.md in repository root with project overview and build instructions

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Utility Classes (Shared Across All Stories)

- [ ] T006 [P] Create DateUtils utility class in src/main/java/com/histdata/etl/util/DateUtils.java with date parsing methods (yyyyMMdd, yyyy-MM-dd, yyyy-MM-dd HH:mm:ss.SSS)
- [ ] T007 [P] Create FileLock utility class in src/main/java/com/histdata/etl/util/FileLock.java with NIO FileLock mechanism and PID file management
- [ ] T008 [P] Create ProgressMonitor utility class in src/main/java/com/histdata/etl/util/ProgressMonitor.java with console progress display and status tracking

### Configuration Management (Shared Across All Stories)

- [ ] T009 Create Config interface in src/main/java/com/histdata/etl/config/Config.java
- [ ] T010 [P] Create CosConfig model in src/main/java/com/histdata/etl/config/CosConfig.java with validation rules
- [ ] T011 [P] Create MySqlConfig model in src/main/java/com/histdata/etl/config/MySqlConfig.java with validation rules
- [ ] T012 [P] Create DolphinDbConfig model in src/main/java/com/histdata/etl/config/DolphinDbConfig.java with validation rules
- [ ] T013 Implement IniConfigLoader class in src/main/java/com/histdata/etl/config/IniConfigLoader.java using Apache Commons Configuration 2.9.0 to parse INI files and load config objects
- [ ] T014 Create ConfigTest in src/test/java/com/histdata/etl/config/ConfigTest.java with unit tests for Config interface
- [ ] T015 Create IniConfigLoaderTest in src/test/java/com/histdata/etl/config/IniConfigLoaderTest.java with unit tests for valid, invalid, and missing configurations

### Model Classes (Shared Across All Stories)

- [ ] T016 [P] Create EtlJobContext model in src/main/java/com/histdata/etl/model/EtlJobContext.java with fields (startDate, endDate, configPath, config, connections, jobId) and state transitions
- [ ] T017 [P] Create ProgressStatus model in src/main/java/com/histdata/etl/model/ProgressStatus.java with fields (currentDate, record counts, timestamps, status)
- [ ] T018 [P] Create JobStatus enum in src/main/java/com/histdata/etl/model/JobStatus.java with values (INITIALIZED, CONNECTING, EXTRACTING, TRANSFORMING, LOADING, COMPLETED, FAILED)
- [ ] T019 Create EtlJobContextTest in src/test/java/com/histdata/etl/model/EtlJobContextTest.java with unit tests for validation rules and state transitions
- [ ] T020 Create ProgressStatusTest in src/test/java/com/histdata/etl/model/ProgressStatusTest.java with unit tests for validation rules

### DataSourceExtractor Interface (Shared Across All Stories)

- [ ] T021 Create DataSourceExtractor interface in src/main/java/com/histdata/etl/datasource/DataSourceExtractor.java with extract(LocalDate businessDate) method signature
- [ ] T022 Create CosExtractor implementation in src/main/java/com/histdata/etl/datasource/CosExtractor.java implementing DataSourceExtractor for COS CSV file extraction with streaming downloads
- [ ] T023 Create XbondQuoteExtractor in src/main/java/com/histdata/etl/datasource/XbondQuoteExtractor.java extending CosExtractor for AllPriceDepth CSV extraction with filtering by business date
- [ ] T024 Create XbondTradeExtractor in src/main/java/com/histdata/etl/datasource/XbondTradeExtractor.java extending CosExtractor for XbondCfetsDeal CSV extraction with filtering by business date
- [ ] T025 Create MySqlFutureExtractor in src/main/java/com/histdata/etl/datasource/MySqlFutureExtractor.java implementing DataSourceExtractor for MySQL fut_tick table extraction with JDBC queries

### DataLoader Interface (Shared Across All Stories)

- [ ] T026 Create DataLoader interface in src/main/java/com/histdata/etl/loader/DataLoader.java with load(List<?> records) method signature
- [ ] T027 Create DolphinDbLoader implementation in src/main/java/com/histdata/etl/loader/DolphinDbLoader.java implementing DataLoader with batch insert (10,000 records per batch), connection management, and cleanup logic
- [ ] T028 [P] Implement DolphinDB temporary table creation in DolphinDbLoader with SQL scripts from plan.md (xbond_quote_stream_temp, xbond_trade_stream_temp, market_price_stream_temp, fut_market_price_stream_temp)
- [ ] T029 [P] Implement DolphinDB temporary table deletion in DolphinDbLoader with cleanup on success and error scenarios

### Utility Unit Tests (Constitutional Requirement - Every Class Must Have Tests)

- [ ] T030 [P] Create DateUtilsTest in src/test/java/com/histdata/etl/util/DateUtilsTest.java with unit tests for all date parsing methods and edge cases
- [ ] T031 [P] Create FileLockTest in src/test/java/com/histdata/etl/util/FileLockTest.java with unit tests for lock acquisition, release, stale lock detection, and concurrent execution scenarios
- [ ] T032 [P] Create ProgressMonitorTest in src/test/java/com/histdata/etl/util/ProgressMonitorTest.java with unit tests for progress display, status updates, and percentage calculations

### DataSourceExtractor Unit Tests

- [ ] T033 [P] Create CosExtractorTest in src/test/java/com/histdata/etl/datasource/CosExtractorTest.java with unit tests for COS connection, file listing, and download with mocks
- [ ] T034 [P] Create XbondQuoteExtractorTest in src/test/java/com/histdata/etl/datasource/XbondQuoteExtractorTest.java with unit tests for CSV parsing, filtering, and grouping logic
- [ ] T035 [P] Create XbondTradeExtractorTest in src/test/java/com/histdata/etl/datasource/XbondTradeExtractorTest.java with unit tests for CSV parsing and field extraction
- [ ] T036 [P] Create MySqlFutureExtractorTest in src/test/java/com/histdata/etl/datasource/MySqlFutureExtractorTest.java with unit tests for JDBC queries, result processing, and error handling

### DataLoader Unit Tests

- [ ] T037 [P] Create DolphinDbLoaderTest in src/test/java/com/histdata/etl/loader/DolphinDbLoaderTest.java with unit tests for table creation, batch loading, connection management, and cleanup

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Single Day ETL Execution (Priority: P1) 🎯 MVP

**Goal**: Extract, transform, and load data from all three sources for a single business day with progress display

**Independent Test**: Run tool with start date = end date (e.g., 20250101 20250101) and verify all data sources are extracted, combined, sorted by receive_time, and loaded into DolphinDB with progress displayed

### Record Models for User Story 1

- [ ] T038 [US1] Create XbondQuoteRecord model in src/main/java/com/histdata/etl/model/XbondQuoteRecord.java with all fields from data-model.md (businessDate, exchProductId, productType, exchange, source, settleSpeed, level, status, price fields, bid/offer fields 0-5, eventTime, receiveTime)
- [ ] T039 [US1] Create XbondTradeRecord model in src/main/java/com/histdata/etl/model/XbondTradeRecord.java with all fields from data-model.md (businessDate, exchProductId, productType, exchange, source, settleSpeed, lastTrade fields, eventTime, receiveTime)
- [ ] T040 [US1] Create FutureQuoteRecord model in src/main/java/com/histdata/etl/model/FutureQuoteRecord.java with all fields from data-model.md (businessDate, exchProductId, productType, exchange, source, settleSpeed, level, status, OHLCV fields, bid/offer fields 0-4, eventTime, receiveTime)

### DataTransformer Interface for User Story 1

- [ ] T041 [US1] Create DataTransformer interface in src/main/java/com/histdata/etl/transformer/DataTransformer.java with transform(Object rawRecord) method signature
- [ ] T042 [US1] Create XbondQuoteTransformer in src/main/java/com/histdata/etl/transformer/XbondQuoteTransformer.java implementing DataTransformer with grouping by mq_offset, sorting by level/side, field mapping, and receive_time null handling with warnings
- [ ] T043 [US1] Create XbondTradeTransformer in src/main/java/com/histdata/etl/transformer/XbondTradeTransformer.java implementing DataTransformer with direct field mapping, side mapping (X→TKN, Y→GVN, Z→TRD, D→DONE), and receive_time fallback logic
- [ ] T044 [US1] Create FutureQuoteTransformer in src/main/java/com/histdata/etl/transformer/FutureQuoteTransformer.java implementing DataTransformer with array unwinding (bid_prices, ask_prices, bid_qty, ask_qty), event_time parsing (action_date + action_time), and receive_time fallback logic

### Record Model Unit Tests for User Story 1

- [ ] T045 [P] [US1] Create XbondQuoteRecordTest in src/test/java/com/histdata/etl/model/XbondQuoteRecordTest.java with unit tests for all field validations (receiveTime required, settleSpeed 0/1, yield types MATURITY/EXERCISE, prices non-negative)
- [ ] T046 [P] [US1] Create XbondTradeRecordTest in src/test/java/com/histdata/etl/model/XbondTradeRecordTest.java with unit tests for all field validations (lastTradeSide enum values, lastTradeVolume positive, lastTradePrice non-negative)
- [ ] T047 [P] [US1] Create FutureQuoteRecordTest in src/test/java/com/histdata/etl/model/FutureQuoteRecordTest.java with unit tests for all field validations (receiveTime required, settleSpeed always 1, volume fields non-negative)

### DataTransformer Unit Tests for User Story 1

- [ ] T048 [P] [US1] Create XbondQuoteTransformerTest in src/test/java/com/histdata/etl/transformer/XbondQuoteTransformerTest.java with unit tests for grouping logic, sorting logic, field mappings, and receive_time null handling
- [ ] T049 [P] [US1] Create XbondTradeTransformerTest in src/test/java/com/histdata/etl/transformer/XbondTradeTransformerTest.java with unit tests for field mappings, side mappings, date parsing, and receive_time fallback
- [ ] T050 [P] [US1] Create FutureQuoteTransformerTest in src/test/java/com/histdata/etl/transformer/FutureQuoteTransformerTest.java with unit tests for array unwinding, date parsing, and receive_time fallback

### CLI Entry Point for User Story 1

- [ ] T051 [US1] Create EtlCli main class in src/main/java/com/histdata/etl/cli/EtlCli.java with argument parsing (START_DATE, END_DATE, CONFIG_FILE), validation (date format, start <= end), and main orchestration logic
- [ ] T052 [US1] Implement single-day processing logic in EtlCli with connection initialization, memory check, data extraction (parallel), transformation, sorting by receive_time, loading, progress display, and cleanup
- [ ] T053 [US1] Implement memory pre-flight check in EtlCli using Runtime.getRuntime() to calculate estimated memory requirement and fail gracefully if > 90% of available memory with clear error message
- [ ] T054 [US1] Implement progress display in EtlCli using ProgressMonitor to show real-time status (current date, data source progress, completion percentage, elapsed time)

### CLI Unit Tests for User Story 1

- [ ] T055 [US1] Create EtlCliTest in src/test/java/com/histdata/etl/cli/EtlCliTest.java with unit tests for argument validation, error handling, and orchestration logic

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently with all data sources

---

## Phase 4: User Story 2 - Multi-Day Batch ETL Execution (Priority: P1)

**Goal**: Process data across a date range sequentially, completing all operations for each day before starting the next

**Independent Test**: Run tool with multi-day date range (e.g., 20250101 20250103) and verify each day processes completely in sequence, errors stop processing, and all data is loaded with proper receive_time ordering

### Multi-Day Processing Logic

- [ ] T056 [US2] Implement date range iteration in EtlCli to process each business day from startDate to endDate sequentially
- [ ] T057 [US2] Implement error handling for multi-day processing in EtlCli to stop processing on error and not proceed to next day with clear error context
- [ ] T058 [US2] Update progress display in EtlCli to show multi-day progress (current day number, total days, completion across days)

### Multi-Day Unit Tests

- [ ] T059 [US2] Add multi-day processing tests to EtlCliTest in src/test/java/com/histdata/etl/cli/EtlCliTest.java for sequential processing, error handling, and progress display

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Configuration Management (Priority: P2)

**Goal**: Read and validate INI configuration files, establish connections to all systems

**Independent Test**: Provide valid INI file with connection details and verify tool connects to COS, MySQL, and DolphinDB; also test with invalid/missing configuration for proper error handling

### Configuration Validation

- [ ] T060 [US3] Implement configuration file validation in IniConfigLoader to check for required sections ([xbond], [future], [ddb]) and required fields
- [ ] T061 [US3] Implement configuration validation in IniConfigLoader to validate connection parameters (ports, hosts, URLs) and fail with clear error messages for invalid/missing parameters
- [ ] T062 [US3] Add embedded config.ini fallback in EtlCli to use default configuration from src/main/resources when CONFIG_FILE not provided
- [ ] T063 [US3] Add COS credential support in EtlCli to read from JVM parameters (-Dcos.secret.id, -Dcos.secret.key, -Dcos.trust.key)

### Connection Management

- [ ] T064 [US3] Implement COS connection in CosExtractor using credentials from CosConfig and JVM parameters with retry logic for transient failures
- [ ] T065 [US3] Implement MySQL connection in MySqlFutureExtractor using MySqlConfig with connection pooling and error handling
- [ ] T066 [US3] Implement DolphinDB connection in DolphinDbLoader using DolphinDbConfig with connection management and proper cleanup in try-finally blocks

### Configuration Unit Tests

- [ ] T067 [P] [US3] Add configuration validation tests to IniConfigLoaderTest for valid, invalid, and missing configuration scenarios
- [ ] T068 [P] [US3] Add connection tests to CosExtractorTest for successful and failed connection scenarios
- [ ] T069 [P] [US3] Add connection tests to MySqlFutureExtractorTest for successful and failed connection scenarios
- [ ] T070 [P] [US3] Add connection tests to DolphinDbLoaderTest for successful and failed connection scenarios

**Checkpoint**: At this point, User Stories 1, 2, AND 3 should all work independently

---

## Phase 6: User Story 4 - DolphinDB Temporary Table Lifecycle (Priority: P2)

**Goal**: Create temporary stream tables at start of execution and delete them on completion (success or error)

**Independent Test**: Run ETL tool and verify temporary tables exist during execution and are cleaned up after completion in both success and failure scenarios

### Temporary Table Management

- [ ] T071 [US4] Implement temporary table creation in DolphinDbLoader with SQL scripts from plan.md (create stream tables, create reactive engines, subscribe tables)
- [ ] T072 [US4] Implement temporary table cleanup in DolphinDbLoader with SQL scripts from plan.md (unsubscribe tables, drop engines, drop stream tables)
- [ ] T073 [US4] Implement cleanup on error scenario in DolphinDbLoader using try-finally or shutdown hooks to ensure tables are deleted even if process fails
- [ ] T074 [US4] Implement shutdown hook in EtlCli to call DolphinDbLoader.cleanup() on JVM termination for graceful resource cleanup

### Temporary Table Unit Tests

- [ ] T075 [P] [US4] Add temporary table creation tests to DolphinDbLoaderTest for successful table creation
- [ ] T076 [P] [US4] Add temporary table cleanup tests to DolphinDbLoaderTest for successful cleanup
- [ ] T077 [P] [US4] Add error scenario cleanup tests to DolphinDbLoaderTest for cleanup on failure

**Checkpoint**: All user stories should now be independently functional

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

### Error Handling & Exit Codes

- [ ] T078 [P] Implement custom exception classes in appropriate packages (ConcurrentExecutionException, InsufficientMemoryException, ConfigurationException, ExtractionException, TransformationException, LoadingException) in src/main/java/com/histdata/etl/exception/
- [ ] T079 [P] Implement exit code handling in EtlCli with exit codes: 0 (Success), 1 (Invalid Arguments), 2 (Configuration Error), 3 (Connection Error), 4 (Extraction Error), 5 (Transformation Error), 6 (Loading Error), 7 (Memory Error), 8 (Concurrent Execution), 9 (Unexpected Error)
- [ ] T080 [P] Add --version option to EtlCli to display version information (v1.0.0, build date, Java version)
- [ ] T081 [P] Add --help option to EtlCli to display usage instructions with all parameters, options, and examples

### Logging & Diagnostics

- [ ] T082 [P] Enhance logging in EtlCli and all components to log at appropriate levels (DEBUG, INFO, WARN, ERROR) with context (dates, record counts, connection details)
- [ ] T083 [P] Add logging to FileLock for lock acquisition/release and stale lock detection
- [ ] T084 [P] Add logging to ProgressMonitor for progress updates and status changes
- [ ] T085 [P] Add detailed error logging with context and suggestions for troubleshooting (error codes, connection details, file paths)

### Performance Optimization

- [ ] T086 [P] Optimize CSV parsing in CosExtractor subclasses to use streaming API (Apache Commons CSV) for memory efficiency with large files
- [ ] T087 [P] Optimize batch loading in DolphinDbLoader to use 10,000 records per batch for network efficiency
- [ ] T088 [P] Optimize parallel data extraction in EtlCli to use ExecutorService or CompletableFuture for concurrent extraction from all three data sources
- [ ] T089 [P] Optimize memory usage by using primitive arrays or compact data structures in transformers where possible

### Security Hardening

- [ ] T090 [P] Validate that COS credentials are never hardcoded in code and only read from JVM parameters
- [ ] T091 [P] Add recommendations in README.md for storing credentials securely (environment variables, credential managers)
- [ ] T092 [P] Add file permission warnings for configuration files (chmod 600 recommended)

### Documentation Updates

- [ ] T093 [P] Update README.md with comprehensive setup instructions, configuration examples, usage examples, and troubleshooting guide aligned with quickstart.md
- [ ] T094 [P] Add performance tuning section to README.md with memory allocation recommendations for different data volumes
- [ ] T095 [P] Add integration examples to README.md for cron, Windows Task Scheduler, and CI/CD pipelines

### Additional Edge Case Handling

- [ ] T096 [P] Implement no-data warning in EtlCli to log warning and continue processing when business date contains no data in any source system (treat as expected scenario, not error)
- [ ] T097 [P] Implement stale lock detection in FileLock to check if PID from .etl-tool.pid is still running and clear stale locks automatically
- [ ] T098 [P] Implement connection timeout and retry logic in all extractors and loaders for transient network failures

### Final Integration Tests

- [ ] T099 [P] Create end-to-end integration test in src/test/java/com/histdata/etl/integration/EndToEndEtlTest.java with real database connections for single-day ETL
- [ ] T100 [P] Create multi-day integration test in src/test/java/com/histdata/etl/integration/MultiDayEtlTest.java for sequential processing across multiple days
- [ ] T101 [P] Create error scenario integration test in src/test/java/com/histdata/etl/integration/ErrorRecoveryTest.java for cleanup on error

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User Story 1 (P1): Can start after Foundational - No dependencies on other stories
  - User Story 2 (P1): Can start after Foundational and User Story 1 - Extends US1 with date range iteration
  - User Story 3 (P2): Can start after Foundational - Independent of US1/US2 (configuration is prerequisite)
  - User Story 4 (P2): Can start after Foundational - Integrates with US1 (adds lifecycle management)
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Depends on User Story 1 - Extends single-day ETL to multi-day processing
- **User Story 3 (P2)**: Can start after Foundational - Independent of US1/US2 (configuration is shared infrastructure)
- **User Story 4 (P2)**: Depends on User Story 1 - Adds temporary table lifecycle management to ETL process

### Within Each User Story

- Unit tests MUST be written before implementation (Constitutional requirement)
- Models before services/transformers
- Transformers before CLI orchestration
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks (Phase 1) marked [P] can run in parallel
- All Foundational tasks (Phase 2) marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, User Stories 3 and 4 can start in parallel with User Stories 1 and 2 (if team capacity allows)
- All unit tests marked [P] within each phase can run in parallel
- All models marked [P] within a user story can run in parallel
- User Story 3 (P2) and User Story 4 (P2) can run in parallel as they are independent
- All Polish tasks marked [P] in Phase 7 can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all model creation tasks together (T038, T039, T040):
Task: "Create XbondQuoteRecord model in src/main/java/com/histdata/etl/model/XbondQuoteRecord.java"
Task: "Create XbondTradeRecord model in src/main/java/com/histdata/etl/model/XbondTradeRecord.java"
Task: "Create FutureQuoteRecord model in src/main/java/com/histdata/etl/model/FutureQuoteRecord.java"

# Launch all transformer creation tasks together (T042, T043, T044):
Task: "Create XbondQuoteTransformer in src/main/java/com/histdata/etl/transformer/XbondQuoteTransformer.java"
Task: "Create XbondTradeTransformer in src/main/java/com/histdata/etl/transformer/XbondTradeTransformer.java"
Task: "Create FutureQuoteTransformer in src/main/java/com/histdata/etl/transformer/FutureQuoteTransformer.java"

# Launch all model unit tests together (T045, T046, T047):
Task: "Create XbondQuoteRecordTest in src/test/java/com/histdata/etl/model/XbondQuoteRecordTest.java"
Task: "Create XbondTradeRecordTest in src/test/java/com/histdata/etl/model/XbondTradeRecordTest.java"
Task: "Create FutureQuoteRecordTest in src/test/java/com/histdata/etl/model/FutureQuoteRecordTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T005)
2. Complete Phase 2: Foundational (T006-T037) - CRITICAL - blocks all stories
3. Complete Phase 3: User Story 1 (T038-T055)
4. **STOP and VALIDATE**: Test User Story 1 independently with single-day ETL
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Add User Story 4 → Test independently → Deploy/Demo
6. Complete Phase 7: Polish → Final production-ready release
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together (T001-T037)
2. Once Foundational is done:
   - Developer A: User Story 1 (T038-T055)
   - Developer B: User Story 3 (T060-T070) - Can start in parallel
3. User Story 1 completes → Developer A starts User Story 2 (T056-T059)
4. User Story 1 and 2 complete → Developer A starts User Story 4 (T071-T077)
5. All user stories complete → Team completes Phase 7: Polish (T078-T101)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Unit tests are MANDATORY per constitution - every class must have corresponding tests
- Verify tests fail before implementing (TDD approach recommended)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence

---

## Task Summary

**Total Tasks**: 101
**Tasks per User Story**:
  - User Story 1 (P1): 18 tasks (T038-T055)
  - User Story 2 (P2): 4 tasks (T056-T059)
  - User Story 3 (P2): 11 tasks (T060-T070)
  - User Story 4 (P2): 7 tasks (T071-T077)

**Parallel Opportunities Identified**: 69 tasks marked [P] for parallel execution
  - Setup phase: 5 tasks parallelizable
  - Foundational phase: 32 tasks parallelizable
  - User Story 1: 6 tasks parallelizable
  - User Story 2: 0 tasks parallelizable
  - User Story 3: 4 tasks parallelizable
  - User Story 4: 4 tasks parallelizable
  - Polish phase: 18 tasks parallelizable

**Independent Test Criteria**:
  - User Story 1: Run with single date, verify all sources extracted/transformed/loaded, progress displayed, exit code 0
  - User Story 2: Run with multi-day range, verify sequential processing, error stops execution, proper ordering maintained
  - User Story 3: Provide valid INI, verify connections established; test invalid/missing config for error handling
  - User Story 4: Run ETL, verify tables created during execution, cleaned up on success/error

**Suggested MVP Scope**: User Story 1 Only (T001-T055) - Single-day ETL with all data sources, progress display, and proper error handling

**Format Validation**: ✅ ALL tasks follow checklist format (checkbox, ID, labels, file paths)
