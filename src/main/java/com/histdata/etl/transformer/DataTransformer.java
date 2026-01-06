package com.histdata.etl.transformer;

/**
 * Interface for transforming raw data records into domain-specific record models.
 * Each data source type has its own transformer implementation.
 */
public interface DataTransformer<T> {
    /**
     * Transform a raw data record into the target domain record type.
     *
     * @param rawRecord the raw record from the data source
     * @return the transformed domain record, or null if the record should be skipped
     * @throws TransformationException if the transformation fails
     */
    T transform(Object rawRecord) throws Exception;
}
