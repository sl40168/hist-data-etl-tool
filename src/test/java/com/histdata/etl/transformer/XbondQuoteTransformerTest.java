package com.histdata.etl.transformer;

import com.histdata.etl.model.XbondQuoteRecord;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for XbondQuoteTransformer.
 */
public class XbondQuoteTransformerTest {
    private XbondQuoteTransformer transformer;

    @Before
    public void setUp() {
        transformer = new XbondQuoteTransformer();
    }

    @Test
    public void testInvalidInput() {
        try {
            transformer.transform("invalid");
            fail("Should throw IllegalArgumentException for invalid input");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Expected Map"));
        } catch (Exception e) {
            fail("Should throw IllegalArgumentException");
        }
    }

    @Test
    public void testEmptyMap() throws Exception {
        Map<String, List<CSVRecord>> emptyMap = new HashMap<>();
        XbondQuoteRecord result = transformer.transform(emptyMap);
        assertNull(result);
    }

    @Test
    public void testNullReceiveTime() throws Exception {
        Map<String, List<CSVRecord>> groupedRecords = new HashMap<>();
        List<CSVRecord> records = new ArrayList<>();
        
        CSVRecord record = mock(CSVRecord.class);
        when(record.get("business_date")).thenReturn("20250101");
        when(record.get("underlying_security_id")).thenReturn("210210");
        when(record.get("underlying_settlement_type")).thenReturn("1");
        when(record.get("transact_time")).thenReturn("20250101-09:30:00.000");
        when(record.get("recv_time")).thenReturn("");
        records.add(record);
        
        groupedRecords.put("key", records);
        
        XbondQuoteRecord result = transformer.transform(groupedRecords);
        assertNull(result);
    }

    @Test
    public void testSuccessfulTransformation() throws Exception {
        Map<String, List<CSVRecord>> groupedRecords = new HashMap<>();
        List<CSVRecord> records = new ArrayList<>();
        
        CSVRecord record1 = mock(CSVRecord.class);
        when(record1.get("business_date")).thenReturn("20250101");
        when(record1.get("underlying_security_id")).thenReturn("210210");
        when(record1.get("underlying_settlement_type")).thenReturn("1");
        when(record1.get("transact_time")).thenReturn("20250101-09:30:00.000");
        when(record1.get("recv_time")).thenReturn("20250101-09:30:00.000");
        when(record1.get("underlying_md_entry_type")).thenReturn("0");
        when(record1.get("underlying_md_price_level")).thenReturn("1");
        when(record1.get("underlying_md_entry_px")).thenReturn("100.0");
        when(record1.get("underlying_md_yield")).thenReturn("3.5");
        when(record1.get("underlying_md_yield_type")).thenReturn("MATURITY");
        when(record1.get("underlying_md_entry_size")).thenReturn("1000");
        when(record1.isSet("underlying_md_yield")).thenReturn(true);
        when(record1.isSet("underlying_md_yield_type")).thenReturn(true);
        records.add(record1);
        
        groupedRecords.put("key", records);
        
        XbondQuoteRecord result = transformer.transform(groupedRecords);
        
        assertNotNull(result);
        assertEquals("210210.IB", result.getExchProductId());
        assertEquals(0, result.getSettleSpeed());
        assertEquals("MATURITY", result.getBid0YieldType());
        assertEquals(Double.valueOf(100.0), result.getBid0Price());
        assertEquals(Long.valueOf(1000L), result.getBid0Volume());
    }

    @Test
    public void testSettleSpeedMapping() throws Exception {
        Map<String, List<CSVRecord>> groupedRecords1 = new HashMap<>();
        List<CSVRecord> records1 = new ArrayList<>();
        
        CSVRecord record1 = mock(CSVRecord.class);
        when(record1.get("business_date")).thenReturn("20250101");
        when(record1.get("underlying_security_id")).thenReturn("210210");
        when(record1.get("underlying_settlement_type")).thenReturn("1");
        when(record1.get("transact_time")).thenReturn("20250101-09:30:00.000");
        when(record1.get("recv_time")).thenReturn("20250101-09:30:00.000");
        when(record1.get("underlying_md_entry_type")).thenReturn("0");
        when(record1.get("underlying_md_price_level")).thenReturn("1");
        when(record1.get("underlying_md_entry_px")).thenReturn("100.0");
        when(record1.get("underlying_md_entry_size")).thenReturn("1000");
        when(record1.isSet(anyString())).thenReturn(false);
        records1.add(record1);
        
        groupedRecords1.put("key", records1);
        
        XbondQuoteRecord result1 = transformer.transform(groupedRecords1);
        assertEquals(0, result1.getSettleSpeed());

        Map<String, List<CSVRecord>> groupedRecords2 = new HashMap<>();
        List<CSVRecord> records2 = new ArrayList<>();
        
        CSVRecord record2 = mock(CSVRecord.class);
        when(record2.get("business_date")).thenReturn("20250101");
        when(record2.get("underlying_security_id")).thenReturn("210210");
        when(record2.get("underlying_settlement_type")).thenReturn("2");
        when(record2.get("transact_time")).thenReturn("20250101-09:30:00.000");
        when(record2.get("recv_time")).thenReturn("20250101-09:30:00.000");
        when(record2.get("underlying_md_entry_type")).thenReturn("0");
        when(record2.get("underlying_md_price_level")).thenReturn("1");
        when(record2.get("underlying_md_entry_px")).thenReturn("100.0");
        when(record2.get("underlying_md_entry_size")).thenReturn("1000");
        when(record2.isSet(anyString())).thenReturn(false);
        records2.add(record2);
        
        groupedRecords2.put("key", records2);
        
        XbondQuoteRecord result2 = transformer.transform(groupedRecords2);
        assertEquals(1, result2.getSettleSpeed());
    }
}
