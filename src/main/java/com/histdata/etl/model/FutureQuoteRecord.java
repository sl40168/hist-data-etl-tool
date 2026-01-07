package com.histdata.etl.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a transformed Bond Future L2 Quote record ready for loading into DolphinDB.
 */
public class FutureQuoteRecord {
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
    private Long preInterest;
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
    private Long bid0TradableVolume;
    private Long bid0Volume;
    private Double bid1Price;
    private Long bid1TradableVolume;
    private Long bid1Volume;
    private Double bid2Price;
    private Long bid2TradableVolume;
    private Long bid2Volume;
    private Double bid3Price;
    private Long bid3TradableVolume;
    private Long bid3Volume;
    private Double bid4Price;
    private Long bid4TradableVolume;
    private Long bid4Volume;
    private Double offer0Price;
    private Long offer0TradableVolume;
    private Long offer0Volume;
    private Double offer1Price;
    private Long offer1TradableVolume;
    private Long offer1Volume;
    private Double offer2Price;
    private Long offer2TradableVolume;
    private Long offer2Volume;
    private Double offer3Price;
    private Long offer3TradableVolume;
    private Long offer3Volume;
    private Double offer4Price;
    private Long offer4TradableVolume;
    private Long offer4Volume;
    private Timestamp eventTime;
    private Timestamp receiveTime;

    public FutureQuoteRecord(Date businessDate, String exchProductId) {
        this.businessDate = businessDate;
        this.exchProductId = exchProductId;
        this.productType = "BOND_FUT";
        this.exchange = "CFFEX";
        this.source = "CFFEX";
        this.settleSpeed = 0;
        this.level = "L1";
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
        this.preClosePrice = preClosePrice;
    }

    public Double getPreSettlePrice() {
        return preSettlePrice;
    }

    public void setPreSettlePrice(Double preSettlePrice) {
        this.preSettlePrice = preSettlePrice;
    }

    public Long getPreInterest() {
        return preInterest;
    }

    public void setPreInterest(Long preInterest) {
        this.preInterest = preInterest;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
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
        this.settlePrice = settlePrice;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Double lowerLimit) {
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
        this.bid0Price = bid0Price;
    }

    public Long getBid0TradableVolume() {
        return bid0TradableVolume;
    }

    public void setBid0TradableVolume(Long bid0TradableVolume) {
        if (bid0TradableVolume != null && bid0TradableVolume < 0) {
            throw new IllegalArgumentException("bid0TradableVolume must be non-negative");
        }
        this.bid0TradableVolume = bid0TradableVolume;
    }

    public Long getBid0Volume() {
        return bid0Volume;
    }

    public void setBid0Volume(Long bid0Volume) {
        this.bid0Volume = bid0Volume;
    }

    public Double getBid1Price() {
        return bid1Price;
    }

    public void setBid1Price(Double bid1Price) {
        this.bid1Price = bid1Price;
    }

    public Long getBid1TradableVolume() {
        return bid1TradableVolume;
    }

    public void setBid1TradableVolume(Long bid1TradableVolume) {
        if (bid1TradableVolume != null && bid1TradableVolume < 0) {
            throw new IllegalArgumentException("bid1TradableVolume must be non-negative");
        }
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
        this.bid2Price = bid2Price;
    }

    public Long getBid2TradableVolume() {
        return bid2TradableVolume;
    }

    public void setBid2TradableVolume(Long bid2TradableVolume) {
        if (bid2TradableVolume != null && bid2TradableVolume < 0) {
            throw new IllegalArgumentException("bid2TradableVolume must be non-negative");
        }
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
        this.bid3Price = bid3Price;
    }

    public Long getBid3TradableVolume() {
        return bid3TradableVolume;
    }

    public void setBid3TradableVolume(Long bid3TradableVolume) {
        if (bid3TradableVolume != null && bid3TradableVolume < 0) {
            throw new IllegalArgumentException("bid3TradableVolume must be non-negative");
        }
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
        this.bid4Price = bid4Price;
    }

    public Long getBid4TradableVolume() {
        return bid4TradableVolume;
    }

    public void setBid4TradableVolume(Long bid4TradableVolume) {
        if (bid4TradableVolume != null && bid4TradableVolume < 0) {
            throw new IllegalArgumentException("bid4TradableVolume must be non-negative");
        }
        this.bid4TradableVolume = bid4TradableVolume;
    }

    public Long getBid4Volume() {
        return bid4Volume;
    }

    public void setBid4Volume(Long bid4Volume) {
        this.bid4Volume = bid4Volume;
    }

    public Double getOffer0Price() {
        return offer0Price;
    }

    public void setOffer0Price(Double offer0Price) {
        this.offer0Price = offer0Price;
    }

    public Long getOffer0TradableVolume() {
        return offer0TradableVolume;
    }

    public void setOffer0TradableVolume(Long offer0TradableVolume) {
        if (offer0TradableVolume != null && offer0TradableVolume < 0) {
            throw new IllegalArgumentException("offer0TradableVolume must be non-negative");
        }
        this.offer0TradableVolume = offer0TradableVolume;
    }

    public Long getOffer0Volume() {
        return offer0Volume;
    }

    public void setOffer0Volume(Long offer0Volume) {
        this.offer0Volume = offer0Volume;
    }

    public Double getOffer1Price() {
        return offer1Price;
    }

    public void setOffer1Price(Double offer1Price) {
        this.offer1Price = offer1Price;
    }

    public Long getOffer1TradableVolume() {
        return offer1TradableVolume;
    }

    public void setOffer1TradableVolume(Long offer1TradableVolume) {
        if (offer1TradableVolume != null && offer1TradableVolume < 0) {
            throw new IllegalArgumentException("offer1TradableVolume must be non-negative");
        }
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
        this.offer2Price = offer2Price;
    }

    public Long getOffer2TradableVolume() {
        return offer2TradableVolume;
    }

    public void setOffer2TradableVolume(Long offer2TradableVolume) {
        if (offer2TradableVolume != null && offer2TradableVolume < 0) {
            throw new IllegalArgumentException("offer2TradableVolume must be non-negative");
        }
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
        this.offer3Price = offer3Price;
    }

    public Long getOffer3TradableVolume() {
        return offer3TradableVolume;
    }

    public void setOffer3TradableVolume(Long offer3TradableVolume) {
        if (offer3TradableVolume != null && offer3TradableVolume < 0) {
            throw new IllegalArgumentException("offer3TradableVolume must be non-negative");
        }
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
        this.offer4Price = offer4Price;
    }

    public Long getOffer4TradableVolume() {
        return offer4TradableVolume;
    }

    public void setOffer4TradableVolume(Long offer4TradableVolume) {
        if (offer4TradableVolume != null && offer4TradableVolume < 0) {
            throw new IllegalArgumentException("offer4TradableVolume must be non-negative");
        }
        this.offer4TradableVolume = offer4TradableVolume;
    }

    public Long getOffer4Volume() {
        return offer4Volume;
    }

    public void setOffer4Volume(Long offer4Volume) {
        this.offer4Volume = offer4Volume;
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
