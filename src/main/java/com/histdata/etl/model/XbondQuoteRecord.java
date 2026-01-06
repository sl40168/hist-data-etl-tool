package com.histdata.etl.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a transformed XBond Market Quote record ready for loading into DolphinDB.
 */
public class XbondQuoteRecord {
    private Date businessDate;
    private String exchProductId;
    private String productType;
    private String exchange;
    private String source;
    private int settleSpeed;
    private String level;
    private String status;
    private Double preClosePrice;
    private Double preSettlePrice;
    private Double preInterest;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;
    private Double settlePrice;
    private Double upperLimit;
    private Double lowerLimit;
    private Long totalVolume;
    private Double totalTurnover;
    private Long openInterest;
    private Double bid0Price;
    private Double bid0Yield;
    private String bid0YieldType;
    private Long bid0TradableVolume;
    private Long bid0Volume;
    private Double offer0Price;
    private Double offer0Yield;
    private String offer0YieldType;
    private Long offer0TradableVolume;
    private Long offer0Volume;
    private Double bid1Price;
    private Double bid1Yield;
    private String bid1YieldType;
    private Long bid1TradableVolume;
    private Long bid1Volume;
    private Double bid2Price;
    private Double bid2Yield;
    private String bid2YieldType;
    private Long bid2TradableVolume;
    private Long bid2Volume;
    private Double bid3Price;
    private Double bid3Yield;
    private String bid3YieldType;
    private Long bid3TradableVolume;
    private Long bid3Volume;
    private Double bid4Price;
    private Double bid4Yield;
    private String bid4YieldType;
    private Long bid4TradableVolume;
    private Long bid4Volume;
    private Double bid5Price;
    private Double bid5Yield;
    private String bid5YieldType;
    private Long bid5TradableVolume;
    private Long bid5Volume;
    private Double offer1Price;
    private Double offer1Yield;
    private String offer1YieldType;
    private Long offer1TradableVolume;
    private Long offer1Volume;
    private Double offer2Price;
    private Double offer2Yield;
    private String offer2YieldType;
    private Long offer2TradableVolume;
    private Long offer2Volume;
    private Double offer3Price;
    private Double offer3Yield;
    private String offer3YieldType;
    private Long offer3TradableVolume;
    private Long offer3Volume;
    private Double offer4Price;
    private Double offer4Yield;
    private String offer4YieldType;
    private Long offer4TradableVolume;
    private Long offer4Volume;
    private Double offer5Price;
    private Double offer5Yield;
    private String offer5YieldType;
    private Long offer5TradableVolume;
    private Long offer5Volume;
    private Timestamp eventTime;
    private Timestamp receiveTime;

    public XbondQuoteRecord(Date businessDate, String exchProductId) {
        this.businessDate = businessDate;
        this.exchProductId = exchProductId;
        this.productType = "BOND";
        this.exchange = "CFETS";
        this.source = "XBOND";
        this.level = "L2";
        this.status = "Normal";
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(Double preClosePrice) {
        if (preClosePrice != null && preClosePrice < 0) {
            throw new IllegalArgumentException("preClosePrice must be non-negative");
        }
        this.preClosePrice = preClosePrice;
    }

    public Double getPreSettlePrice() {
        return preSettlePrice;
    }

    public void setPreSettlePrice(Double preSettlePrice) {
        if (preSettlePrice != null && preSettlePrice < 0) {
            throw new IllegalArgumentException("preSettlePrice must be non-negative");
        }
        this.preSettlePrice = preSettlePrice;
    }

    public Double getPreInterest() {
        return preInterest;
    }

    public void setPreInterest(Double preInterest) {
        this.preInterest = preInterest;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        if (openPrice != null && openPrice < 0) {
            throw new IllegalArgumentException("openPrice must be non-negative");
        }
        this.openPrice = openPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        if (highPrice != null && highPrice < 0) {
            throw new IllegalArgumentException("highPrice must be non-negative");
        }
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        if (lowPrice != null && lowPrice < 0) {
            throw new IllegalArgumentException("lowPrice must be non-negative");
        }
        this.lowPrice = lowPrice;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        if (closePrice != null && closePrice < 0) {
            throw new IllegalArgumentException("closePrice must be non-negative");
        }
        this.closePrice = closePrice;
    }

    public Double getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(Double settlePrice) {
        if (settlePrice != null && settlePrice < 0) {
            throw new IllegalArgumentException("settlePrice must be non-negative");
        }
        this.settlePrice = settlePrice;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        if (upperLimit != null && upperLimit < 0) {
            throw new IllegalArgumentException("upperLimit must be non-negative");
        }
        this.upperLimit = upperLimit;
    }

    public Double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Double lowerLimit) {
        if (lowerLimit != null && lowerLimit < 0) {
            throw new IllegalArgumentException("lowerLimit must be non-negative");
        }
        this.lowerLimit = lowerLimit;
    }

