# Historical ETL Tool Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-01-06

## Active Technologies

- **Language**: Java 1.8 (Mandatory per constitution)
- **Build Tool**: Maven 3.6.3
- **CSV Parsing**: Apache Commons CSV 1.10.0
- **INI Configuration**: Apache Commons Configuration 2.9.0
- **MySQL JDBC**: MySQL Connector/J 8.0.33
- **DolphinDB API**: dolphindb-javaapi 3.00.0.2
- **COS SDK**: cos_api 5.6.3
- **Logging**: SLF4J 1.7.x + Logback 1.2.x
- **JSON (if needed)**: Jackson 2.13.x

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/histdata/etl/
│   │       ├── cli/               # CLI entry point
│   │       ├── config/            # Configuration management
│   │       ├── datasource/        # Data source extraction (interface-based)
│   │       ├── transformer/       # Data transformation (interface-based)
│   │       ├── loader/            # Data loading (interface-based)
│   │       ├── model/             # Domain models
│   │       └── util/              # Utilities
│   └── resources/
│       ├── config.ini             # Default configuration
│       └── logback.xml            # Logging configuration
└── test/
    └── java/
        └── com/histdata/etl/
            ├── cli/
            ├── config/
            ├── datasource/
            ├── transformer/
            ├── loader/
            └── util/
```

## Commands

### Maven Commands

```bash
# Build the project
mvn clean package

# Run tests
mvn test

# Run a specific test
mvn test -Dtest=XbondQuoteTransformerTest

# Build with dependencies
mvn clean package -DskipTests

# Generate dependency tree
mvn dependency:tree
```

### Running the Application

```bash
# Run single-day ETL with default config
java -jar target/etl-tool-1.0.0.jar 20250101 20250101

# Run multi-day ETL with custom config
java -jar target/etl-tool-1.0.0.jar 20250101 20250131 config.ini

# Run with increased memory
java -Xmx4g -jar target/etl-tool-1.0.0.jar 20250101 20250131 config.ini

# Run with COS credentials
java -Dcos.secret.id=YOUR_SECRET_ID \
     -Dcos.secret.key=YOUR_SECRET_KEY \
     -Dcos.trust.key=YOUR_TRUST_KEY \
     -jar target/etl-tool-1.0.0.jar 20250101 20250131 config.ini
```

## Code Style

### Java Coding Standards

- **Naming Conventions**:
  - Classes: PascalCase (e.g., `XbondQuoteRecord`)
  - Methods: camelCase (e.g., `extractData`)
  - Constants: UPPER_SNAKE_CASE (e.g., `RECORD_SIZE_BYTES`)
  - Packages: lowercase (e.g., `com.histdata.etl.datasource`)

- **Interface-Based Design**:
  - All component boundaries must be defined through Java interfaces
  - Public APIs must expose interface types, not concrete classes
  - Implementation details must be hidden behind interface abstractions
  - Example: `DataSourceExtractor` interface with `CosExtractor`, `XbondQuoteExtractor` implementations

- **Error Handling**:
  - Use custom exceptions for domain-specific errors (e.g., `InsufficientMemoryException`, `ConcurrentExecutionException`)
  - Provide clear, actionable error messages
  - Log errors at ERROR level with context
  - Use proper exception chaining

- **Logging**:
  - Use SLF4J for logging (not System.out.println)
  - Log levels: DEBUG (detailed info), INFO (general progress), WARN (non-critical issues), ERROR (failures)
  - Include context in log messages (e.g., dates, record counts, connection details)

- **Testing**:
  - Every class must have corresponding unit tests
  - Tests should be written in parallel with production code
  - Use JUnit for unit testing
  - Mock external dependencies (COS, MySQL, DolphinDB) in unit tests
  - Tests should be isolated and fast

- **Resource Management**:
  - Use try-with-resources for AutoCloseable objects (connections, streams, locks)
  - Implement proper cleanup in finally blocks if try-with-resources not available
  - Register shutdown hooks for critical resources (file locks, database connections)

- **Performance**:
  - Process CSV files line-by-line using streaming APIs (not load entire file into memory)
  - Use efficient data structures for large datasets (primitive arrays vs. objects)
  - Implement batch operations for database inserts (10,000 records per batch)
  - Pre-check memory requirements before processing large datasets

### INI File Format

```ini
[section]
key = value
another_key = another_value
```

- Use standard INI format (sections, key=value pairs)
- Section names in square brackets: `[section]`
- Key-value pairs with `=` separator
- No quotes around values
- Comments start with `#` or `;`

### CSV File Format

- Use Apache Commons CSV for parsing
- Support quoted and unquoted fields
- Handle empty fields and null values
- Stream processing for large files

## Constitutional Requirements

### Core Principles

1. **Maven Build Tool**: Project MUST use Maven 3.6.3
2. **Java 8 Compatibility**: Project MUST target Java 1.8
3. **CLI Interface**: All functionality MUST be accessible through CLI
4. **INI Configuration**: All runtime configuration MUST use `.ini` files
5. **Unit Test Coverage**: Every class MUST have corresponding unit tests
6. **Well-Known Open Source Components**: All dependencies MUST be well-known, open-source libraries
7. **Interface-Based Component Boundaries**: All component boundaries MUST be defined through interfaces

### Technology Stack

- Build Tool: Maven 3.6.3 (strict)
- Language: Java 1.8 (strict)
- Interface: CLI (strict)
- Configuration: INI files (strict)
- Testing Framework: JUnit (recommended)
- Library Policy: Well-known, open-source components only
- Architecture: Interface-based component boundaries

## Recent Changes

### Feature 003: ETL CLI Tool (Current)

Added ETL CLI Tool for extracting financial data from multiple sources:
- COS CSV files (XBond Market Quote, XBond Trade)
- MySQL database (Bond Future L2 Quote)
- Loading into DolphinDB temporary tables
- Multi-day batch processing with sequential execution
- Single-instance enforcement via file locking
- Memory management with pre-flight checks
- Progress monitoring and reporting

**Technologies Added**:
- Apache Commons CSV 1.10.0
- Apache Commons Configuration 2.9.0
- MySQL Connector/J 8.0.33
- DolphinDB Java API 3.00.0.2
- COS SDK 5.6.3

**Architecture**:
- Interface-based design for DataSourceExtractor, DataTransformer, DataLoader
- Maven standard directory structure
- Unit tests for every class

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
