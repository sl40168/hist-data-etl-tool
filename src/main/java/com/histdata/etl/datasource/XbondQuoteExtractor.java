package com.histdata.etl.datasource;

import com.histdata.etl.config.CosConfig;
import org.apache.commons.csv.CSVRecord;

import java.time.LocalDate;

/**
 * Extractor for XBond Market Quote (AllPriceDepth) CSV files from COS.
 */
public class XbondQuoteExtractor extends CosExtractor {
    private static final String FILE_PATTERN = "/AllPriceDepth/YYYY-MM-DD/*.csv";

    public XbondQuoteExtractor(CosConfig config) {
        super(config);
    }

    @Override
    protected String getFilePath(LocalDate businessDate) {
        return FILE_PATTERN.replace("YYYY-MM-DD", businessDate.toString());
    }

    @Override
    protected boolean matchesBusinessDate(CSVRecord record, LocalDate businessDate) {
        String recordDateStr = record.get("business_date");
        LocalDate recordDate = LocalDate.parse(recordDateStr.replace("-", ""));
        return recordDate.equals(businessDate);
    }
}
