package com.histdata.etl.datasource;

import com.histdata.etl.config.CosConfig;
import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class XbondQuoteExtractorTest {

    private CosConfig mockConfig;
    private XbondQuoteExtractor extractor;

    @Before
    public void setUp() {
        mockConfig = new CosConfig();
        mockConfig.setSecretId("test-secret-id");
        mockConfig.setSecretKey("test-secret-key");
        mockConfig.setBucket("test-bucket");
        mockConfig.setRegion("ap-beijing");
        mockConfig.setDomain("https://ap-beijing.myqcloud.com");
        extractor = new XbondQuoteExtractor(mockConfig);
    }

    @Test
    public void testGetFilePath_YYYYMMDD_Format() {
        LocalDate testDate = LocalDate.of(2025, 1, 7);
        String filePath = extractor.getFilePath(testDate);
        assertEquals("/AllPriceDepth/20250107/*.csv", filePath);
    }

    @Test
    public void testGetFilePath_VariousDates() {
        assertEquals("/AllPriceDepth/20250728/*.csv", extractor.getFilePath(LocalDate.of(2025, 7, 28)));
        assertEquals("/AllPriceDepth/20250101/*.csv", extractor.getFilePath(LocalDate.of(2025, 1, 1)));
        assertEquals("/AllPriceDepth/20241231/*.csv", extractor.getFilePath(LocalDate.of(2024, 12, 31)));
    }

    @Test
    public void testMatchesBusinessDate_AlwaysReturnsTrue() {
        assertTrue("Should always return true for filtered directory",
            extractor.matchesBusinessDate(null, LocalDate.of(2025, 1, 7)));
    }
}
