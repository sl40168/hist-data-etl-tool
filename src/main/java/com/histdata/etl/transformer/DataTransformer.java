package com.histdata.etl.transformer;

import java.time.LocalDate;

/**
 * Interface for transforming raw data records into domain-specific record models.
 * Each data source type has its own transformer implementation.
 */
public interface DataTransformer<T> {
    /**
     * Transform a raw data record into target domain record type.
     *
     * @param rawRecord raw record from data source
     * @param businessDate business date for this ETL job
     * @return transformed domain record, or null if record should be skipped
     * @throws TransformationException if transformation fails
     */
    T transform(Object rawRecord, LocalDate businessDate) throws Exception;
}
