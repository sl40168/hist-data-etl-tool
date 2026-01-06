package com.histdata.etl.loader;

import java.util.List;

/**
 * Interface for loading data into target systems.
 * Each target system type (DolphinDB, etc.) has its own loader implementation.
 */
public interface DataLoader {
    /**
     * Load a batch of records into the target system.
     *
     * @param records list of records to load
     * @throws Exception if loading fails
     */
    void load(List<?> records) throws Exception;

    /**
     * Initialize the loader with configuration.
     *
     * @throws Exception if initialization fails
     */
    void initialize() throws Exception;

    /**
     * Close any resources used by the loader.
     *
     * @throws Exception if cleanup fails
     */
    void close() throws Exception;

    /**
     * Create temporary tables in the target system.
     *
     * @throws Exception if table creation fails
     */
    void createTemporaryTables() throws Exception;

    /**
     * Clean up temporary tables in the target system.
     *
     * @throws Exception if cleanup fails
     */
    void cleanup() throws Exception;
}
