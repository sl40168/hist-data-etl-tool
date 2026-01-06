package com.histdata.etl.transformer;

import com.histdata.etl.model.XbondTradeRecord;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for XbondTradeTransformer.
 */
@RunWith(MockitoJUnitRunner.class)
public class XbondTradeTransformerTest {
    private XbondTradeTransformer transformer;

    @Before
    public void setUp() {
        transformer = new XbondTradeTransformer();
    }

    @Test
    public void testInvalidInput() {
        try {
            transformer.transform("invalid");
            fail("Should throw IllegalArgumentException for invalid input");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Expected CSVRecord"));
        } catch (Exception e) {
            fail("Should throw IllegalArgumentException");
        }
    }

    @Test
    public void testSuccessfulTransformation() throws Exception {
        CSVRecord record = mock(CSVRecord.class);
        when(record.get("business_date")).thenReturn("20250101");
        when(record.get("bond_key")).thenReturn("210210");
        when(record.get("net_price")).thenReturn("100.5");
        when(record.get("set_days")).thenReturn("T+0");
        when(record.get("yield")).thenReturn("3.5");
        when(record.get("yield_type")).thenReturn("0");
        when(record.get("deal_size")).thenReturn("1000000");
        when(record.get("side")).thenReturn("X");
        when(record.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(record.get("recv_time")).thenReturn("2025-01-01 09:30:00.000");
        
        XbondTradeRecord result = transformer.transform(record);
        
        assertNotNull(result);
        assertEquals("210210", result.getExchProductId());
        assertEquals("BOND", result.getProductType());
        assertEquals("CFETS", result.getExchange());
        assertEquals("XBOND", result.getSource());
        assertEquals(0, result.getSettleSpeed());
        assertEquals(Double.valueOf(100.5), result.getLastTradePrice());
        assertEquals(Double.valueOf(3.5), result.getLastTradeYield());
        assertEquals("MATURITY", result.getLastTradeYieldType());
        assertEquals(Long.valueOf(1000000L), result.getLastTradeVolume());
        assertEquals("TKN", result.getLastTradeSide());
        assertNotNull(result.getEventTime());
        assertNotNull(result.getReceiveTime());
    }

    @Test
    public void testSettleSpeedMapping() throws Exception {
        CSVRecord record1 = mock(CSVRecord.class);
        when(record1.get("business_date")).thenReturn("20250101");
        when(record1.get("bond_key")).thenReturn("210210");
        when(record1.get("net_price")).thenReturn("100.0");
        when(record1.get("set_days")).thenReturn("T+0");
        when(record1.get("yield")).thenReturn("3.5");
        when(record1.get("yield_type")).thenReturn("0");
        when(record1.get("deal_size")).thenReturn("1000");
        when(record1.get("side")).thenReturn("X");
        when(record1.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(record1.get("recv_time")).thenReturn("2025-01-01 09:30:00.000");
        
        XbondTradeRecord result1 = transformer.transform(record1);
        assertEquals(0, result1.getSettleSpeed());

        CSVRecord record2 = mock(CSVRecord.class);
        when(record2.get("business_date")).thenReturn("20250101");
        when(record2.get("bond_key")).thenReturn("210210");
        when(record2.get("net_price")).thenReturn("100.0");
        when(record2.get("set_days")).thenReturn("T+1");
        when(record2.get("yield")).thenReturn("3.5");
        when(record2.get("yield_type")).thenReturn("0");
        when(record2.get("deal_size")).thenReturn("1000");
        when(record2.get("side")).thenReturn("X");
        when(record2.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(record2.get("recv_time")).thenReturn("2025-01-01 09:30:00.000");
        
        XbondTradeRecord result2 = transformer.transform(record2);
        assertEquals(1, result2.getSettleSpeed());
    }

    @Test
    public void testSideMapping() throws Exception {
        CSVRecord recordX = mock(CSVRecord.class);
        when(recordX.get("business_date")).thenReturn("20250101");
        when(recordX.get("bond_key")).thenReturn("210210");
        when(recordX.get("net_price")).thenReturn("100.0");
        when(recordX.get("set_days")).thenReturn("T+0");
        when(recordX.get("yield")).thenReturn("3.5");
        when(recordX.get("yield_type")).thenReturn("0");
        when(recordX.get("deal_size")).thenReturn("1000");
        when(recordX.get("side")).thenReturn("X");
        when(recordX.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(recordX.get("recv_time")).thenReturn("2025-01-01 09:30:00.000");
        
        XbondTradeRecord resultX = transformer.transform(recordX);
        assertEquals("TKN", resultX.getLastTradeSide());

        CSVRecord recordY = mock(CSVRecord.class);
        when(recordY.get("business_date")).thenReturn("20250101");
        when(recordY.get("bond_key")).thenReturn("210210");
        when(recordY.get("net_price")).thenReturn("100.0");
        when(recordY.get("set_days")).thenReturn("T+0");
        when(recordY.get("yield")).thenReturn("3.5");
        when(recordY.get("yield_type")).thenReturn("0");
        when(recordY.get("deal_size")).thenReturn("1000");
        when(recordY.get("side")).thenReturn("Y");
        when(recordY.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(recordY.get("recv_time")).thenReturn("2025-01-01 09:30:00.000");
        
        XbondTradeRecord resultY = transformer.transform(recordY);
        assertEquals("GVN", resultY.getLastTradeSide());

        CSVRecord recordZ = mock(CSVRecord.class);
        when(recordZ.get("business_date")).thenReturn("20250101");
        when(recordZ.get("bond_key")).thenReturn("210210");
        when(recordZ.get("net_price")).thenReturn("100.0");
        when(recordZ.get("set_days")).thenReturn("T+0");
        when(recordZ.get("yield")).thenReturn("3.5");
        when(recordZ.get("yield_type")).thenReturn("0");
        when(recordZ.get("deal_size")).thenReturn("1000");
        when(recordZ.get("side")).thenReturn("Z");
        when(recordZ.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(recordZ.get("recv_time")).thenReturn("2025-01-01 09:30:00.000");
        
        XbondTradeRecord resultZ = transformer.transform(recordZ);
        assertEquals("TRD", resultZ.getLastTradeSide());

        CSVRecord recordD = mock(CSVRecord.class);
        when(recordD.get("business_date")).thenReturn("20250101");
        when(recordD.get("bond_key")).thenReturn("210210");
        when(recordD.get("net_price")).thenReturn("100.0");
        when(recordD.get("set_days")).thenReturn("T+0");
        when(recordD.get("yield")).thenReturn("3.5");
        when(recordD.get("yield_type")).thenReturn("0");
        when(recordD.get("deal_size")).thenReturn("1000");
        when(recordD.get("side")).thenReturn("D");
        when(recordD.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(recordD.get("recv_time")).thenReturn("2025-01-01 09:30:00.000");
        
        XbondTradeRecord resultD = transformer.transform(recordD);
        assertEquals("DONE", resultD.getLastTradeSide());
    }

    @Test
    public void testReceiveTimeFallback() throws Exception {
        CSVRecord record = mock(CSVRecord.class);
        when(record.get("business_date")).thenReturn("20250101");
        when(record.get("bond_key")).thenReturn("210210");
        when(record.get("net_price")).thenReturn("100.0");
        when(record.get("set_days")).thenReturn("T+0");
        when(record.get("yield")).thenReturn("3.5");
        when(record.get("yield_type")).thenReturn("0");
        when(record.get("deal_size")).thenReturn("1000");
        when(record.get("side")).thenReturn("X");
        when(record.get("deal_time")).thenReturn("2025-01-01 09:30:00.000");
        when(record.get("recv_time")).thenReturn("");
        
        XbondTradeRecord result = transformer.transform(record);
        
        assertNotNull(result.getReceiveTime());
        assertEquals(result.getEventTime(), result.getReceiveTime());
    }
}
