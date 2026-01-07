package com.histdata.etl.model;

import org.junit.Test;
import org.junit.Before;

import java.sql.Date;

import static org.junit.Assert.*;

/**
 * Unit tests for FutureQuoteRecord.
 */
public class FutureQuoteRecordTest {
    private FutureQuoteRecord record;

    @Before
    public void setUp() {
        record = new FutureQuoteRecord(Date.valueOf("2025-01-01"), "T2503");
    }

    @Test
    public void testRequiredFields() {
        assertNotNull(record.getBusinessDate());
        assertEquals("T2503", record.getExchProductId());
        assertEquals("BOND_FUT", record.getProductType());
        assertEquals("CFFEX", record.getExchange());
        assertEquals("CFFEX", record.getSource());
        assertEquals(0, record.getSettleSpeed());
        assertEquals("L1", record.getLevel());
        assertEquals("Normal", record.getStatus());
    }

    @Test
    public void testSettleSpeedAlwaysZero() {
        assertEquals(0, record.getSettleSpeed());
    }

    @Test
    public void testReceiveTimeRequired() {
        try {
            record.setReceiveTime(null);
            fail("Should throw IllegalArgumentException for null receiveTime");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("receiveTime is required"));
        }
    }

    @Test
    public void testClosePriceNonNegativeValidation() {
        record.setClosePrice(100.0);
        assertEquals(Double.valueOf(100.0), record.getClosePrice());

        record.setClosePrice(0.0);
        assertEquals(Double.valueOf(0.0), record.getClosePrice());

        try {
            record.setClosePrice(-1.0);
            fail("Should throw IllegalArgumentException for negative price");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be non-negative"));
        }
    }

    @Test
    public void testTotalVolumeNonNegativeValidation() {
        record.setTotalVolume(1000L);
        assertEquals(Long.valueOf(1000L), record.getTotalVolume());

        record.setTotalVolume(0L);
        assertEquals(Long.valueOf(0L), record.getTotalVolume());

        try {
            record.setTotalVolume(-1L);
            fail("Should throw IllegalArgumentException for negative volume");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be non-negative"));
        }
    }

    @Test
    public void testOpenInterestNonNegativeValidation() {
        record.setOpenInterest(5000L);
        assertEquals(Long.valueOf(5000L), record.getOpenInterest());

        record.setOpenInterest(0L);
        assertEquals(Long.valueOf(0L), record.getOpenInterest());

        try {
            record.setOpenInterest(-1L);
            fail("Should throw IllegalArgumentException for negative open interest");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be non-negative"));
        }
    }

    @Test
    public void testBidTradableVolumeNonNegativeValidation() {
        record.setBid0TradableVolume(100L);
        assertEquals(Long.valueOf(100L), record.getBid0TradableVolume());

        record.setBid0TradableVolume(0L);
        assertEquals(Long.valueOf(0L), record.getBid0TradableVolume());

        try {
            record.setBid0TradableVolume(-1L);
            fail("Should throw IllegalArgumentException for negative volume");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be non-negative"));
        }
    }

    @Test
    public void testOfferTradableVolumeNonNegativeValidation() {
        record.setOffer0TradableVolume(100L);
        assertEquals(Long.valueOf(100L), record.getOffer0TradableVolume());

        record.setOffer0TradableVolume(0L);
        assertEquals(Long.valueOf(0L), record.getOffer0TradableVolume());

        try {
            record.setOffer0TradableVolume(-1L);
            fail("Should throw IllegalArgumentException for negative volume");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be non-negative"));
        }
    }

    @Test
    public void testAllBidOfferFields() {
        record.setBid0Price(100.0);
        record.setBid1Price(99.5);
        record.setBid2Price(99.0);
        record.setBid3Price(98.5);
        record.setBid4Price(98.0);

        assertEquals(Double.valueOf(100.0), record.getBid0Price());
        assertEquals(Double.valueOf(99.5), record.getBid1Price());
        assertEquals(Double.valueOf(99.0), record.getBid2Price());
        assertEquals(Double.valueOf(98.5), record.getBid3Price());
        assertEquals(Double.valueOf(98.0), record.getBid4Price());

        record.setOffer0Price(101.0);
        record.setOffer1Price(101.5);
        record.setOffer2Price(102.0);
        record.setOffer3Price(102.5);
        record.setOffer4Price(103.0);

        assertEquals(Double.valueOf(101.0), record.getOffer0Price());
        assertEquals(Double.valueOf(101.5), record.getOffer1Price());
        assertEquals(Double.valueOf(102.0), record.getOffer2Price());
        assertEquals(Double.valueOf(102.5), record.getOffer3Price());
        assertEquals(Double.valueOf(103.0), record.getOffer4Price());
    }
}
