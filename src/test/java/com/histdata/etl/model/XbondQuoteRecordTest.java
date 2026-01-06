package com.histdata.etl.model;

import org.junit.Test;
import org.junit.Before;

import java.sql.Date;
import java.sql.Timestamp;

import static org.junit.Assert.*;

/**
 * Unit tests for XbondQuoteRecord.
 */
public class XbondQuoteRecordTest {
    private XbondQuoteRecord record;

    @Before
    public void setUp() {
        record = new XbondQuoteRecord(Date.valueOf("2025-01-01"), "210210.IB");
    }

    @Test
    public void testRequiredFields() {
        assertNotNull(record.getBusinessDate());
        assertEquals("210210.IB", record.getExchProductId());
        assertEquals("BOND", record.getProductType());
        assertEquals("CFETS", record.getExchange());
        assertEquals("XBOND", record.getSource());
        assertEquals("L2", record.getLevel());
        assertEquals("Normal", record.getStatus());
    }

    @Test
    public void testSettleSpeedValidation() {
        record.setSettleSpeed(0);
        assertEquals(0, record.getSettleSpeed());

        record.setSettleSpeed(1);
        assertEquals(1, record.getSettleSpeed());

        try {
            record.setSettleSpeed(2);
            fail("Should throw IllegalArgumentException for settleSpeed=2");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("settleSpeed must be 0 or 1"));
        }
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
    public void testBid0YieldTypeValidation() {
        record.setBid0YieldType("MATURITY");
        assertEquals("MATURITY", record.getBid0YieldType());

        record.setBid0YieldType("EXERCISE");
        assertEquals("EXERCISE", record.getBid0YieldType());

        try {
            record.setBid0YieldType("INVALID");
            fail("Should throw IllegalArgumentException for invalid yieldType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("bid0YieldType must be MATURITY or EXERCISE"));
        }
    }

    @Test
    public void testPriceNonNegativeValidation() {
        record.setBid0Price(100.0);
        assertEquals(Double.valueOf(100.0), record.getBid0Price());

        record.setBid0Price(0.0);
        assertEquals(Double.valueOf(0.0), record.getBid0Price());

        try {
            record.setBid0Price(-1.0);
            fail("Should throw IllegalArgumentException for negative price");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be non-negative"));
        }
    }

    @Test
    public void testOffer0YieldTypeValidation() {
        record.setOffer0YieldType("MATURITY");
        assertEquals("MATURITY", record.getOffer0YieldType());

        record.setOffer0YieldType("EXERCISE");
        assertEquals("EXERCISE", record.getOffer0YieldType());

        try {
            record.setOffer0YieldType("INVALID");
            fail("Should throw IllegalArgumentException for invalid yieldType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("offer0YieldType must be MATURITY or EXERCISE"));
        }
    }

    @Test
    public void testAllBidOfferFields() {
        record.setBid0Price(100.0);
        record.setBid1Price(99.5);
        record.setBid2Price(99.0);
        record.setBid3Price(98.5);
        record.setBid4Price(98.0);
        record.setBid5Price(97.5);

        assertEquals(Double.valueOf(100.0), record.getBid0Price());
        assertEquals(Double.valueOf(99.5), record.getBid1Price());
        assertEquals(Double.valueOf(99.0), record.getBid2Price());
        assertEquals(Double.valueOf(98.5), record.getBid3Price());
        assertEquals(Double.valueOf(98.0), record.getBid4Price());
        assertEquals(Double.valueOf(97.5), record.getBid5Price());

        record.setOffer0Price(101.0);
        record.setOffer1Price(101.5);
        record.setOffer2Price(102.0);
        record.setOffer3Price(102.5);
        record.setOffer4Price(103.0);
        record.setOffer5Price(103.5);

        assertEquals(Double.valueOf(101.0), record.getOffer0Price());
        assertEquals(Double.valueOf(101.5), record.getOffer1Price());
        assertEquals(Double.valueOf(102.0), record.getOffer2Price());
        assertEquals(Double.valueOf(102.5), record.getOffer3Price());
        assertEquals(Double.valueOf(103.0), record.getOffer4Price());
        assertEquals(Double.valueOf(103.5), record.getOffer5Price());
    }
}
