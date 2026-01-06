package com.histdata.etl.datasource;

import com.histdata.etl.config.MySqlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extractor for Bond Future L2 Quote records from MySQL fut_tick table.
 */
public class MySqlFutureExtractor implements DataSourceExtractor<Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(MySqlFutureExtractor.class);

    private Connection connection;
    private MySqlConfig config;

    public MySqlFutureExtractor(MySqlConfig config) {
        this.config = config;
    }

    @Override
    public void initialize() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                config.getHost(), config.getPort(), config.getDatabase());
        connection = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        logger.info("MySQL connection established to {}:{}", config.getHost(), config.getPort());
    }

    @Override
    public List<Map<String, Object>> extract(LocalDate businessDate) throws Exception {
        if (connection == null) {
            initialize();
        }

        String sql = "SELECT * FROM bond.fut_tick WHERE action_date = ? ORDER BY action_time";
        logger.info("Executing query: {}", sql);

        List<Map<String, Object>> records = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(businessDate.toString().replace("-", "")));
            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    record.put(columnName, value);
                }
                records.add(record);
            }
        }

        logger.info("Extracted {} records from fut_tick for date {}", records.size(), businessDate);
        return records;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
            logger.info("MySQL connection closed");
        }
    }
}
