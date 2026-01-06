package com.histdata.etl.transformer;

import com.histdata.etl.model.FutureQuoteRecord;
import com.histdata.etl.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Transforms MySQL fut_tick records into FutureQuoteRecord objects.
 * One source record produces one FutureQuoteRecord.
 */
public class FutureQuoteTransformer implements DataTransformer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(FutureQuoteTransformer.class);

    @Override
    public FutureQuoteRecord transform(Object rawRecord) throws Exception {
        if (!(rawRecord instanceof Map)) {
            throw new IllegalArgumentException("Expected Map, got: " + rawRecord.getClass());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> record = (Map<String, Object>) rawRecord;

        String businessDateStr = (String) record.get("business_date");
        String code = (String) record.get("code");
        java.sql.Date businessDate = new java.sql.Date(DateUtils.parseDate(businessDateStr, "yyyyMMdd").getTime());

        String exchProductId = code + ".CFFEX";
        FutureQuoteRecord result = new FutureQuoteRecord(businessDate, exchProductId);

        result.setPreClosePrice(getDouble(record, "pre_close"));
        result.setPreSettlePrice(getDouble(record, "pre_settle"));
        result.setPreInterest(getLong(record, "pre_interest"));
        result.setOpenPrice(getDouble(record, "open"));
        result.setHighPrice(getDouble(record, "high"));
        result.setLowPrice(getDouble(record, "low"));
        result.setClosePrice(getDouble(record, "price"));
        result.setSettlePrice(getDouble(record, "settle_price"));
        result.setUpperLimit(getDouble(record, "upper_limit"));
        result.setLowerLimit(getDouble(record, "lower_limit"));
        result.setTotalVolume(getLong(record, "total_volume"));
        result.setTotalTurnover(getDouble(record, "total_turnover"));
        result.setOpenInterest(getLong(record, "open_interest"));

        Object bidPricesObj = record.get("bid_prices");
        Object bidQtyObj = record.get("bid_qty");
        Object askPricesObj = record.get("ask_prices");
        Object askQtyObj = record.get("ask_qty");

        if (bidPricesObj instanceof double[] && bidQtyObj instanceof long[]
                && askPricesObj instanceof double[] && askQtyObj instanceof long[]) {
            double[] bidPrices = (double[]) bidPricesObj;
            long[] bidQty = (long[]) bidQtyObj;
            double[] askPrices = (double[]) askPricesObj;
            long[] askQty = (long[]) askQtyObj;

            for (int i = 0; i < 5; i++) {
                if (i < bidPrices.length) {
                    setBidField(result, i, bidPrices[i], bidQty[i]);
                }
                if (i < askPrices.length) {
                    setOfferField(result, i, askPrices[i], askQty[i]);
                }
            }
        }

        int actionDate = getInteger(record, "action_date");
        int actionTime = getInteger(record, "action_time");
        String actionDateStr = String.format("%08d", actionDate);
        String actionTimeStr = String.format("%09d", actionTime);
        String eventTimeStr = actionDateStr + "-" + 
                actionTimeStr.substring(0, 2) + ":" + 
                actionTimeStr.substring(2, 4) + ":" + 
                actionTimeStr.substring(4, 6) + "." + 
                actionTimeStr.substring(6);
        java.sql.Timestamp eventTime = new java.sql.Timestamp(DateUtils.parseTimestamp(eventTimeStr, "yyyyMMdd-HH:mm:ss.SSS").getTime());
        result.setEventTime(eventTime);

        String receiveTimeStr = (String) record.get("receive_time");
        Timestamp receiveTime;
        if (receiveTimeStr != null && !receiveTimeStr.isEmpty()) {
            receiveTime = new java.sql.Timestamp(DateUtils.parseTimestamp(receiveTimeStr, "yyyy-MM-dd HH:mm:ss.SSS").getTime());
        } else {
            receiveTime = eventTime;
            logger.warn("receive_time is null for future {}, falling back to event_time", code);
        }
        result.setReceiveTime(receiveTime);

        return result;
    }

    private void setBidField(FutureQuoteRecord record, int level, double price, long volume) {
        switch (level) {
            case 0:
                record.setBid0Price(price);
                record.setBid0TradableVolume(volume);
                break;
            case 1:
                record.setBid1Price(price);
                record.setBid1TradableVolume(volume);
                break;
            case 2:
                record.setBid2Price(price);
                record.setBid2TradableVolume(volume);
                break;
            case 3:
                record.setBid3Price(price);
                record.setBid3TradableVolume(volume);
                break;
            case 4:
                record.setBid4Price(price);
                record.setBid4TradableVolume(volume);
                break;
        }
    }

    private void setOfferField(FutureQuoteRecord record, int level, double price, long volume) {
        switch (level) {
            case 0:
                record.setOffer0Price(price);
                record.setOffer0TradableVolume(volume);
                break;
            case 1:
                record.setOffer1Price(price);
                record.setOffer1TradableVolume(volume);
                break;
            case 2:
                record.setOffer2Price(price);
                record.setOffer2TradableVolume(volume);
                break;
            case 3:
                record.setOffer3Price(price);
                record.setOffer3TradableVolume(volume);
                break;
            case 4:
                record.setOffer4Price(price);
                record.setOffer4TradableVolume(volume);
                break;
        }
    }

    private Double getDouble(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private Long getLong(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    private Integer getInteger(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }
}
