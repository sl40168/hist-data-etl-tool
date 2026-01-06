package com.histdata.etl.model;

import org.junit.Test;
import org.junit.Before;

import java.sql.Date;

import static org.junit.Assert.*;

/**
 * Unit tests for XbondTradeRecord.
 */
public class XbondTradeRecordTest {
    private XbondTradeRecord record;

    @Before
    public void setUp() {
        record = new XbondTradeRecord(Date.valueOf("2025-01-01"), "210210.IB");
    }

    @Test
    public void testRequiredFields() {
        assertNotNull(record.getBusinessDate());
        assertEquals("210210.IB", record.getExchProductId());
        assertEquals("BOND", record.getProductType());
        assertEquals("CFETS", record.getExchange());
        assertEquals("XBOND", record.getSource());
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
    public void testLastTradeSideValidation() {
        record.setLastTradeSide("TKN");
        assertEquals("TKN", record.getLastTradeSide());

        record.setLastTradeSide("GVN");
        assertEquals("GVN", record.getLastTradeSide());

        record.setLastTradeSide("TRD");
        assertEquals("TRD", record.getLastTradeSide());

        record.setLastTradeSide("DONE");
        assertEquals("DONE", record.getLastTradeSide());

        try {
            record.setLastTradeSide("INVALID");
            fail("Should throw IllegalArgumentException for invalid lastTradeSide");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("lastTradeSide must be TKN, GVN, TRD, or DONE"));
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
    public void testLastTradeYieldTypeValidation() {
        record.setLastTradeYieldType("MATURITY");
        assertEquals("MATURITY", record.getLastTradeYieldType());

        record.setLastTradeYieldType("EXERCISE");
        assertEquals("EXERCISE", record.getLastTradeYieldType());

        try {
            record.setLastTradeYieldType("INVALID");
            fail("Should throw IllegalArgumentException for invalid yieldType");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("lastTradeYieldType must be MATURITY or EXERCISE"));
        }
    }

    @Test
    public void testLastTradePriceNonNegativeValidation() {
        record.setLastTradePrice(100.0);
        assertEquals(Double.valueOf(100.0), record.getLastTradePrice());

        record.setLastTradePrice(0.0);
        assertEquals(Double.valueOf(0.0), record.getLastTradePrice());

        try {
            record.setLastTradePrice(-1.0);
            fail("Should throw IllegalArgumentException for negative price");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be non-negative"));
        }
    }

    @Test
    public void testLastTradeVolumePositiveValidation() {
        record.setLastTradeVolume(100L);
        assertEquals(Long.valueOf(100L), record.getLastTradeVolume());

        try {
            record.setLastTradeVolume(0L);
            fail("Should throw IllegalArgumentException for zero volume");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be positive"));
        }

        try {
            record.setLastTradeVolume(-1L);
            fail("Should throw IllegalArgumentException for negative volume");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("must be positive"));
        }
    }
}
