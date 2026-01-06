package com.histdata.etl.transformer;

import com.histdata.etl.model.XbondTradeRecord;
import com.histdata.etl.util.DateUtils;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Transforms XbondCfetsDeal CSV records into XbondTradeRecord objects.
 * One source record produces one XbondTradeRecord.
 */
public class XbondTradeTransformer implements DataTransformer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(XbondTradeTransformer.class);

    @Override
    public XbondTradeRecord transform(Object rawRecord) throws Exception {
        if (!(rawRecord instanceof CSVRecord)) {
            throw new IllegalArgumentException("Expected CSVRecord, got: " + rawRecord.getClass());
        }

        CSVRecord record = (CSVRecord) rawRecord;

        String businessDateStr = record.get("business_date");
        String securityId = record.get("bond_key");
        Date businessDate = new Date(DateUtils.parseDate(businessDateStr, "yyyyMMdd").getTime());

        XbondTradeRecord result = new XbondTradeRecord(businessDate, securityId);

        double netPrice = Double.parseDouble(record.get("net_price"));
        result.setLastTradePrice(netPrice);

        String setDays = record.get("set_days");
        int settleSpeed = "T+0".equals(setDays) ? 0 : 1;
        result.setSettleSpeed(settleSpeed);

        String yieldStr = record.get("yield");
        if (yieldStr != null && !yieldStr.isEmpty()) {
            double yield = Double.parseDouble(yieldStr);
            result.setLastTradeYield(yield);
        }

        String yieldTypeStr = record.get("yield_type");
        if (yieldTypeStr != null && !yieldTypeStr.isEmpty()) {
            int yieldType = Integer.parseInt(yieldTypeStr);
            result.setLastTradeYieldType(yieldType == 0 ? "MATURITY" : "EXERCISE");
        }

        long dealSize = Long.parseLong(record.get("deal_size"));
        result.setLastTradeVolume(dealSize);

        String side = record.get("side");
        String lastTradeSide = mapSide(side);
        result.setLastTradeSide(lastTradeSide);

        String dealTimeStr = record.get("deal_time");
        Timestamp eventTime = new Timestamp(DateUtils.parseTimestamp(dealTimeStr, "yyyy-MM-dd HH:mm:ss.SSS").getTime());
        result.setEventTime(eventTime);

        String recvTimeStr = record.get("recv_time");
        Timestamp receiveTime;
        if (recvTimeStr != null && !recvTimeStr.isEmpty()) {
            receiveTime = new Timestamp(DateUtils.parseTimestamp(recvTimeStr, "yyyy-MM-dd HH:mm:ss.SSS").getTime());
        } else {
            receiveTime = eventTime;
            logger.warn("recv_time is null for trade {}, falling back to event_time", securityId);
        }
        result.setReceiveTime(receiveTime);

        return result;
    }

    private String mapSide(String side) {
        switch (side) {
            case "X":
                return "TKN";
            case "Y":
                return "GVN";
            case "Z":
                return "TRD";
            case "D":
                return "DONE";
            default:
                throw new IllegalArgumentException("Unknown side value: " + side);
        }
    }
}
