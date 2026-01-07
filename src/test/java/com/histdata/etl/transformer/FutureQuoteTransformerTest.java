package com.histdata.etl.transformer;

import com.histdata.etl.model.FutureQuoteRecord;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for FutureQuoteTransformer.
 */
public class FutureQuoteTransformerTest {

    private FutureQuoteTransformer transformer;

    @Before
    public void setUp() {
        transformer = new FutureQuoteTransformer();
    }

    @After
    public void tearDown() {
        transformer = null;
    }

    @Test
    public void testTransform_WithStandardInput_ProducesCorrectEventTime() throws Exception {
        // Given: action_date=20250107, action_time=93050090
        // Expected event time: "2025-01-07 09:30:50.090"
        Map<String, Object> record = new HashMap<>();
        record.put("action_date", 20250107);
        record.put("action_time", 93050090);
        record.put("code", "TF2412");
        record.put("pre_close", 123.45);
        record.put("pre_settle", 123.50);
        record.put("pre_interest", 1000L);
        record.put("open", 124.0);
        record.put("high", 125.0);
        record.put("low", 123.0);
        record.put("price", 124.50);
        record.put("settle_price", 124.30);
        record.put("upper_limit", 125.5);
        record.put("lower_limit", 122.5);
        record.put("total_volume", 10000L);
        record.put("total_turnover", 1245000.0);
        record.put("open_interest", 5000L);
        record.put("bid_prices", new double[]{124.0, 123.9, 123.8, 123.7, 123.6});
        record.put("bid_qty", new long[]{100, 200, 300, 400, 500});
        record.put("ask_prices", new double[]{124.1, 124.0, 123.9, 123.8, 123.7});
        record.put("ask_qty", new long[]{100, 200, 300, 400, 500});
        record.put("receive_time", "2025-01-07 09:30:51.123");

        LocalDate businessDate = LocalDate.of(2025, 1, 7);

        // When
        FutureQuoteRecord result = transformer.transform(record, businessDate);

        // Then
        assertNotNull("Result should not be null", result);
        assertEquals("TF2412", result.getExchProductId());
        
        // Verify event time is correctly formatted as "2025-01-07 09:30:50.090"
        Timestamp expectedEventTime = Timestamp.valueOf("2025-01-07 09:30:50.090");
        assertEquals("Event time should match expected format", 
                     expectedEventTime, result.getEventTime());
        
        // Verify receive time
        Timestamp expectedReceiveTime = Timestamp.valueOf("2025-01-07 09:30:51.123");
        assertEquals("Receive time should match", 
                     expectedReceiveTime, result.getReceiveTime());

        // Verify bid/offer fields
        assertEquals(124.0, result.getBid0Price(), 0.001);
        assertEquals(123.9, result.getBid1Price(), 0.001);
        assertEquals(124.1, result.getOffer0Price(), 0.001);
        assertEquals(124.0, result.getOffer1Price(), 0.001);
    }

    @Test
    public void testTransform_DifferentDate_ProducesCorrectEventTime() throws Exception {
        // Given: action_date=20241231, action_time=235959999
        // Expected event time: "2024-12-31 23:59:59.999"
        Map<String, Object> record = new HashMap<>();
        record.put("action_date", 20241231);
        record.put("action_time", 235959999);
        record.put("code", "TF2412");
        record.put("open", 100.0);
        record.put("high", 101.0);
        record.put("low", 99.0);
        record.put("price", 100.5);
        record.put("total_volume", 5000L);
        record.put("total_turnover", 502500.0);
        record.put("open_interest", 2500L);
        record.put("bid_prices", new double[]{100.0, 99.9, 99.8, 99.7, 99.6});
        record.put("bid_qty", new long[]{50, 100, 150, 200, 250});
        record.put("ask_prices", new double[]{100.1, 100.0, 99.9, 99.8, 99.7});
        record.put("ask_qty", new long[]{50, 100, 150, 200, 250});
        record.put("receive_time", "2024-12-31 23:59:59.999");

        LocalDate businessDate = LocalDate.of(2024, 12, 31);

        // When
        FutureQuoteRecord result = transformer.transform(record, businessDate);

        // Then
        assertNotNull("Result should not be null", result);
        
        // Verify event time is correctly formatted as "2024-12-31 23:59:59.999"
        Timestamp expectedEventTime = Timestamp.valueOf("2024-12-31 23:59:59.999");
        assertEquals("Event time for end of day should match", 
                     expectedEventTime, result.getEventTime());
    }

