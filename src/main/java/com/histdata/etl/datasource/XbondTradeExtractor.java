package com.histdata.etl.datasource;

import com.histdata.etl.config.CosConfig;
import org.apache.commons.csv.CSVRecord;

import java.time.LocalDate;

/**
 * Extractor for XBond Trade (XbondCfetsDeal) CSV files from COS.
 */
public class XbondTradeExtractor extends CosExtractor {
    private static final String FILE_PATTERN = "/XbondCfetsDeal/YYYY-MM-DD/*.csv";

    public XbondTradeExtractor(CosConfig config) {
        super(config);
    }

    @Override
    protected String getFilePath(LocalDate businessDate) {
        return FILE_PATTERN.replace("YYYY-MM-DD", businessDate.toString());
    }

    @Override
    protected boolean matchesBusinessDate(CSVRecord record, LocalDate businessDate) {
        // File path is already filtered to correct date directory
        // All records in this directory belong to the same business date
        return true;
    }
}
