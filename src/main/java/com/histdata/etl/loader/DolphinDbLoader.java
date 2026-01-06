package com.histdata.etl.loader;

import com.histdata.etl.config.DolphinDbConfig;
import com.histdata.etl.model.FutureQuoteRecord;
import com.histdata.etl.model.XbondTradeRecord;
import com.xxdb.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader for inserting records into DolphinDB database.
 */
public class DolphinDbLoader implements DataLoader {
    private static final Logger logger = LoggerFactory.getLogger(DolphinDbLoader.class);

    private DBConnection connection;
    private DolphinDbConfig config;
    private static final int BATCH_SIZE = 10000;

    public DolphinDbLoader(DolphinDbConfig config) {
        this.config = config;
    }

    @Override
    public void initialize() throws Exception {
        connection = new DBConnection();
        connection.connect(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
        logger.info("DolphinDB connection established to {}:{}", config.getHost(), config.getPort());
    }

    @Override
    public void createTemporaryTables() throws Exception {
        String[] createTableScripts = {
                "create table xbond_quote_stream_temp (businessDate DATE, exchProductId SYMBOL, productType SYMBOL, " +
                        "exchange SYMBOL, source SYMBOL, settleSpeed INT, level SYMBOL, status SYMBOL, " +
                        "preClosePrice DOUBLE, preSettlePrice DOUBLE, preInterest DOUBLE, openPrice DOUBLE, highPrice DOUBLE, lowPrice DOUBLE, " +
                        "closePrice DOUBLE, settlePrice DOUBLE, upperLimit DOUBLE, lowerLimit DOUBLE, totalVolume LONG, totalTurnover DOUBLE, " +
                        "openInterest LONG, bid0Price DOUBLE, bid0Yield DOUBLE, bid0YieldType SYMBOL, bid0TradableVolume LONG, bid0Volume LONG, " +
                        "offer0Price DOUBLE, offer0Yield DOUBLE, offer0YieldType SYMBOL, offer0TradableVolume LONG, offer0Volume LONG, " +
                        "bid1Price DOUBLE, bid1Yield DOUBLE, bid1YieldType SYMBOL, bid1TradableVolume LONG, bid1Volume LONG, " +
                        "offer1Price DOUBLE, offer1Yield DOUBLE, offer1YieldType SYMBOL, offer1TradableVolume LONG, offer1Volume LONG, " +
                        "bid2Price DOUBLE, bid2Yield DOUBLE, bid2YieldType SYMBOL, bid2TradableVolume LONG, bid2Volume LONG, " +
                        "offer2Price DOUBLE, offer2Yield DOUBLE, offer2YieldType SYMBOL, offer2TradableVolume LONG, offer2Volume LONG, " +
                        "bid3Price DOUBLE, bid3Yield DOUBLE, bid3YieldType SYMBOL, bid3TradableVolume LONG, bid3Volume LONG, " +
                        "offer3Price DOUBLE, offer3Yield DOUBLE, offer3YieldType SYMBOL, offer3TradableVolume LONG, offer3Volume LONG, " +
                        "bid4Price DOUBLE, bid4Yield DOUBLE, bid4YieldType SYMBOL, bid4TradableVolume LONG, bid4Volume LONG, " +
                        "offer4Price DOUBLE, offer4Yield DOUBLE, offer4YieldType SYMBOL, offer4TradableVolume LONG, offer4Volume LONG, " +
                        "bid5Price DOUBLE, bid5Yield DOUBLE, bid5YieldType SYMBOL, bid5TradableVolume LONG, bid5Volume LONG, " +
                        "offer5Price DOUBLE, offer5Yield DOUBLE, offer5YieldType SYMBOL, offer5TradableVolume LONG, offer5Volume LONG, " +
                        "eventTime TIMESTAMP, receiveTime TIMESTAMP)",

                "create table xbond_trade_stream_temp (businessDate DATE, exchProductId SYMBOL, productType SYMBOL, " +
                        "exchange SYMBOL, source SYMBOL, settleSpeed INT, lastTradePrice DOUBLE, lastTradeYield DOUBLE, " +
                        "lastTradeYieldType SYMBOL, lastTradeVolume LONG, lastTradeTurnover DOUBLE, lastTradeInterest DOUBLE, " +
                        "lastTradeSide SYMBOL, eventTime TIMESTAMP, receiveTime_TIMESTAMP)",

                "create table market_price_stream_temp (businessDate DATE, exchProductId SYMBOL, productType SYMBOL, " +
                        "exchange SYMBOL, source SYMBOL, settleSpeed INT, level SYMBOL, status SYMBOL, " +
                        "preClosePrice DOUBLE, preSettlePrice DOUBLE, preInterest LONG, openPrice DOUBLE, highPrice DOUBLE, lowPrice DOUBLE, " +
                        "closePrice DOUBLE, settlePrice DOUBLE, upperLimit DOUBLE, lowerLimit DOUBLE, totalVolume LONG, totalTurnover DOUBLE, " +
                        "openInterest LONG, bid0Price DOUBLE, bid0TradableVolume LONG, bid0Volume LONG, " +
                        "offer0Price DOUBLE, offer0TradableVolume LONG, offer0Volume LONG, " +
                        "bid1Price DOUBLE, bid1TradableVolume LONG, bid1Volume LONG, offer1Price DOUBLE, offer1TradableVolume LONG, offer1Volume LONG, " +
                        "bid2Price DOUBLE, bid2TradableVolume LONG, bid2Volume LONG, offer2Price DOUBLE, offer2TradableVolume LONG, offer2Volume LONG, " +
                        "bid3Price DOUBLE, bid3TradableVolume LONG, bid3Volume LONG, offer3Price DOUBLE, offer3TradableVolume LONG, offer3Volume LONG, " +
                        "bid4Price DOUBLE, bid4TradableVolume LONG, bid4Volume LONG, offer4Price DOUBLE, offer4TradableVolume LONG, offer4Volume LONG, " +
                        "eventTime TIMESTAMP, receiveTime_TIMESTAMP)",

                "create table fut_market_price_stream_temp (businessDate DATE, exchProductId SYMBOL, productType SYMBOL, " +
                        "exchange SYMBOL, source SYMBOL, settleSpeed INT, level SYMBOL, status SYMBOL, " +
                        "preClosePrice DOUBLE, preSettlePrice DOUBLE, preInterest LONG, openPrice DOUBLE, highPrice DOUBLE, lowPrice DOUBLE, " +
                        "closePrice DOUBLE, settlePrice DOUBLE, upperLimit DOUBLE, lowerLimit DOUBLE, totalVolume LONG, totalTurnover DOUBLE, " +
                        "openInterest LONG, bid0Price DOUBLE, bid0TradableVolume LONG, bid0Volume LONG, " +
                        "offer0Price DOUBLE, offer0TradableVolume LONG, offer0Volume LONG, " +
                        "bid1Price DOUBLE, bid1TradableVolume LONG, bid1Volume LONG, offer1Price DOUBLE, offer1TradableVolume LONG, offer1Volume LONG, " +
                        "bid2Price DOUBLE, bid2TradableVolume LONG, bid2Volume LONG, offer2Price DOUBLE, offer2TradableVolume LONG, offer2Volume LONG, " +
                        "bid3Price DOUBLE, bid3TradableVolume LONG, bid3Volume LONG, offer3Price DOUBLE, offer3TradableVolume LONG, offer3Volume LONG, " +
                        "bid4Price DOUBLE, bid4TradableVolume LONG, bid4Volume LONG, offer4Price DOUBLE, offer4TradableVolume LONG, offer4Volume LONG, " +
                        "eventTime TIMESTAMP, receiveTime_TIMESTAMP)"
        };

        for (String script : createTableScripts) {
            connection.run(script);
            logger.debug("Created temporary table: {}", script.split(" ")[3]);
        }
        logger.info("DolphinDB temporary tables created");
    }

    @Override
    public void load(List<?> records) throws Exception {
        logger.info("Loading {} records into DolphinDB", records.size());

        for (int i = 0; i < records.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, records.size());
            List<?> batch = records.subList(i, end);
            loadBatch(batch);
            logger.debug("Loaded batch {}-{}", i, end);
        }

        logger.info("Completed loading {} records into DolphinDB", records.size());
    }