    @Test
    public void testTransform_Midnight_ProducesCorrectEventTime() throws Exception {
        // Given: action_date=20250101, action_time=000000000
        // Expected event time: "2025-01-01 00:00:00.000"
        Map<String, Object> record = new HashMap<>();
        record.put("action_date", 20250101);
        record.put("action_time", 0);
        record.put("code", "TF2412");
        record.put("open", 100.0);
        record.put("price", 100.5);
        record.put("total_volume", 1000L);
        record.put("total_turnover", 100500.0);
        record.put("open_interest", 500L);
        record.put("bid_prices", new double[]{100.0, 99.9, 99.8, 99.7, 99.6});
        record.put("bid_qty", new long[]{50, 100, 150, 200, 250});
        record.put("ask_prices", new double[]{100.1, 100.0, 99.9, 99.8, 99.7});
        record.put("ask_qty", new long[]{50, 100, 150, 200, 250});

        LocalDate businessDate = LocalDate.of(2025, 1, 1);

        // When
        FutureQuoteRecord result = transformer.transform(record, businessDate);

        // Then
        assertNotNull("Result should not be null", result);
        
        // Verify event time is correctly formatted as "2025-01-01 00:00:00.000"
        Timestamp expectedEventTime = Timestamp.valueOf("2025-01-01 00:00:00.000");
        assertEquals("Event time at midnight should match", 
                     expectedEventTime, result.getEventTime());
    }

    @Test
    public void testTransform_WithNullReceiveTime_FallsBackToEventTime() throws Exception {
        // Given: action_date=20250107, action_time=93050090, receive_time=null
        Map<String, Object> record = new HashMap<>();
        record.put("action_date", 20250107);
        record.put("action_time", 93050090);
        record.put("code", "TF2412");
        record.put("open", 100.0);
        record.put("price", 100.5);
        record.put("total_volume", 1000L);
        record.put("total_turnover", 100500.0);
        record.put("open_interest", 500L);
        record.put("bid_prices", new double[]{100.0, 99.9, 99.8, 99.7, 99.6});
        record.put("bid_qty", new long[]{50, 100, 150, 200, 250});
        record.put("ask_prices", new double[]{100.1, 100.0, 99.9, 99.8, 99.7});
        record.put("ask_qty", new long[]{50, 100, 150, 200, 250});
        record.put("receive_time", null);

        LocalDate businessDate = LocalDate.of(2025, 1, 7);

        // When
        FutureQuoteRecord result = transformer.transform(record, businessDate);

        // Then
        assertNotNull("Result should not be null", result);
        
        // When receive_time is null, should fall back to event_time
        Timestamp expectedEventTime = Timestamp.valueOf("2025-01-07 09:30:50.090");
        assertEquals("Receive time should fall back to event time", 
                     expectedEventTime, result.getReceiveTime());
    }

    @Test
    public void testTransform_WithEmptyReceiveTime_FallsBackToEventTime() throws Exception {
        // Given: action_date=20250107, action_time=93050090, receive_time=""
        Map<String, Object> record = new HashMap<>();
        record.put("action_date", 20250107);
        record.put("action_time", 93050090);
        record.put("code", "TF2412");
        record.put("open", 100.0);
        record.put("price", 100.5);
        record.put("total_volume", 1000L);
        record.put("total_turnover", 100500.0);
        record.put("open_interest", 500L);
        record.put("bid_prices", new double[]{100.0, 99.9, 99.8, 99.7, 99.6});
        record.put("bid_qty", new long[]{50, 100, 150, 200, 250});
        record.put("ask_prices", new double[]{100.1, 100.0, 99.9, 99.8, 99.7});
        record.put("ask_qty", new long[]{50, 100, 150, 200, 250});
        record.put("receive_time", "");

        LocalDate businessDate = LocalDate.of(2025, 1, 7);

        // When
        FutureQuoteRecord result = transformer.transform(record, businessDate);

        // Then
        assertNotNull("Result should not be null", result);
        
        // When receive_time is empty, should fall back to event_time
        Timestamp expectedEventTime = Timestamp.valueOf("2025-01-07 09:30:50.090");
        assertEquals("Receive time should fall back to event time", 
                     expectedEventTime, result.getReceiveTime());
    }
}
