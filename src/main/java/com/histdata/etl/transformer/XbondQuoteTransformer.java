package com.histdata.etl.transformer;

import com.histdata.etl.model.XbondQuoteRecord;
import com.histdata.etl.util.DateUtils;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Transforms AllPriceDepth CSV records into XbondQuoteRecord objects.
 * Groups records by mq_offset and underlying_security_id, then creates one XbondQuoteRecord per group.
 */
public class XbondQuoteTransformer implements DataTransformer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(XbondQuoteTransformer.class);

    @Override
    public XbondQuoteRecord transform(Object rawRecord) throws Exception {
        if (!(rawRecord instanceof Map)) {
            throw new IllegalArgumentException("Expected Map of CSVRecords, got: " + rawRecord.getClass());
        }

        @SuppressWarnings("unchecked")
        Map<String, List<CSVRecord>> groupedRecords = (Map<String, List<CSVRecord>>) rawRecord;

        if (groupedRecords.isEmpty()) {
            return null;
        }

        XbondQuoteRecord result = null;

        for (Map.Entry<String, List<CSVRecord>> entry : groupedRecords.entrySet()) {
            List<CSVRecord> records = entry.getValue();
            CSVRecord firstRecord = records.get(0);

            String businessDateStr = firstRecord.get("business_date");
            String securityId = firstRecord.get("underlying_security_id") + ".IB";
            Date businessDate = new Date(DateUtils.parseDateYYYYMMDD(businessDateStr).getTime());

            result = new XbondQuoteRecord(businessDate, securityId);

            int settleSpeedRaw = Integer.parseInt(firstRecord.get("underlying_settlement_type"));
            result.setSettleSpeed(settleSpeedRaw == 1 ? 0 : 1);

            result.setEventTime(parseTimestamp(firstRecord.get("transact_time")));

            String recvTimeStr = firstRecord.get("recv_time");
            if (recvTimeStr == null || recvTimeStr.isEmpty()) {
                logger.warn("receive_time is null for security {}, skipping record", securityId);
                return null;
            }
            result.setReceiveTime(parseTimestamp(recvTimeStr));

            List<CSVRecord> sortedRecords = new ArrayList<>(records);
            sortedRecords.sort(Comparator
                    .comparing((CSVRecord r) -> Integer.parseInt(r.get("underlying_md_price_level")))
                    .thenComparing(r -> Integer.parseInt(r.get("underlying_md_entry_type"))));

            for (CSVRecord record : sortedRecords) {
                int entryType = Integer.parseInt(record.get("underlying_md_entry_type"));
                int level = Integer.parseInt(record.get("underlying_md_price_level"));
                double price = Double.parseDouble(record.get("underlying_md_entry_px"));
                Double yield = record.isSet("underlying_md_yield") && !record.get("underlying_md_yield").isEmpty()
                        ? Double.parseDouble(record.get("underlying_md_yield")) : null;
                String yieldType = record.isSet("underlying_md_yield_type") ? record.get("underlying_md_yield_type") : null;
                long volume = Long.parseLong(record.get("underlying_md_entry_size"));

                if (entryType == 0) {
                    setBidField(result, level, price, yield, yieldType, volume);
                } else if (entryType == 1) {
                    setOfferField(result, level, price, yield, yieldType, volume);
                }
            }
        }

        return result;
    }

    private Timestamp parseTimestamp(String timestampStr) throws ParseException {
        java.util.Date date = DateUtils.parseTimestamp(timestampStr, "yyyy-MM-dd HH:mm:ss.SSS");
        return new Timestamp(date.getTime());
    }

    private void setBidField(XbondQuoteRecord record, int level, double price, Double yield, String yieldType, long volume) {
        switch (level) {
            case 1:
                record.setBid0Price(price);
                record.setBid0Yield(yield);
                record.setBid0YieldType(yieldType);
                record.setBid0Volume(volume);
                record.setBid0TradableVolume(0L);
                break;
            case 2:
                record.setBid1Price(price);
                record.setBid1Yield(yield);
                record.setBid1YieldType(yieldType);
                record.setBid1Volume(volume);
                record.setBid1TradableVolume(volume);
                break;
            case 3:
                record.setBid2Price(price);
                record.setBid2Yield(yield);
                record.setBid2YieldType(yieldType);
                record.setBid2Volume(volume);
                record.setBid2TradableVolume(volume);
                break;
            case 4:
                record.setBid3Price(price);
                record.setBid3Yield(yield);
                record.setBid3YieldType(yieldType);
                record.setBid3Volume(volume);
                record.setBid3TradableVolume(volume);
                break;
            case 5:
                record.setBid4Price(price);
                record.setBid4Yield(yield);
                record.setBid4YieldType(yieldType);
                record.setBid4Volume(volume);
                record.setBid4TradableVolume(volume);
                break;
            case 6:
                record.setBid5Price(price);
                record.setBid5Yield(yield);
                record.setBid5YieldType(yieldType);
                record.setBid5Volume(volume);
                record.setBid5TradableVolume(volume);
                break;
        }
    }

    private void setOfferField(XbondQuoteRecord record, int level, double price, Double yield, String yieldType, long volume) {
        switch (level) {
            case 1:
                record.setOffer0Price(price);
                record.setOffer0Yield(yield);
                record.setOffer0YieldType(yieldType);
                record.setOffer0Volume(volume);
                record.setOffer0TradableVolume(0L);
                break;
            case 2:
                record.setOffer1Price(price);
                record.setOffer1Yield(yield);
                record.setOffer1YieldType(yieldType);
                record.setOffer1Volume(volume);
                record.setOffer1TradableVolume(volume);
                break;
            case 3:
                record.setOffer2Price(price);
                record.setOffer2Yield(yield);
                record.setOffer2YieldType(yieldType);
                record.setOffer2Volume(volume);
                record.setOffer2TradableVolume(volume);
                break;
            case 4:
                record.setOffer3Price(price);
                record.setOffer3Yield(yield);
                record.setOffer3YieldType(yieldType);
                record.setOffer3Volume(volume);
                record.setOffer3TradableVolume(volume);
                break;
            case 5:
                record.setOffer4Price(price);
                record.setOffer4Yield(yield);
                record.setOffer4YieldType(yieldType);
                record.setOffer4Volume(volume);
                record.setOffer4TradableVolume(volume);
                break;
            case 6:
                record.setOffer5Price(price);
                record.setOffer5Yield(yield);
                record.setOffer5YieldType(yieldType);
                record.setOffer5Volume(volume);
                record.setOffer5TradableVolume(volume);
                break;
        }
    }
}
