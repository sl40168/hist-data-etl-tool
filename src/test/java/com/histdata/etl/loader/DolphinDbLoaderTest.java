package com.histdata.etl.loader;

import com.histdata.etl.config.DolphinDbConfig;
import com.histdata.etl.model.FutureQuoteRecord;
import com.histdata.etl.model.XbondQuoteRecord;
import com.histdata.etl.model.XbondTradeRecord;
import com.xxdb.DBConnection;
import com.xxdb.data.BasicTable;
import com.xxdb.data.Entity;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DolphinDbLoader.
 */
@RunWith(MockitoJUnitRunner.class)
public class DolphinDbLoaderTest {

    @Mock
    private DBConnection mockConnection;

    @Mock
    private DolphinDbConfig mockConfig;

    private DolphinDbLoader loader;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockConfig.getHost()).thenReturn("localhost");
        when(mockConfig.getPort()).thenReturn(8848);
        when(mockConfig.getUsername()).thenReturn("admin");
        when(mockConfig.getPassword()).thenReturn("123456");

        loader = new DolphinDbLoader(mockConfig);
        // Inject mock connection via reflection for testing
        try {
            java.lang.reflect.Field field = DolphinDbLoader.class.getDeclaredField("connection");
            field.setAccessible(true);
            field.set(loader, mockConnection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInitialize() throws Exception {
        // Mock successful connection
        doNothing().when(mockConnection).connect(anyString(), anyInt(), anyString(), anyString());
        
        loader.initialize();
        
        verify(mockConnection, times(1)).connect("localhost", 8848, "admin", "123456");
        // Connection field should be set (already mocked)
        assertNotNull("Connection should be initialized", getConnectionForTest());
    }

    @Test
    public void testCreateTemporaryTables() throws Exception {
        // Mock connection.run() to do nothing
        doNothing().when(mockConnection).run(anyString());
        
        loader.createTemporaryTables();
        
        // Should call connection.run() for each create table script
        verify(mockConnection, atLeast(4)).run(anyString());
    }

    @Test
    public void testLoadEmptyList() throws Exception {
        // Should handle empty list gracefully
        loader.load(new ArrayList<>());
        
        // No interactions with connection expected
        verifyNoInteractions(mockConnection);
    }

    @Test
    public void testLoadMixedRecords() throws Exception {
        // Create sample records
        List<Object> records = new ArrayList<>();
        records.add(createSampleXbondQuoteRecord());
        records.add(createSampleXbondTradeRecord());
        records.add(createSampleFutureQuoteRecord());
        
        // Mock connection.run() for tableInsert
        when(mockConnection.run(anyString(), any(List.class))).thenReturn(null);
        
        loader.load(records);
        
        // Should call tableInsert for each record type
        // Verify at least one tableInsert call (could be multiple batches)
        verify(mockConnection, atLeast(1)).run(anyString(), any(List.class));
    }

    @Test
    public void testCleanup() throws Exception {
        // Mock connection.run() for drop table scripts
        doNothing().when(mockConnection).run(anyString());
        
        loader.cleanup();
        
        // Should call connection.run() for each drop table script
        verify(mockConnection, atLeast(4)).run(anyString());
    }

    @Test
    public void testCloseWhenConnected() throws Exception {
        // Mock connection.isConnected() to return true
        when(mockConnection.isConnected()).thenReturn(true);
        doNothing().when(mockConnection).close();
        
        loader.close();
        
        verify(mockConnection, times(1)).close();
    }

    @Test
    public void testCloseWhenNotConnected() throws Exception {
        // Mock connection.isConnected() to return false
        when(mockConnection.isConnected()).thenReturn(false);
        
        loader.close();
        
        // Should not call close()
        verify(mockConnection, never()).close();
    }

    @Test
    public void testCloseWhenConnectionNull() throws Exception {
        // Set connection to null via reflection
        try {
            java.lang.reflect.Field field = DolphinDbLoader.class.getDeclaredField("connection");
            field.setAccessible(true);
            field.set(loader, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        loader.close();
        
        // Should not throw
    }

    // Helper method to expose connection for testing
    private DBConnection getConnectionForTest() throws Exception {
        java.lang.reflect.Field field = DolphinDbLoader.class.getDeclaredField("connection");
        field.setAccessible(true);
        return (DBConnection) field.get(loader);
    }

    private XbondQuoteRecord createSampleXbondQuoteRecord() {
        XbondQuoteRecord record = new XbondQuoteRecord(
            Date.valueOf("2025-01-01"), 
            "210210.IB"
        );
        record.setProductType("BOND");
        record.setExchange("CFETS");
        record.setSource("XBOND");
        record.setSettleSpeed(1);
        record.setLevel("L2");
        record.setStatus("Normal");
        record.setPreClosePrice(100.0);
        record.setPreSettlePrice(99.5);
        record.setPreInterest(0.5);
        record.setOpenPrice(100.5);
        record.setHighPrice(101.0);
        record.setLowPrice(99.0);
        record.setClosePrice(100.2);
        record.setSettlePrice(99.8);
        record.setUpperLimit(105.0);
        record.setLowerLimit(95.0);
        record.setTotalVolume(100000L);
        record.setTotalTurnover(10000000.0);
        record.setOpenInterest(50000L);
        record.setBid0Price(99.9);
        record.setBid0Yield(2.5);
        record.setBid0YieldType("MATURITY");
        record.setBid0TradableVolume(1000L);
        record.setBid0Volume(500L);
        record.setOffer0Price(100.1);
        record.setOffer0Yield(2.4);
        record.setOffer0YieldType("EXERCISE");
        record.setOffer0TradableVolume(800L);
        record.setOffer0Volume(400L);
        record.setEventTime(Timestamp.valueOf("2025-01-01 09:30:00.000"));
        record.setReceiveTime(Timestamp.valueOf("2025-01-01 09:30:00.500"));
        return record;
    }

    private XbondTradeRecord createSampleXbondTradeRecord() {
        XbondTradeRecord record = new XbondTradeRecord(
            Date.valueOf("2025-01-01"), 
            "210210.IB"
        );
        record.setProductType("BOND");
        record.setExchange("CFETS");
        record.setSource("XBOND");
        record.setSettleSpeed(1);
        record.setLastTradePrice(100.0);
        record.setLastTradeYield(2.5);
        record.setLastTradeYieldType("MATURITY");
        record.setLastTradeVolume(10000L);
        record.setLastTradeTurnover(1000000.0);
        record.setLastTradeInterest(50.0);
        record.setLastTradeSide("TKN");
        record.setEventTime(Timestamp.valueOf("2025-01-01 09:31:00.000"));
        record.setReceiveTime(Timestamp.valueOf("2025-01-01 09:31:00.300"));
        return record;
    }

    private FutureQuoteRecord createSampleFutureQuoteRecord() {
        FutureQuoteRecord record = new FutureQuoteRecord(
            Date.valueOf("2025-01-01"), 
            "TF2503.CFE"
        );
        record.setProductType("FUTURE");
        record.setExchange("CFE");
        record.setSource("FUTURE");
        record.setSettleSpeed(1);
        record.setLevel("L2");
        record.setStatus("Normal");
        record.setPreClosePrice(100.0);
        record.setPreSettlePrice(99.5);
        record.setPreInterest(1000L);
        record.setOpenPrice(100.5);
        record.setHighPrice(101.0);
        record.setLowPrice(99.0);
        record.setClosePrice(100.2);
        record.setSettlePrice(99.8);
        record.setUpperLimit(105.0);
        record.setLowerLimit(95.0);
        record.setTotalVolume(200000L);
        record.setTotalTurnover(20000000.0);
        record.setOpenInterest(100000L);
        record.setBid0Price(99.9);
        record.setBid0TradableVolume(2000L);
        record.setBid0Volume(1000L);
        record.setOffer0Price(100.1);
        record.setOffer0TradableVolume(1500L);
        record.setOffer0Volume(800L);
        record.setEventTime(Timestamp.valueOf("2025-01-01 09:30:00.000"));
        record.setReceiveTime(Timestamp.valueOf("2025-01-01 09:30:00.500"));
        return record;
    }
}