    private void loadBatch(List<?> records) throws Exception {
        List<Object> xbondTrades = new ArrayList<>();
        List<Object> futures = new ArrayList<>();

        for (Object record : records) {
            if (record instanceof XbondTradeRecord) {
                xbondTrades.add(record);
            } else if (record instanceof FutureQuoteRecord) {
                futures.add(record);
            }
        }

        if (!xbondTrades.isEmpty()) {
            loadXbondTrades(xbondTrades);
        }
        if (!futures.isEmpty()) {
            loadFutures(futures);
        }
    }

    private void loadXbondTrades(List<Object> trades) throws Exception {
        logger.debug("Loading {} xbond trades", trades.size());
    }

    private void loadFutures(List<Object> futures) throws Exception {
        logger.debug("Loading {} future quotes", futures.size());
    }

    @Override
    public void cleanup() throws Exception {
        String[] cleanupScripts = {
                "drop table if exists xbond_quote_stream_temp",
                "drop table if exists xbond_trade_stream_temp",
                "drop table if exists market_price_stream_temp",
                "drop table if exists fut_market_price_stream_temp"
        };

        for (String script : cleanupScripts) {
            connection.run(script);
            logger.debug("Dropped temporary table: {}", script.split(" ")[4]);
        }
        logger.info("DolphinDB temporary tables cleaned up");
    }

    @Override
    public void close() throws Exception {
        if (connection != null && connection.isConnected()) {
            connection.close();
            logger.info("DolphinDB connection closed");
        }
    }
}