    public Long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Long totalVolume) {
        if (totalVolume != null && totalVolume < 0) {
            throw new IllegalArgumentException("totalVolume must be non-negative");
        }
        this.totalVolume = totalVolume;
    }

    public Double getTotalTurnover() {
        return totalTurnover;
    }

    public void setTotalTurnover(Double totalTurnover) {
        if (totalTurnover != null && totalTurnover < 0) {
            throw new IllegalArgumentException("totalTurnover must be non-negative");
        }
        this.totalTurnover = totalTurnover;
    }

    public Long getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(Long openInterest) {
        if (openInterest != null && openInterest < 0) {
            throw new IllegalArgumentException("openInterest must be non-negative");
        }
        this.openInterest = openInterest;
    }

    public Double getBid0Price() {
        return bid0Price;
    }

    public void setBid0Price(Double bid0Price) {
        if (bid0Price != null && bid0Price < 0) {
            throw new IllegalArgumentException("bid0Price must be non-negative");
        }
        this.bid0Price = bid0Price;
    }

    public Double getBid0Yield() {
        return bid0Yield;
    }

    public void setBid0Yield(Double bid0Yield) {
        this.bid0Yield = bid0Yield;
    }

    public String getBid0YieldType() {
        return bid0YieldType;
    }

    public void setBid0YieldType(String bid0YieldType) {
        if (!"MATURITY".equals(bid0YieldType) && !"EXERCISE".equals(bid0YieldType)) {
            throw new IllegalArgumentException("bid0YieldType must be MATURITY or EXERCISE");
        }
        this.bid0YieldType = bid0YieldType;
    }

    public Long getBid0TradableVolume() {
        return bid0TradableVolume;
    }

    public void setBid0TradableVolume(Long bid0TradableVolume) {
        this.bid0TradableVolume = bid0TradableVolume;
    }

    public Long getBid0Volume() {
        return bid0Volume;
    }

    public void setBid0Volume(Long bid0Volume) {
        this.bid0Volume = bid0Volume;
    }

    public Double getOffer0Price() {
        return offer0Price;
    }

    public void setOffer0Price(Double offer0Price) {
        if (offer0Price != null && offer0Price < 0) {
            throw new IllegalArgumentException("offer0Price must be non-negative");
        }
        this.offer0Price = offer0Price;
    }

    public Double getOffer0Yield() {
        return offer0Yield;
    }

    public void setOffer0Yield(Double offer0Yield) {
        this.offer0Yield = offer0Yield;
    }

    public String getOffer0YieldType() {
        return offer0YieldType;
    }

    public void setOffer0YieldType(String offer0YieldType) {
        if (!"MATURITY".equals(offer0YieldType) && !"EXERCISE".equals(offer0YieldType)) {
            throw new IllegalArgumentException("offer0YieldType must be MATURITY or EXERCISE");
        }
        this.offer0YieldType = offer0YieldType;
    }

    public Long getOffer0TradableVolume() {
        return offer0TradableVolume;
    }

    public void setOffer0TradableVolume(Long offer0TradableVolume) {
        this.offer0TradableVolume = offer0TradableVolume;
    }

    public Long getOffer0Volume() {
        return offer0Volume;
    }

    public void setOffer0Volume(Long offer0Volume) {
        this.offer0Volume = offer0Volume;
    }

    public Double getBid1Price() {
        return bid1Price;
    }

    public void setBid1Price(Double bid1Price) {
        if (bid1Price != null && bid1Price < 0) {
            throw new IllegalArgumentException("bid1Price must be non-negative");
        }
        this.bid1Price = bid1Price;
    }

    public Double getBid1Yield() {
        return bid1Yield;
    }

    public void setBid1Yield(Double bid1Yield) {
        this.bid1Yield = bid1Yield;
    }

    public String getBid1YieldType() {
        return bid1YieldType;
    }

    public void setBid1YieldType(String bid1YieldType) {
        if (!"MATURITY".equals(bid1YieldType) && !"EXERCISE".equals(bid1YieldType)) {
            throw new IllegalArgumentException("bid1YieldType must be MATURITY or EXERCISE");
        }
        this.bid1YieldType = bid1YieldType;
    }

    public Long getBid1TradableVolume() {
        return bid1TradableVolume;
    }

    public void setBid1TradableVolume(Long bid1TradableVolume) {
        this.bid1TradableVolume = bid1TradableVolume;
    }

    public Long getBid1Volume() {
        return bid1Volume;
    }

    public void setBid1Volume(Long bid1Volume) {
        this.bid1Volume = bid1Volume;
    }

    public Double getBid2Price() {
        return bid2Price;
    }

    public void setBid2Price(Double bid2Price) {
        if (bid2Price != null && bid2Price < 0) {
            throw new IllegalArgumentException("bid2Price must be non-negative");
        }
        this.bid2Price = bid2Price;
    }

    public Double getBid2Yield() {
        return bid2Yield;
    }

    public void setBid2Yield(Double bid2Yield) {
        this.bid2Yield = bid2Yield;
    }

    public String getBid2YieldType() {
        return bid2YieldType;
    }

    public void setBid2YieldType(String bid2YieldType) {
        if (!"MATURITY".equals(bid2YieldType) && !"EXERCISE".equals(bid2YieldType)) {
            throw new IllegalArgumentException("bid2YieldType must be MATURITY or EXERCISE");
        }
        this.bid2YieldType = bid2YieldType;
    }

    public Long getBid2TradableVolume() {
        return bid2TradableVolume;
    }

    public void setBid2TradableVolume(Long bid2TradableVolume) {
        this.bid2TradableVolume = bid2TradableVolume;
    }

    public Long getBid2Volume() {
        return bid2Volume;
    }

    public void setBid2Volume(Long bid2Volume) {
        this.bid2Volume = bid2Volume;
    }

    public Double getBid3Price() {
        return bid3Price;
    }

    public void setBid3Price(Double bid3Price) {
        if (bid3Price != null && bid3Price < 0) {
            throw new IllegalArgumentException("bid3Price must be non-negative");
        }
        this.bid3Price = bid3Price;
    }

    public Double getBid3Yield() {
        return bid3Yield;
    }

    public void setBid3Yield(Double bid3Yield) {
        this.bid3Yield = bid3Yield;
    }

    public String getBid3YieldType() {
        return bid3YieldType;
    }

    public void setBid3YieldType(String bid3YieldType) {
        if (!"MATURITY".equals(bid3YieldType) && !"EXERCISE".equals(bid3YieldType)) {
            throw new IllegalArgumentException("bid3YieldType must be MATURITY or EXERCISE");
        }
        this.bid3YieldType = bid3YieldType;
    }

    public Long getBid3TradableVolume() {
        return bid3TradableVolume;
    }

    public void setBid3TradableVolume(Long bid3TradableVolume) {
        this.bid3TradableVolume = bid3TradableVolume;
    }

    public Long getBid3Volume() {
        return bid3Volume;
    }

    public void setBid3Volume(Long bid3Volume) {
        this.bid3Volume = bid3Volume;
    }

    public Double getBid4Price() {
        return bid4Price;
    }

    public void setBid4Price(Double bid4Price) {
        if (bid4Price != null && bid4Price < 0) {
            throw new IllegalArgumentException("bid4Price must be non-negative");
        }
        this.bid4Price = bid4Price;
    }

    public Double getBid4Yield() {
        return bid4Yield;
    }

    public void setBid4Yield(Double bid4Yield) {
        this.bid4Yield = bid4Yield;
    }

    public String getBid4YieldType() {
        return bid4YieldType;
    }

    public void setBid4YieldType(String bid4YieldType) {
        if (!"MATURITY".equals(bid4YieldType) && !"EXERCISE".equals(bid4YieldType)) {
            throw new IllegalArgumentException("bid4YieldType must be MATURITY or EXERCISE");
        }
        this.bid4YieldType = bid4YieldType;
    }

    public Long getBid4TradableVolume() {
        return bid4TradableVolume;
    }

    public void setBid4TradableVolume(Long bid4TradableVolume) {
        this.bid4TradableVolume = bid4TradableVolume;
    }

    public Long getBid4Volume() {
        return bid4Volume;
    }

    public void setBid4Volume(Long bid4Volume) {
        this.bid4Volume = bid4Volume;
    }

    public Double getBid5Price() {
        return bid5Price;
    }

    public void setBid5Price(Double bid5Price) {
        if (bid5Price != null && bid5Price < 0) {
            throw new IllegalArgumentException("bid5Price must be non-negative");
        }
        this.bid5Price = bid5Price;
    }

    public Double getBid5Yield() {
        return bid5Yield;
    }

    public void setBid5Yield(Double bid5Yield) {
        this.bid5Yield = bid5Yield;
    }

    public String getBid5YieldType() {
        return bid5YieldType;
    }

    public void setBid5YieldType(String bid5YieldType) {
        if (!"MATURITY".equals(bid5YieldType) && !"EXERCISE".equals(bid5YieldType)) {
            throw new IllegalArgumentException("bid5YieldType must be MATURITY or EXERCISE");
        }
        this.bid5YieldType = bid5YieldType;
    }

    public Long getBid5TradableVolume() {
        return bid5TradableVolume;
    }

    public void setBid5TradableVolume(Long bid5TradableVolume) {
        this.bid5TradableVolume = bid5TradableVolume;
    }

    public Long getBid5Volume() {
        return bid5Volume;
    }

    public void setBid5Volume(Long bid5Volume) {
        this.bid5Volume = bid5Volume;
    }

    public Double getOffer1Price() {
        return offer1Price;
    }

    public void setOffer1Price(Double offer1Price) {
        if (offer1Price != null && offer1Price < 0) {
            throw new IllegalArgumentException("offer1Price must be non-negative");
        }
        this.offer1Price = offer1Price;
    }

    public Double getOffer1Yield() {
        return offer1Yield;
    }

    public void setOffer1Yield(Double offer1Yield) {
        this.offer1Yield = offer1Yield;
    }

    public String getOffer1YieldType() {
        return offer1YieldType;
    }

    public void setOffer1YieldType(String offer1YieldType) {
        if (!"MATURITY".equals(offer1YieldType) && !"EXERCISE".equals(offer1YieldType)) {
            throw new IllegalArgumentException("offer1YieldType must be MATURITY or EXERCISE");
        }
        this.offer1YieldType = offer1YieldType;
    }

    public Long getOffer1TradableVolume() {
        return offer1TradableVolume;
    }

    public void setOffer1TradableVolume(Long offer1TradableVolume) {
        this.offer1TradableVolume = offer1TradableVolume;
    }

    public Long getOffer1Volume() {
        return offer1Volume;
    }

    public void setOffer1Volume(Long offer1Volume) {
        this.offer1Volume = offer1Volume;
    }

    public Double getOffer2Price() {
        return offer2Price;
    }

    public void setOffer2Price(Double offer2Price) {
        if (offer2Price != null && offer2Price < 0) {
            throw new IllegalArgumentException("offer2Price must be non-negative");
        }
        this.offer2Price = offer2Price;
    }

    public Double getOffer2Yield() {
        return offer2Yield;
    }

    public void setOffer2Yield(Double offer2Yield) {
        this.offer2Yield = offer2Yield;
    }

    public String getOffer2YieldType() {
        return offer2YieldType;
    }

    public void setOffer2YieldType(String offer2YieldType) {
        if (!"MATURITY".equals(offer2YieldType) && !"EXERCISE".equals(offer2YieldType)) {
            throw new IllegalArgumentException("offer2YieldType must be MATURITY or EXERCISE");
        }
        this.offer2YieldType = offer2YieldType;
    }

    public Long getOffer2TradableVolume() {
        return offer2TradableVolume;
    }

    public void setOffer2TradableVolume(Long offer2TradableVolume) {
        this.offer2TradableVolume = offer2TradableVolume;
    }

    public Long getOffer2Volume() {
        return offer2Volume;
    }

    public void setOffer2Volume(Long offer2Volume) {
        this.offer2Volume = offer2Volume;
    }

    public Double getOffer3Price() {
        return offer3Price;
    }

    public void setOffer3Price(Double offer3Price) {
        if (offer3Price != null && offer3Price < 0) {
            throw new IllegalArgumentException("offer3Price must be non-negative");
        }
        this.offer3Price = offer3Price;
    }

    public Double getOffer3Yield() {
        return offer3Yield;
    }

    public void setOffer3Yield(Double offer3Yield) {
        this.offer3Yield = offer3Yield;
    }

    public String getOffer3YieldType() {
        return offer3YieldType;
    }

    public void setOffer3YieldType(String offer3YieldType) {
        if (!"MATURITY".equals(offer3YieldType) && !"EXERCISE".equals(offer3YieldType)) {
            throw new IllegalArgumentException("offer3YieldType must be MATURITY or EXERCISE");
        }
        this.offer3YieldType = offer3YieldType;
    }

    public Long getOffer3TradableVolume() {
        return offer3TradableVolume;
    }

    public void setOffer3TradableVolume(Long offer3TradableVolume) {
        this.offer3TradableVolume = offer3TradableVolume;
    }

    public Long getOffer3Volume() {
        return offer3Volume;
    }

    public void setOffer3Volume(Long offer3Volume) {
        this.offer3Volume = offer3Volume;
    }

    public Double getOffer4Price() {
        return offer4Price;
    }

    public void setOffer4Price(Double offer4Price) {
        if (offer4Price != null && offer4Price < 0) {
            throw new IllegalArgumentException("offer4Price must be non-negative");
        }
        this.offer4Price = offer4Price;
    }

    public Double getOffer4Yield() {
        return offer4Yield;
    }

    public void setOffer4Yield(Double offer4Yield) {
        this.offer4Yield = offer4Yield;
    }

    public String getOffer4YieldType() {
        return offer4YieldType;
    }

    public void setOffer4YieldType(String offer4YieldType) {
        if (!"MATURITY".equals(offer4YieldType) && !"EXERCISE".equals(offer4YieldType)) {
            throw new IllegalArgumentException("offer4YieldType must be MATURITY or EXERCISE");
        }
        this.offer4YieldType = offer4YieldType;
    }

    public Long getOffer4TradableVolume() {
        return offer4TradableVolume;
    }

    public void setOffer4TradableVolume(Long offer4TradableVolume) {
        this.offer4TradableVolume = offer4TradableVolume;
    }

    public Long getOffer4Volume() {
        return offer4Volume;
    }

    public void setOffer4Volume(Long offer4Volume) {
        this.offer4Volume = offer4Volume;
    }

    public Double getOffer5Price() {
        return offer5Price;
    }

    public void setOffer5Price(Double offer5Price) {
        if (offer5Price != null && offer5Price < 0) {
            throw new IllegalArgumentException("offer5Price must be non-negative");
        }
        this.offer5Price = offer5Price;
    }

    public Double getOffer5Yield() {
        return offer5Yield;
    }

    public void setOffer5Yield(Double offer5Yield) {
        this.offer5Yield = offer5Yield;
    }

    public String getOffer5YieldType() {
        return offer5YieldType;
    }

    public void setOffer5YieldType(String offer5YieldType) {
        if (!"MATURITY".equals(offer5YieldType) && !"EXERCISE".equals(offer5YieldType)) {
            throw new IllegalArgumentException("offer5YieldType must be MATURITY or EXERCISE");
        }
        this.offer5YieldType = offer5YieldType;
    }

    public Long getOffer5TradableVolume() {
        return offer5TradableVolume;
    }

    public void setOffer5TradableVolume(Long offer5TradableVolume) {
        this.offer5TradableVolume = offer5TradableVolume;
    }

    public Long getOffer5Volume() {
        return offer5Volume;
    }

    public void setOffer5Volume(Long offer5Volume) {
        this.offer5Volume = offer5Volume;
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
