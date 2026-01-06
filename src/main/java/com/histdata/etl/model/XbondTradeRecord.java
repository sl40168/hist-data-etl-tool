package com.histdata.etl.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a transformed XBond Trade record ready for loading into DolphinDB.
 */
public class XbondTradeRecord {
    private Date businessDate;
    private String exchProductId;
    private String productType;
    private String exchange;
    private String source;
    private int settleSpeed;
    private Double lastTradePrice;
    private Double lastTradeYield;
    private String lastTradeYieldType;
    private Long lastTradeVolume;
    private Double lastTradeTurnover;
    private Double lastTradeInterest;
    private String lastTradeSide;
    private Timestamp eventTime;
    private Timestamp receiveTime;

    public XbondTradeRecord(Date businessDate, String exchProductId) {
        this.businessDate = businessDate;
        this.exchProductId = exchProductId;
        this.productType = "BOND";
        this.exchange = "CFETS";
        this.source = "XBOND";
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public String getExchProductId() {
        return exchProductId;
    }

    public void setExchProductId(String exchProductId) {
        this.exchProductId = exchProductId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(int settleSpeed) {
        if (settleSpeed != 0 && settleSpeed != 1) {
            throw new IllegalArgumentException("settleSpeed must be 0 or 1");
        }
        this.settleSpeed = settleSpeed;
    }

    public Double getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(Double lastTradePrice) {
        if (lastTradePrice != null && lastTradePrice < 0) {
            throw new IllegalArgumentException("lastTradePrice must be non-negative");
        }
        this.lastTradePrice = lastTradePrice;
    }

    public Double getLastTradeYield() {
        return lastTradeYield;
    }

    public void setLastTradeYield(Double lastTradeYield) {
        this.lastTradeYield = lastTradeYield;
    }

    public String getLastTradeYieldType() {
        return lastTradeYieldType;
    }

    public void setLastTradeYieldType(String lastTradeYieldType) {
        if (!"MATURITY".equals(lastTradeYieldType) && !"EXERCISE".equals(lastTradeYieldType)) {
            throw new IllegalArgumentException("lastTradeYieldType must be MATURITY or EXERCISE");
        }
        this.lastTradeYieldType = lastTradeYieldType;
    }

    public Long getLastTradeVolume() {
        return lastTradeVolume;
    }

    public void setLastTradeVolume(Long lastTradeVolume) {
        if (lastTradeVolume != null && lastTradeVolume <= 0) {
            throw new IllegalArgumentException("lastTradeVolume must be positive");
        }
        this.lastTradeVolume = lastTradeVolume;
    }

    public Double getLastTradeTurnover() {
        return lastTradeTurnover;
    }

    public void setLastTradeTurnover(Double lastTradeTurnover) {
        this.lastTradeTurnover = lastTradeTurnover;
    }

    public Double getLastTradeInterest() {
        return lastTradeInterest;
    }

    public void setLastTradeInterest(Double lastTradeInterest) {
        this.lastTradeInterest = lastTradeInterest;
    }

    public String getLastTradeSide() {
        return lastTradeSide;
    }

    public void setLastTradeSide(String lastTradeSide) {
        if (!"TKN".equals(lastTradeSide) && !"GVN".equals(lastTradeSide) 
                && !"TRD".equals(lastTradeSide) && !"DONE".equals(lastTradeSide)) {
            throw new IllegalArgumentException("lastTradeSide must be TKN, GVN, TRD, or DONE");
        }
        this.lastTradeSide = lastTradeSide;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public Timestamp getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Timestamp receiveTime) {
        if (receiveTime == null) {
            throw new IllegalArgumentException("receiveTime is required");
        }
        this.receiveTime = receiveTime;
    }
}
