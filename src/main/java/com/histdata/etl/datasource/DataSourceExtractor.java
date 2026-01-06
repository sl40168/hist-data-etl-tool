package com.histdata.etl.datasource;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for extracting data from source systems.
 * Each data source type (COS, MySQL, etc.) has its own extractor implementation.
 */
public interface DataSourceExtractor<T> {
    /**
     * Extract data for a given business date from the data source.
     *
     * @param businessDate the business date to extract data for
     * @return list of raw data records
     * @throws Exception if extraction fails
     */
    List<T> extract(LocalDate businessDate) throws Exception;

    /**
     * Initialize the extractor with configuration.
     *
     * @throws Exception if initialization fails
     */
    default void initialize() throws Exception {
    }

    /**
     * Close any resources used by the extractor.
     *
     * @throws Exception if cleanup fails
     */
    default void close() throws Exception {
    }
}
