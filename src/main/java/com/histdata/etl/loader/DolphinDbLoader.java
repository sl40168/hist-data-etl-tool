package com.histdata.etl.loader;

import com.histdata.etl.config.DolphinDbConfig;
import com.histdata.etl.model.FutureQuoteRecord;
import com.histdata.etl.model.XbondQuoteRecord;
import com.histdata.etl.model.XbondTradeRecord;
import com.xxdb.DBConnection;
import com.xxdb.data.BasicDateVector;
import com.xxdb.data.BasicDoubleVector;
import com.xxdb.data.BasicIntVector;
import com.xxdb.data.BasicLongVector;
import com.xxdb.data.BasicStringVector;
import com.xxdb.data.BasicTable;
import com.xxdb.data.Entity;
import com.xxdb.data.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        List<Object> xbondQuotes = new ArrayList<>();
        List<Object> xbondTrades = new ArrayList<>();
        List<Object> futures = new ArrayList<>();

        for (Object record : records) {
            if (record instanceof XbondQuoteRecord) {
                xbondQuotes.add(record);
            } else if (record instanceof XbondTradeRecord) {
                xbondTrades.add(record);
            } else if (record instanceof FutureQuoteRecord) {
                futures.add(record);
            }
        }

        if (!xbondQuotes.isEmpty()) {
            loadXbondQuotes(xbondQuotes);
        }
        if (!xbondTrades.isEmpty()) {
            loadXbondTrades(xbondTrades);
        }
        if (!futures.isEmpty()) {
            loadFutures(futures);
        }
    }

    private void loadXbondQuotes(List<Object> quotes) throws Exception {
        logger.debug("Loading {} xbond quotes", quotes.size());

        int size = quotes.size();
        List<String> exchProductIds = new ArrayList<>(size);
        List<String> productTypes = new ArrayList<>(size);
        List<String> exchanges = new ArrayList<>(size);
        List<String> sources = new ArrayList<>(size);
        List<Integer> settleSpeeds = new ArrayList<>(size);
        List<String> levels = new ArrayList<>(size);
        List<String> statuses = new ArrayList<>(size);
        List<Double> preClosePrices = new ArrayList<>(size);
        List<Double> preSettlePrices = new ArrayList<>(size);
        List<Double> preInterests = new ArrayList<>(size);
        List<Double> openPrices = new ArrayList<>(size);
        List<Double> highPrices = new ArrayList<>(size);
        List<Double> lowPrices = new ArrayList<>(size);
        List<Double> closePrices = new ArrayList<>(size);
        List<Double> settlePrices = new ArrayList<>(size);
        List<Double> upperLimits = new ArrayList<>(size);
        List<Double> lowerLimits = new ArrayList<>(size);
        List<Long> totalVolumes = new ArrayList<>(size);
        List<Double> totalTurnovers = new ArrayList<>(size);
        List<Long> openInterests = new ArrayList<>(size);
        List<Double> bid0Prices = new ArrayList<>(size);
        List<Double> bid0Yields = new ArrayList<>(size);
        List<String> bid0YieldTypes = new ArrayList<>(size);
        List<Long> bid0TradableVolumes = new ArrayList<>(size);
        List<Long> bid0Volumes = new ArrayList<>(size);
        List<Double> offer0Prices = new ArrayList<>(size);
        List<Double> offer0Yields = new ArrayList<>(size);
        List<String> offer0YieldTypes = new ArrayList<>(size);
        List<Long> offer0TradableVolumes = new ArrayList<>(size);
        List<Long> offer0Volumes = new ArrayList<>(size);
        List<Double> bid1Prices = new ArrayList<>(size);
        List<Double> bid1Yields = new ArrayList<>(size);
        List<String> bid1YieldTypes = new ArrayList<>(size);
        List<Long> bid1TradableVolumes = new ArrayList<>(size);
        List<Long> bid1Volumes = new ArrayList<>(size);
        List<Double> offer1Prices = new ArrayList<>(size);
        List<Double> offer1Yields = new ArrayList<>(size);
        List<String> offer1YieldTypes = new ArrayList<>(size);
        List<Long> offer1TradableVolumes = new ArrayList<>(size);
        List<Long> offer1Volumes = new ArrayList<>(size);
        List<Double> bid2Prices = new ArrayList<>(size);
        List<Double> bid2Yields = new ArrayList<>(size);
        List<String> bid2YieldTypes = new ArrayList<>(size);
        List<Long> bid2TradableVolumes = new ArrayList<>(size);
        List<Long> bid2Volumes = new ArrayList<>(size);
        List<Double> offer2Prices = new ArrayList<>(size);
        List<Double> offer2Yields = new ArrayList<>(size);
        List<String> offer2YieldTypes = new ArrayList<>(size);
        List<Long> offer2TradableVolumes = new ArrayList<>(size);
        List<Long> offer2Volumes = new ArrayList<>(size);
        List<Double> bid3Prices = new ArrayList<>(size);
        List<Double> bid3Yields = new ArrayList<>(size);
        List<String> bid3YieldTypes = new ArrayList<>(size);
        List<Long> bid3TradableVolumes = new ArrayList<>(size);
        List<Long> bid3Volumes = new ArrayList<>(size);
        List<Double> offer3Prices = new ArrayList<>(size);
        List<Double> offer3Yields = new ArrayList<>(size);
        List<String> offer3YieldTypes = new ArrayList<>(size);
        List<Long> offer3TradableVolumes = new ArrayList<>(size);
        List<Long> offer3Volumes = new ArrayList<>(size);
        List<Double> bid4Prices = new ArrayList<>(size);
        List<Double> bid4Yields = new ArrayList<>(size);
        List<String> bid4YieldTypes = new ArrayList<>(size);
        List<Long> bid4TradableVolumes = new ArrayList<>(size);
        List<Long> bid4Volumes = new ArrayList<>(size);
        List<Double> offer4Prices = new ArrayList<>(size);
        List<Double> offer4Yields = new ArrayList<>(size);
        List<String> offer4YieldTypes = new ArrayList<>(size);
        List<Long> offer4TradableVolumes = new ArrayList<>(size);
        List<Long> offer4Volumes = new ArrayList<>(size);
        List<Double> bid5Prices = new ArrayList<>(size);
        List<Double> bid5Yields = new ArrayList<>(size);
        List<String> bid5YieldTypes = new ArrayList<>(size);
        List<Long> bid5TradableVolumes = new ArrayList<>(size);
        List<Long> bid5Volumes = new ArrayList<>(size);
        List<Double> offer5Prices = new ArrayList<>(size);
        List<Double> offer5Yields = new ArrayList<>(size);
        List<String> offer5YieldTypes = new ArrayList<>(size);
        List<Long> offer5TradableVolumes = new ArrayList<>(size);
        List<Long> offer5Volumes = new ArrayList<>(size);
        List<Long> eventTimes = new ArrayList<>(size);
        List<Long> receiveTimes = new ArrayList<>(size);

        for (Object obj : quotes) {
            XbondQuoteRecord record = (XbondQuoteRecord) obj;
            exchProductIds.add(record.getExchProductId());
            productTypes.add(record.getProductType());
            exchanges.add(record.getExchange());
            sources.add(record.getSource());
            settleSpeeds.add(record.getSettleSpeed());
            levels.add(record.getLevel());
            statuses.add(record.getStatus());
            preClosePrices.add(record.getPreClosePrice());
            preSettlePrices.add(record.getPreSettlePrice());
            preInterests.add(record.getPreInterest());
            openPrices.add(record.getOpenPrice());
            highPrices.add(record.getHighPrice());
            lowPrices.add(record.getLowPrice());
            closePrices.add(record.getClosePrice());
            settlePrices.add(record.getSettlePrice());
            upperLimits.add(record.getUpperLimit());
            lowerLimits.add(record.getLowerLimit());
            totalVolumes.add(record.getTotalVolume());
            totalTurnovers.add(record.getTotalTurnover());
            openInterests.add(record.getOpenInterest());
            bid0Prices.add(record.getBid0Price());
            bid0Yields.add(record.getBid0Yield());
            bid0YieldTypes.add(record.getBid0YieldType());
            bid0TradableVolumes.add(record.getBid0TradableVolume());
            bid0Volumes.add(record.getBid0Volume());
            offer0Prices.add(record.getOffer0Price());
            offer0Yields.add(record.getOffer0Yield());
            offer0YieldTypes.add(record.getOffer0YieldType());
            offer0TradableVolumes.add(record.getOffer0TradableVolume());
            offer0Volumes.add(record.getOffer0Volume());
            bid1Prices.add(record.getBid1Price());
            bid1Yields.add(record.getBid1Yield());
            bid1YieldTypes.add(record.getBid1YieldType());
            bid1TradableVolumes.add(record.getBid1TradableVolume());
            bid1Volumes.add(record.getBid1Volume());
            offer1Prices.add(record.getOffer1Price());
            offer1Yields.add(record.getOffer1Yield());
            offer1YieldTypes.add(record.getOffer1YieldType());
            offer1TradableVolumes.add(record.getOffer1TradableVolume());
            offer1Volumes.add(record.getOffer1Volume());
            bid2Prices.add(record.getBid2Price());
            bid2Yields.add(record.getBid2Yield());
            bid2YieldTypes.add(record.getBid2YieldType());
            bid2TradableVolumes.add(record.getBid2TradableVolume());
            bid2Volumes.add(record.getBid2Volume());
            offer2Prices.add(record.getOffer2Price());
            offer2Yields.add(record.getOffer2Yield());
            offer2YieldTypes.add(record.getOffer2YieldType());
            offer2TradableVolumes.add(record.getOffer2TradableVolume());
            offer2Volumes.add(record.getOffer2Volume());
            bid3Prices.add(record.getBid3Price());
            bid3Yields.add(record.getBid3Yield());
            bid3YieldTypes.add(record.getBid3YieldType());
            bid3TradableVolumes.add(record.getBid3TradableVolume());
            bid3Volumes.add(record.getBid3Volume());
            offer3Prices.add(record.getOffer3Price());
            offer3Yields.add(record.getOffer3Yield());
            offer3YieldTypes.add(record.getOffer3YieldType());
            offer3TradableVolumes.add(record.getOffer3TradableVolume());
            offer3Volumes.add(record.getOffer3Volume());
            bid4Prices.add(record.getBid4Price());
            bid4Yields.add(record.getBid4Yield());
            bid4YieldTypes.add(record.getBid4YieldType());
            bid4TradableVolumes.add(record.getBid4TradableVolume());
            bid4Volumes.add(record.getBid4Volume());
            offer4Prices.add(record.getOffer4Price());
            offer4Yields.add(record.getOffer4Yield());
            offer4YieldTypes.add(record.getOffer4YieldType());
            offer4TradableVolumes.add(record.getOffer4TradableVolume());
            offer4Volumes.add(record.getOffer4Volume());
            bid5Prices.add(record.getBid5Price());
            bid5Yields.add(record.getBid5Yield());
            bid5YieldTypes.add(record.getBid5YieldType());
            bid5TradableVolumes.add(record.getBid5TradableVolume());
            bid5Volumes.add(record.getBid5Volume());
            offer5Prices.add(record.getOffer5Price());
            offer5Yields.add(record.getOffer5Yield());
            offer5YieldTypes.add(record.getOffer5YieldType());
            offer5TradableVolumes.add(record.getOffer5TradableVolume());
            offer5Volumes.add(record.getOffer5Volume());
            eventTimes.add(record.getEventTime() != null ? record.getEventTime().getTime() : null);
            receiveTimes.add(record.getReceiveTime().getTime());
        }

        BasicTable table = new BasicTable(
            Arrays.asList("exchProductId", "productType", "exchange", "source", "settleSpeed", "level", "status",
                        "preClosePrice", "preSettlePrice", "preInterest", "openPrice", "highPrice", "lowPrice", "closePrice",
                        "settlePrice", "upperLimit", "lowerLimit", "totalVolume", "totalTurnover", "openInterest",
                        "bid0Price", "bid0Yield", "bid0YieldType", "bid0TradableVolume", "bid0Volume",
                        "offer0Price", "offer0Yield", "offer0YieldType", "offer0TradableVolume", "offer0Volume",
                        "bid1Price", "bid1Yield", "bid1YieldType", "bid1TradableVolume", "bid1Volume",
                        "offer1Price", "offer1Yield", "offer1YieldType", "offer1TradableVolume", "offer1Volume",
                        "bid2Price", "bid2Yield", "bid2YieldType", "bid2TradableVolume", "bid2Volume",
                        "offer2Price", "offer2Yield", "offer2YieldType", "offer2TradableVolume", "offer2Volume",
                        "bid3Price", "bid3Yield", "bid3YieldType", "bid3TradableVolume", "bid3Volume",
                        "offer3Price", "offer3Yield", "offer3YieldType", "offer3TradableVolume", "offer3Volume",
                        "bid4Price", "bid4Yield", "bid4YieldType", "bid4TradableVolume", "bid4Volume",
                        "offer4Price", "offer4Yield", "offer4YieldType", "offer4TradableVolume", "offer4Volume",
                        "bid5Price", "bid5Yield", "bid5YieldType", "bid5TradableVolume", "bid5Volume",
                        "offer5Price", "offer5Yield", "offer5YieldType", "offer5TradableVolume", "offer5Volume",
                        "eventTime", "receiveTime"),
            Arrays.asList(
                new BasicStringVector(exchProductIds),
                new BasicStringVector(productTypes),
                new BasicStringVector(exchanges),
                new BasicStringVector(sources),
                new BasicIntVector(settleSpeeds),
                new BasicStringVector(levels),
                new BasicStringVector(statuses),
                new BasicDoubleVector(preClosePrices),
                new BasicDoubleVector(preSettlePrices),
                new BasicDoubleVector(preInterests),
                new BasicDoubleVector(openPrices),
                new BasicDoubleVector(highPrices),
                new BasicDoubleVector(lowPrices),
                new BasicDoubleVector(closePrices),
                new BasicDoubleVector(settlePrices),
                new BasicDoubleVector(upperLimits),
                new BasicDoubleVector(lowerLimits),
                new BasicLongVector(totalVolumes),
                new BasicDoubleVector(totalTurnovers),
                new BasicLongVector(openInterests),
                new BasicDoubleVector(bid0Prices),
                new BasicDoubleVector(bid0Yields),
                new BasicStringVector(bid0YieldTypes),
                new BasicLongVector(bid0TradableVolumes),
                new BasicLongVector(bid0Volumes),
                new BasicDoubleVector(offer0Prices),
                new BasicDoubleVector(offer0Yields),
                new BasicStringVector(offer0YieldTypes),
                new BasicLongVector(offer0TradableVolumes),
                new BasicLongVector(offer0Volumes),
                new BasicDoubleVector(bid1Prices),
                new BasicDoubleVector(bid1Yields),
                new BasicStringVector(bid1YieldTypes),
                new BasicLongVector(bid1TradableVolumes),
                new BasicLongVector(bid1Volumes),
                new BasicDoubleVector(offer1Prices),
                new BasicDoubleVector(offer1Yields),
                new BasicStringVector(offer1YieldTypes),
                new BasicLongVector(offer1TradableVolumes),
                new BasicLongVector(offer1Volumes),
                new BasicDoubleVector(bid2Prices),
                new BasicDoubleVector(bid2Yields),
                new BasicStringVector(bid2YieldTypes),
                new BasicLongVector(bid2TradableVolumes),
                new BasicLongVector(bid2Volumes),
                new BasicDoubleVector(offer2Prices),
                new BasicDoubleVector(offer2Yields),
                new BasicStringVector(offer2YieldTypes),
                new BasicLongVector(offer2TradableVolumes),
                new BasicLongVector(offer2Volumes),
                new BasicDoubleVector(bid3Prices),
                new BasicDoubleVector(bid3Yields),
                new BasicStringVector(bid3YieldTypes),
                new BasicLongVector(bid3TradableVolumes),
                new BasicLongVector(bid3Volumes),
                new BasicDoubleVector(offer3Prices),
                new BasicDoubleVector(offer3Yields),
                new BasicStringVector(offer3YieldTypes),
                new BasicLongVector(offer3TradableVolumes),
                new BasicLongVector(offer3Volumes),
                new BasicDoubleVector(bid4Prices),
                new BasicDoubleVector(bid4Yields),
                new BasicStringVector(bid4YieldTypes),
                new BasicLongVector(bid4TradableVolumes),
                new BasicLongVector(bid4Volumes),
                new BasicDoubleVector(offer4Prices),
                new BasicDoubleVector(offer4Yields),
                new BasicStringVector(offer4YieldTypes),
                new BasicLongVector(offer4TradableVolumes),
                new BasicLongVector(offer4Volumes),
                new BasicDoubleVector(bid5Prices),
                new BasicDoubleVector(bid5Yields),
                new BasicStringVector(bid5YieldTypes),
                new BasicLongVector(bid5TradableVolumes),
                new BasicLongVector(bid5Volumes),
                new BasicDoubleVector(offer5Prices),
                new BasicDoubleVector(offer5Yields),
                new BasicStringVector(offer5YieldTypes),
                new BasicLongVector(offer5TradableVolumes),
                new BasicLongVector(offer5Volumes),
                new BasicLongVector(eventTimes),
                new BasicLongVector(receiveTimes)
            ));

        List<Entity> args = new ArrayList<>();
        args.add(table);
        connection.run("tableInsert{xbond_quote_stream_temp}", args);
        logger.info("Successfully loaded {} xbond quotes", size);
    }

    private void loadXbondTrades(List<Object> trades) throws Exception {
        logger.debug("Loading {} xbond trades", trades.size());

        int size = trades.size();
        List<String> exchProductIds = new ArrayList<>(size);
        List<String> productTypes = new ArrayList<>(size);
        List<String> exchanges = new ArrayList<>(size);
        List<String> sources = new ArrayList<>(size);
        List<Integer> settleSpeeds = new ArrayList<>(size);
        List<Double> lastTradePrices = new ArrayList<>(size);
        List<Double> lastTradeYields = new ArrayList<>(size);
        List<String> lastTradeYieldTypes = new ArrayList<>(size);
        List<Long> lastTradeVolumes = new ArrayList<>(size);
        List<Double> lastTradeTurnovers = new ArrayList<>(size);
        List<Double> lastTradeInterests = new ArrayList<>(size);
        List<String> lastTradeSides = new ArrayList<>(size);
        List<Long> eventTimes = new ArrayList<>(size);
        List<Long> receiveTimes = new ArrayList<>(size);

        for (Object obj : trades) {
            XbondTradeRecord record = (XbondTradeRecord) obj;
            exchProductIds.add(record.getExchProductId());
            productTypes.add(record.getProductType());
            exchanges.add(record.getExchange());
            sources.add(record.getSource());
            settleSpeeds.add(record.getSettleSpeed());
            lastTradePrices.add(record.getLastTradePrice());
            lastTradeYields.add(record.getLastTradeYield());
            lastTradeYieldTypes.add(record.getLastTradeYieldType());
            lastTradeVolumes.add(record.getLastTradeVolume());
            lastTradeTurnovers.add(record.getLastTradeTurnover());
            lastTradeInterests.add(record.getLastTradeInterest());
            lastTradeSides.add(record.getLastTradeSide());
            eventTimes.add(record.getEventTime() != null ? record.getEventTime().getTime() : null);
            receiveTimes.add(record.getReceiveTime().getTime());
        }

        BasicTable table = new BasicTable(
            Arrays.asList("exchProductId", "productType", "exchange", "source", "settleSpeed",
                        "lastTradePrice", "lastTradeYield", "lastTradeYieldType", "lastTradeVolume",
                        "lastTradeTurnover", "lastTradeInterest", "lastTradeSide",
                        "eventTime", "receiveTime"),
            Arrays.asList(
                new BasicStringVector(exchProductIds),
                new BasicStringVector(productTypes),
                new BasicStringVector(exchanges),
                new BasicStringVector(sources),
                new BasicIntVector(settleSpeeds),
                new BasicDoubleVector(lastTradePrices),
                new BasicDoubleVector(lastTradeYields),
                new BasicStringVector(lastTradeYieldTypes),
                new BasicLongVector(lastTradeVolumes),
                new BasicDoubleVector(lastTradeTurnovers),
                new BasicDoubleVector(lastTradeInterests),
                new BasicStringVector(lastTradeSides),
                new BasicLongVector(eventTimes),
                new BasicLongVector(receiveTimes)
            ));

        List<Entity> args = new ArrayList<>();
        args.add(table);
        connection.run("tableInsert{xbond_trade_stream_temp}", args);
        logger.info("Successfully loaded {} xbond trades", size);
    }

    private void loadFutures(List<Object> futures) throws Exception {
        logger.debug("Loading {} future quotes", futures.size());

        int size = futures.size();
        List<String> exchProductIds = new ArrayList<>(size);
        List<String> productTypes = new ArrayList<>(size);
        List<String> exchanges = new ArrayList<>(size);
        List<String> sources = new ArrayList<>(size);
        List<Integer> settleSpeeds = new ArrayList<>(size);
        List<String> levels = new ArrayList<>(size);
        List<String> statuses = new ArrayList<>(size);
        List<Double> preClosePrices = new ArrayList<>(size);
        List<Double> preSettlePrices = new ArrayList<>(size);
        List<Long> preInterests = new ArrayList<>(size);
        List<Double> openPrices = new ArrayList<>(size);
        List<Double> highPrices = new ArrayList<>(size);
        List<Double> lowPrices = new ArrayList<>(size);
        List<Double> closePrices = new ArrayList<>(size);
        List<Double> settlePrices = new ArrayList<>(size);
        List<Double> upperLimits = new ArrayList<>(size);
        List<Double> lowerLimits = new ArrayList<>(size);
        List<Long> totalVolumes = new ArrayList<>(size);
        List<Double> totalTurnovers = new ArrayList<>(size);
        List<Long> openInterests = new ArrayList<>(size);
        List<Double> bid0Prices = new ArrayList<>(size);
        List<Long> bid0TradableVolumes = new ArrayList<>(size);
        List<Long> bid0Volumes = new ArrayList<>(size);
        List<Double> bid1Prices = new ArrayList<>(size);
        List<Long> bid1TradableVolumes = new ArrayList<>(size);
        List<Long> bid1Volumes = new ArrayList<>(size);
        List<Double> bid2Prices = new ArrayList<>(size);
        List<Long> bid2TradableVolumes = new ArrayList<>(size);
        List<Long> bid2Volumes = new ArrayList<>(size);
        List<Double> bid3Prices = new ArrayList<>(size);
        List<Long> bid3TradableVolumes = new ArrayList<>(size);
        List<Long> bid3Volumes = new ArrayList<>(size);
        List<Double> bid4Prices = new ArrayList<>(size);
        List<Long> bid4TradableVolumes = new ArrayList<>(size);
        List<Long> bid4Volumes = new ArrayList<>(size);
        List<Double> offer0Prices = new ArrayList<>(size);
        List<Long> offer0TradableVolumes = new ArrayList<>(size);
        List<Long> offer0Volumes = new ArrayList<>(size);
        List<Double> offer1Prices = new ArrayList<>(size);
        List<Long> offer1TradableVolumes = new ArrayList<>(size);
        List<Long> offer1Volumes = new ArrayList<>(size);
        List<Double> offer2Prices = new ArrayList<>(size);
        List<Long> offer2TradableVolumes = new ArrayList<>(size);
        List<Long> offer2Volumes = new ArrayList<>(size);
        List<Double> offer3Prices = new ArrayList<>(size);
        List<Long> offer3TradableVolumes = new ArrayList<>(size);
        List<Long> offer3Volumes = new ArrayList<>(size);
        List<Double> offer4Prices = new ArrayList<>(size);
        List<Long> offer4TradableVolumes = new ArrayList<>(size);
        List<Long> offer4Volumes = new ArrayList<>(size);
        List<Long> eventTimes = new ArrayList<>(size);
        List<Long> receiveTimes = new ArrayList<>(size);

        for (Object obj : futures) {
            FutureQuoteRecord record = (FutureQuoteRecord) obj;
            exchProductIds.add(record.getExchProductId());
            productTypes.add(record.getProductType());
            exchanges.add(record.getExchange());
            sources.add(record.getSource());
            settleSpeeds.add(record.getSettleSpeed());
            levels.add(record.getLevel());
            statuses.add(record.getStatus());
            preClosePrices.add(record.getPreClosePrice());
            preSettlePrices.add(record.getPreSettlePrice());
            preInterests.add(record.getPreInterest());
            openPrices.add(record.getOpenPrice());
            highPrices.add(record.getHighPrice());
            lowPrices.add(record.getLowPrice());
            closePrices.add(record.getClosePrice());
            settlePrices.add(record.getSettlePrice());
            upperLimits.add(record.getUpperLimit());
            lowerLimits.add(record.getLowerLimit());
            totalVolumes.add(record.getTotalVolume());
            totalTurnovers.add(record.getTotalTurnover());
            openInterests.add(record.getOpenInterest());
            bid0Prices.add(record.getBid0Price());
            bid0TradableVolumes.add(record.getBid0TradableVolume());
            bid0Volumes.add(record.getBid0Volume());
            bid1Prices.add(record.getBid1Price());
            bid1TradableVolumes.add(record.getBid1TradableVolume());
            bid1Volumes.add(record.getBid1Volume());
            bid2Prices.add(record.getBid2Price());
            bid2TradableVolumes.add(record.getBid2TradableVolume());
            bid2Volumes.add(record.getBid2Volume());
            bid3Prices.add(record.getBid3Price());
            bid3TradableVolumes.add(record.getBid3TradableVolume());
            bid3Volumes.add(record.getBid3Volume());
            bid4Prices.add(record.getBid4Price());
            bid4TradableVolumes.add(record.getBid4TradableVolume());
            bid4Volumes.add(record.getBid4Volume());
            offer0Prices.add(record.getOffer0Price());
            offer0TradableVolumes.add(record.getOffer0TradableVolume());
            offer0Volumes.add(record.getOffer0Volume());
            offer1Prices.add(record.getOffer1Price());
            offer1TradableVolumes.add(record.getOffer1TradableVolume());
            offer1Volumes.add(record.getOffer1Volume());
            offer2Prices.add(record.getOffer2Price());
            offer2TradableVolumes.add(record.getOffer2TradableVolume());
            offer2Volumes.add(record.getOffer2Volume());
            offer3Prices.add(record.getOffer3Price());
            offer3TradableVolumes.add(record.getOffer3TradableVolume());
            offer3Volumes.add(record.getOffer3Volume());
            offer4Prices.add(record.getOffer4Price());
            offer4TradableVolumes.add(record.getOffer4TradableVolume());
            offer4Volumes.add(record.getOffer4Volume());
            eventTimes.add(record.getEventTime() != null ? record.getEventTime().getTime() : null);
            receiveTimes.add(record.getReceiveTime().getTime());
        }

        BasicTable table = new BasicTable(
            Arrays.asList("exchProductId", "productType", "exchange", "source", "settleSpeed", "level", "status",
                        "preClosePrice", "preSettlePrice", "preInterest", "openPrice", "highPrice", "lowPrice", "closePrice",
                        "settlePrice", "upperLimit", "lowerLimit", "totalVolume", "totalTurnover", "openInterest",
                        "bid0Price", "bid0TradableVolume", "bid0Volume",
                        "bid1Price", "bid1TradableVolume", "bid1Volume",
                        "bid2Price", "bid2TradableVolume", "bid2Volume",
                        "bid3Price", "bid3TradableVolume", "bid3Volume",
                        "bid4Price", "bid4TradableVolume", "bid4Volume",
                        "offer0Price", "offer0TradableVolume", "offer0Volume",
                        "offer1Price", "offer1TradableVolume", "offer1Volume",
                        "offer2Price", "offer2TradableVolume", "offer2Volume",
                        "offer3Price", "offer3TradableVolume", "offer3Volume",
                        "offer4Price", "offer4TradableVolume", "offer4Volume",
                        "eventTime", "receiveTime"),
            Arrays.asList(
                new BasicStringVector(exchProductIds),
                new BasicStringVector(productTypes),
                new BasicStringVector(exchanges),
                new BasicStringVector(sources),
                new BasicIntVector(settleSpeeds),
                new BasicStringVector(levels),
                new BasicStringVector(statuses),
                new BasicDoubleVector(preClosePrices),
                new BasicDoubleVector(preSettlePrices),
                new BasicLongVector(preInterests),
                new BasicDoubleVector(openPrices),
                new BasicDoubleVector(highPrices),
                new BasicDoubleVector(lowPrices),
                new BasicDoubleVector(closePrices),
                new BasicDoubleVector(settlePrices),
                new BasicDoubleVector(upperLimits),
                new BasicDoubleVector(lowerLimits),
                new BasicLongVector(totalVolumes),
                new BasicDoubleVector(totalTurnovers),
                new BasicLongVector(openInterests),
                new BasicDoubleVector(bid0Prices),
                new BasicLongVector(bid0TradableVolumes),
                new BasicLongVector(bid0Volumes),
                new BasicDoubleVector(bid1Prices),
                new BasicLongVector(bid1TradableVolumes),
                new BasicLongVector(bid1Volumes),
                new BasicDoubleVector(bid2Prices),
                new BasicLongVector(bid2TradableVolumes),
                new BasicLongVector(bid2Volumes),
                new BasicDoubleVector(bid3Prices),
                new BasicLongVector(bid3TradableVolumes),
                new BasicLongVector(bid3Volumes),
                new BasicDoubleVector(bid4Prices),
                new BasicLongVector(bid4TradableVolumes),
                new BasicLongVector(bid4Volumes),
                new BasicDoubleVector(offer0Prices),
                new BasicLongVector(offer0TradableVolumes),
                new BasicLongVector(offer0Volumes),
                new BasicDoubleVector(offer1Prices),
                new BasicLongVector(offer1TradableVolumes),
                new BasicLongVector(offer1Volumes),
                new BasicDoubleVector(offer2Prices),
                new BasicLongVector(offer2TradableVolumes),
                new BasicLongVector(offer2Volumes),
                new BasicDoubleVector(offer3Prices),
                new BasicLongVector(offer3TradableVolumes),
                new BasicLongVector(offer3Volumes),
                new BasicDoubleVector(offer4Prices),
                new BasicLongVector(offer4TradableVolumes),
                new BasicLongVector(offer4Volumes),
                new BasicLongVector(eventTimes),
                new BasicLongVector(receiveTimes)
            ));

        List<Entity> args = new ArrayList<>();
        args.add(table);
        connection.run("tableInsert{fut_market_price_stream_temp}", args);
        logger.info("Successfully loaded {} future quotes", size);
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
