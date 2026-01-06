package com.histdata.etl.transformer;

import com.histdata.etl.model.FutureQuoteRecord;
import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for FutureQuoteTransformer.
 */
public class FutureQuoteTransformerTest {
    private FutureQuoteTransformer transformer;

    @Before
    public void setUp() {
        transformer = new FutureQuoteTransformer();
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
    public void testSuccessfulTransformation() throws Exception {
        Map<String, Object> record = new HashMap<>();
        record.put("business_date", "20250101");
        record.put("code", "T2503");
        record.put("pre_close", 100.0);
        record.put("pre_settle", 100.5);
        record.put("pre_interest", 10000L);
        record.put("open", 101.0);
        record.put("high", 102.0);
        record.put("low", 100.5);
        record.put("price", 101.5);
        record.put("settle_price", 0.0);
        record.put("upper_limit", 105.0);
        record.put("lower_limit", 95.0);
        record.put("total_volume", 100000L);
        record.put("total_turnover", 10000000.0);
        record.put("open_interest", 50000L);
        record.put("bid_prices", new double[]{100.0, 99.5, 99.0, 98.5, 98.0});
        record.put("bid_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("ask_prices", new double[]{101.0, 101.5, 102.0, 102.5, 103.0});
        record.put("ask_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("action_date", 20250101);
        record.put("action_time", 93000000);
        record.put("receive_time", "2025-01-01 09:30:00.000");
        
        FutureQuoteRecord result = transformer.transform(record);
        
        assertNotNull(result);
        assertEquals("T2503.CFFEX", result.getExchProductId());
        assertEquals("FUTURE", result.getProductType());
        assertEquals("CFFEX", result.getExchange());
        assertEquals("FUTURE", result.getSource());
        assertEquals(1, result.getSettleSpeed());
        assertEquals(Double.valueOf(100.0), result.getPreClosePrice());
        assertEquals(Double.valueOf(100.5), result.getPreSettlePrice());
        assertEquals(Long.valueOf(10000L), result.getPreInterest());
        assertEquals(Double.valueOf(101.0), result.getOpenPrice());
        assertEquals(Double.valueOf(102.0), result.getHighPrice());
        assertEquals(Double.valueOf(100.5), result.getLowPrice());
        assertEquals(Double.valueOf(101.5), result.getClosePrice());
        assertEquals(Double.valueOf(0.0), result.getSettlePrice());
        assertEquals(Double.valueOf(105.0), result.getUpperLimit());
        assertEquals(Double.valueOf(95.0), result.getLowerLimit());
        assertEquals(Long.valueOf(100000L), result.getTotalVolume());
        assertEquals(Double.valueOf(10000000.0), result.getTotalTurnover());
        assertEquals(Long.valueOf(50000L), result.getOpenInterest());
        assertEquals(Double.valueOf(100.0), result.getBid0Price());
        assertEquals(Long.valueOf(1000L), result.getBid0TradableVolume());
        assertEquals(Double.valueOf(101.0), result.getOffer0Price());
        assertEquals(Long.valueOf(1000L), result.getOffer0TradableVolume());
        assertNotNull(result.getEventTime());
        assertNotNull(result.getReceiveTime());
    }

    @Test
    public void testArrayUnwinding() throws Exception {
        Map<String, Object> record = new HashMap<>();
        record.put("business_date", "20250101");
        record.put("code", "T2503");
        record.put("pre_close", 100.0);
        record.put("pre_settle", 100.5);
        record.put("pre_interest", 10000L);
        record.put("open", 101.0);
        record.put("high", 102.0);
        record.put("low", 100.5);
        record.put("price", 101.5);
        record.put("settle_price", 0.0);
        record.put("upper_limit", 105.0);
        record.put("lower_limit", 95.0);
        record.put("total_volume", 100000L);
        record.put("total_turnover", 10000000.0);
        record.put("open_interest", 50000L);
        record.put("bid_prices", new double[]{100.0, 99.5, 99.0, 98.5, 98.0});
        record.put("bid_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("ask_prices", new double[]{101.0, 101.5, 102.0, 102.5, 103.0});
        record.put("ask_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("action_date", 20250101);
        record.put("action_time", 93000000);
        record.put("receive_time", "2025-01-01 09:30:00.000");
        
        FutureQuoteRecord result = transformer.transform(record);
        
        assertEquals(Double.valueOf(100.0), result.getBid0Price());
        assertEquals(Double.valueOf(99.5), result.getBid1Price());
        assertEquals(Double.valueOf(99.0), result.getBid2Price());
        assertEquals(Double.valueOf(98.5), result.getBid3Price());
        assertEquals(Double.valueOf(98.0), result.getBid4Price());
        
        assertEquals(Double.valueOf(101.0), result.getOffer0Price());
        assertEquals(Double.valueOf(101.5), result.getOffer1Price());
        assertEquals(Double.valueOf(102.0), result.getOffer2Price());
        assertEquals(Double.valueOf(102.5), result.getOffer3Price());
        assertEquals(Double.valueOf(103.0), result.getOffer4Price());
        
        assertEquals(Long.valueOf(1000L), result.getBid0TradableVolume());
        assertEquals(Long.valueOf(2000L), result.getBid1TradableVolume());
        assertEquals(Long.valueOf(3000L), result.getBid2TradableVolume());
        assertEquals(Long.valueOf(4000L), result.getBid3TradableVolume());
        assertEquals(Long.valueOf(5000L), result.getBid4TradableVolume());
        
        assertEquals(Long.valueOf(1000L), result.getOffer0TradableVolume());
        assertEquals(Long.valueOf(2000L), result.getOffer1TradableVolume());
        assertEquals(Long.valueOf(3000L), result.getOffer2TradableVolume());
        assertEquals(Long.valueOf(4000L), result.getOffer3TradableVolume());
        assertEquals(Long.valueOf(5000L), result.getOffer4TradableVolume());
    }

    @Test
    public void testReceiveTimeFallback() throws Exception {
        Map<String, Object> record = new HashMap<>();
        record.put("business_date", "20250101");
        record.put("code", "T2503");
        record.put("pre_close", 100.0);
        record.put("pre_settle", 100.5);
        record.put("pre_interest", 10000L);
        record.put("open", 101.0);
        record.put("high", 102.0);
        record.put("low", 100.5);
        record.put("price", 101.5);
        record.put("settle_price", 0.0);
        record.put("upper_limit", 105.0);
        record.put("lower_limit", 95.0);
        record.put("total_volume", 100000L);
        record.put("total_turnover", 10000000.0);
        record.put("open_interest", 50000L);
        record.put("bid_prices", new double[]{100.0, 99.5, 99.0, 98.5, 98.0});
        record.put("bid_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("ask_prices", new double[]{101.0, 101.5, 102.0, 102.5, 103.0});
        record.put("ask_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("action_date", 20250101);
        record.put("action_time", 93000000);
        record.put("receive_time", "");
        
        FutureQuoteRecord result = transformer.transform(record);
        
        assertNotNull(result.getReceiveTime());
        assertEquals(result.getEventTime(), result.getReceiveTime());
    }

    @Test
    public void testEventTimeParsing() throws Exception {
        Map<String, Object> record = new HashMap<>();
        record.put("business_date", "20250101");
        record.put("code", "T2503");
        record.put("pre_close", 100.0);
        record.put("pre_settle", 100.5);
        record.put("pre_interest", 10000L);
        record.put("open", 101.0);
        record.put("high", 102.0);
        record.put("low", 100.5);
        record.put("price", 101.5);
        record.put("settle_price", 0.0);
        record.put("upper_limit", 105.0);
        record.put("lower_limit", 95.0);
        record.put("total_volume", 100000L);
        record.put("total_turnover", 10000000.0);
        record.put("open_interest", 50000L);
        record.put("bid_prices", new double[]{100.0, 99.5, 99.0, 98.5, 98.0});
        record.put("bid_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("ask_prices", new double[]{101.0, 101.5, 102.0, 102.5, 103.0});
        record.put("ask_qty", new long[]{1000L, 2000L, 3000L, 4000L, 5000L});
        record.put("action_date", 20250101);
        record.put("action_time", 93000000);
        record.put("receive_time", "2025-01-01 09:30:00.000");
        
        FutureQuoteRecord result = transformer.transform(record);
        
        assertNotNull(result.getEventTime());
        assertEquals("2025-01-01 09:30:00.0", result.getEventTime().toString());
    }
}
