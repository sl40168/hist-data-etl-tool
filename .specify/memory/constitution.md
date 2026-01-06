<!--
Sync Impact Report:
- Version change: 1.1.0 → 1.2.0
- Modified principles: None
- Added sections: VII. Interface-Based Component Boundaries
- Removed sections: None
- Templates requiring updates:
  ✅ plan-template.md - Constitution Check section aligns with new principles
  ✅ spec-template.md - No changes needed (structure still compatible)
  ✅ tasks-template.md - No changes needed (testing discipline preserved)
  ⚠ commands/ - Directory does not exist, no updates needed
- Follow-up TODOs: None
-->

# Historical ETL Constitution

## Core Principles

### I. Maven Build Tool
Project MUST use Maven 3.6.3 as the build and dependency management tool. All build configurations, dependency declarations, and lifecycle management MUST be defined in `pom.xml`. Maven Central repository is the primary source for dependencies, with any custom repositories explicitly documented.

### II. Java 8 Compatibility
Project MUST target Java 1.8 (Java 8) as the minimum and primary runtime version. Source compatibility, bytecode target, and all dependency selections MUST ensure Java 8 compatibility. No language features from Java 9+ may be used unless absolutely necessary and explicitly justified with backward-compatibility analysis.

### III. CLI Interface
All functionality MUST be accessible through a Command Line Interface. The application MUST support standard CLI patterns: command-line arguments for configuration, stdin for input data streams, stdout for primary output, and stderr for errors and diagnostics. Text-based protocols with both human-readable and machine-readable (JSON) output formats MUST be provided.

### IV. INI Configuration
All runtime configuration MUST be managed through `.ini` configuration files. Configuration files MUST follow standard INI format (sections, key=value pairs). The application MUST support multiple configuration files with clear precedence rules (e.g., default, user, override). Configuration validation MUST occur at startup with clear error messages for missing or invalid values.

### V. Unit Test Coverage
Every class MUST have corresponding unit tests. Unit tests MUST:
- Be written in parallel with production code (preferably before implementation)
- Cover all public methods and significant private methods
- Use a consistent testing framework (e.g., JUnit)
- Be isolated and fast (no external dependencies)
- Be located in `src/test/java` following Maven conventions
- Achieve meaningful coverage of edge cases and error conditions

### VI. Well-Known Open Source Components
All third-party components and frameworks MUST be well-known, widely-adopted, open-source libraries with:
- Active maintenance and community support
- Stable APIs and documented maturity
- Compatibility with Java 8
- Permissive licenses suitable for commercial use
Examples include Apache Commons, Google Guava, SLF4J, Logback, Jackson, and other industry-standard libraries.

### VII. Interface-Based Component Boundaries
All component boundaries MUST be clearly defined and enforced through interfaces. Components MUST:
- Define public APIs through Java interfaces (not concrete classes)
- Expose only interface types in public method signatures
- Hide implementation details behind interface abstractions
- Support runtime polymorphism to allow interchangeable implementations
- Decouple higher-level modules from lower-level implementation details
- Document the contract each interface enforces (preconditions, postconditions, invariants)

Rationale: Interface-based boundaries enable testability (mocking/stubbing), flexibility (swappable implementations), and maintainability (clear separation of concerns). Direct dependencies on concrete classes violate this principle.

## Technology Stack

- **Build Tool**: Maven 3.6.3 (strict requirement)
- **Language**: Java 1.8 (strict requirement)
- **Interface**: CLI (strict requirement)
- **Configuration**: INI files (strict requirement)
- **Testing Framework**: JUnit (recommended for Java 8)
- **Library Policy**: Well-known, open-source components only (see Principle VI)
- **Architecture**: Interface-based component boundaries (see Principle VII)

## Quality Standards

- **Testing Discipline**: Unit tests are mandatory for every class. No production code should be written without corresponding tests.
- **Configuration Management**: All runtime behavior MUST be configurable via INI files. Hardcoded values are prohibited except for immutable constants.
- **Error Handling**: CLI applications MUST provide clear, actionable error messages on stderr and appropriate exit codes for different failure modes.
- **Code Organization**: Follow Maven directory structure with `src/main/java` for production code and `src/test/java` for tests.
- **Library Selection**: All dependencies MUST be well-known, open-source components with active maintenance and Java 8 compatibility.
- **Component Design**: All component boundaries MUST be defined through interfaces, not concrete classes. Implementation details MUST be hidden behind interface abstractions.

## Governance

This constitution is the authoritative source for all project decisions and MUST supersede any conflicting practices. Development workflows, code reviews, and architectural decisions MUST comply with these principles.

### Amendment Procedure
1. All proposed amendments MUST be documented with rationale and impact analysis
2. Amendments affecting Core Principles require explicit team approval
3. Version MUST be updated according to semantic versioning:
   - MAJOR: Removal or fundamental change to existing principles
   - MINOR: Addition of new principles or significant expansion of guidance
   - PATCH: Clarifications, wording improvements, non-semantic refinements
4. All templates and documentation MUST be updated to reflect amendments
5. Migration plans MUST be provided for any backward-incompatible changes

### Compliance Review
- All pull requests and code reviews MUST verify compliance with this constitution
- Violations of Core Principles MUST be explicitly justified with business or technical necessity
- Complexity beyond what these principles prescribe MUST be documented and approved
- Periodic compliance reviews SHOULD be conducted to ensure ongoing adherence

**Version**: 1.2.0 | **Ratified**: 2026-01-05 | **Last Amended**: 2026-01-06
