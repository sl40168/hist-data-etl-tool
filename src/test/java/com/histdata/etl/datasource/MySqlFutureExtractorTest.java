package com.histdata.etl.datasource;

import com.histdata.etl.config.MySqlConfig;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MySqlFutureExtractor.
 */
@RunWith(MockitoJUnitRunner.class)
public class MySqlFutureExtractorTest {

    @Mock
    private MySqlConfig mockConfig;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private ResultSetMetaData mockMetaData;

    private MySqlFutureExtractor extractor;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockConfig.getHost()).thenReturn("localhost");
        when(mockConfig.getPort()).thenReturn(3306);
        when(mockConfig.getDatabase()).thenReturn("bond");
        when(mockConfig.getUsername()).thenReturn("testuser");
        when(mockConfig.getPassword()).thenReturn("testpass");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        extractor = new MySqlFutureExtractor(mockConfig);
        // Inject mock connection via reflection for testing
        try {
            java.lang.reflect.Field field = MySqlFutureExtractor.class.getDeclaredField("connection");
            field.setAccessible(true);
            field.set(extractor, mockConnection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInitialize() throws SQLException {
        // Mock DriverManager.getConnection
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            String expectedUrl = "jdbc:mysql://localhost:3306/bond?useSSL=false&serverTimezone=UTC";
            when(DriverManager.getConnection(eq(expectedUrl), eq("testuser"), eq("testpass")))
                    .thenReturn(mockConnection);

            MySqlFutureExtractor newExtractor = new MySqlFutureExtractor(mockConfig);
            newExtractor.initialize();

            assertNotNull("Connection should be established", mockConnection);
            verify(mockConfig, times(2)).getHost(); // called in constructor and initialize
        }
    }

    @Test
    public void testExtractSuccess() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 7);
        int dateInt = 20250107;

        when(mockResultSet.next()).thenReturn(true, true, false); // two records
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(3);
        when(mockMetaData.getColumnName(1)).thenReturn("action_date");
        when(mockMetaData.getColumnName(2)).thenReturn("action_time");
        when(mockMetaData.getColumnName(3)).thenReturn("price");
        when(mockResultSet.getObject(1)).thenReturn(dateInt);
        when(mockResultSet.getObject(2)).thenReturn("09:30:00");
        when(mockResultSet.getObject(3)).thenReturn(100.5);

        List<Map<String, Object>> records = extractor.extract(testDate);

        assertEquals("Should extract 2 records", 2, records.size());
        verify(mockPreparedStatement, times(1)).setInt(1, dateInt);
        verify(mockPreparedStatement, times(1)).executeQuery();

        Map<String, Object> firstRecord = records.get(0);
        assertEquals(dateInt, firstRecord.get("action_date"));
        assertEquals("09:30:00", firstRecord.get("action_time"));
        assertEquals(100.5, firstRecord.get("price"));
    }

    @Test
    public void testExtractNoRecords() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 7);
        int dateInt = 20250107;

        when(mockResultSet.next()).thenReturn(false); // no records

        List<Map<String, Object>> records = extractor.extract(testDate);

        assertTrue("Should return empty list", records.isEmpty());
        verify(mockPreparedStatement, times(1)).setInt(1, dateInt);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test(expected = SQLException.class)
    public void testExtractThrowsOnSqlError() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 7);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Connection failed"));

        extractor.extract(testDate);
    }

    @Test
    public void testClose() throws SQLException {
        when(mockConnection.isClosed()).thenReturn(false);
        extractor.close();
        verify(mockConnection, times(1)).close();
    }

    @Test
    public void testCloseWhenAlreadyClosed() throws SQLException {
        when(mockConnection.isClosed()).thenReturn(true);
        extractor.close();
        verify(mockConnection, times(0)).close();
    }

    @Test
    public void testCloseWhenConnectionNull() throws SQLException {
        // Set connection to null
        try {
            java.lang.reflect.Field field = MySqlFutureExtractor.class.getDeclaredField("connection");
            field.setAccessible(true);
            field.set(extractor, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        extractor.close();
        // Should not throw
    }
}