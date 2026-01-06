package com.histdata.etl.datasource;

import com.histdata.etl.config.CosConfig;
import org.apache.commons.csv.CSVRecord;

import java.time.LocalDate;

/**
 * Extractor for XBond Trade (XbondCfetsDeal) CSV files from COS.
 */
public class XbondTradeExtractor extends CosExtractor {
    private static final String FILE_PATTERN = "xbond/XbondCfetsDeal_YYYYMMDD.csv";

    public XbondTradeExtractor(CosConfig config) {
        super(config);
    }

    @Override
    protected String getFilePath(LocalDate businessDate) {
        return FILE_PATTERN.replace("YYYYMMDD", businessDate.toString().replace("-", ""));
    }

    @Override
    protected boolean matchesBusinessDate(CSVRecord record, LocalDate businessDate) {
        String recordDateStr = record.get("business_date");
        LocalDate recordDate = LocalDate.parse(recordDateStr.replace("-", ""));
        return recordDate.equals(businessDate);
    }
}
