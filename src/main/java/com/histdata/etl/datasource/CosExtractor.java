package com.histdata.etl.datasource;

import com.histdata.etl.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Base extractor for COS (Cloud Object Storage) CSV files.
 */
public abstract class CosExtractor implements DataSourceExtractor<CSVRecord> {
    private static final Logger logger = LoggerFactory.getLogger(CosExtractor.class);

    protected COSClient cosClient;
    protected CosConfig config;

    public CosExtractor(CosConfig config) {
        this.config = config;
    }

    @Override
    public void initialize() {
        COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new com.qcloud.cos.region.Region(config.getRegion()));
        cosClient = new COSClient(cred, clientConfig);
        logger.info("COS client initialized for bucket: {}", config.getBucket());
    }

    @Override
    public List<CSVRecord> extract(LocalDate businessDate) throws Exception {
        if (cosClient == null) {
            initialize();
        }

        String key = getFilePath(businessDate);
        logger.info("Extracting from COS: {}", key);

        GetObjectRequest request = new GetObjectRequest(config.getBucket(), key);
        COSObject cosObject = cosClient.getObject(request);

        List<CSVRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(cosObject.getObjectContent()));
             CSVParser parser = CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim().parse(reader)) {

            for (CSVRecord record : parser) {
                if (matchesBusinessDate(record, businessDate)) {
                    records.add(record);
                }
            }
        } finally {
            cosObject.close();
        }

        logger.info("Extracted {} records from {}", records.size(), key);
        return records;
    }

    @Override
    public void close() {
        if (cosClient != null) {
            cosClient.shutdown();
            cosClient = null;
            logger.info("COS client closed");
        }
    }

    protected abstract String getFilePath(LocalDate businessDate);

    protected abstract boolean matchesBusinessDate(CSVRecord record, LocalDate businessDate);
}
