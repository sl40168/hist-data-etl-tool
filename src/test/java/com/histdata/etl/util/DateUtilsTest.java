package com.histdata.etl.util;

import org.junit.Test;
import org.junit.Before;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Unit tests for DateUtils.
 */
public class DateUtilsTest {

    @Test
    public void testParseDateYYYYMMDD() throws ParseException {
        Date date = DateUtils.parseDateYYYYMMDD("20250101");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        assertEquals("20250101", sdf.format(date));
    }

    @Test(expected = ParseException.class)
    public void testParseDateYYYYMMDDInvalidFormat() throws ParseException {
        DateUtils.parseDateYYYYMMDD("2025-01-01");
    }

    @Test(expected = ParseException.class)
    public void testParseDateYYYYMMDDInvalidDate() throws ParseException {
        DateUtils.parseDateYYYYMMDD("20250230");
    }

    @Test
    public void testParseDate() throws ParseException {
        Date date = DateUtils.parseDate("2025-01-01");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("2025-01-01", sdf.format(date));
    }

    @Test(expected = ParseException.class)
    public void testParseDateInvalidFormat() throws ParseException {
        DateUtils.parseDate("20250101");
    }

    @Test
    public void testParseDateWithFormat() throws ParseException {
        Date date = DateUtils.parseDate("01/01/2025", "MM/dd/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals("01/01/2025", sdf.format(date));
    }

    @Test
    public void testParseTimestamp() throws ParseException {
        Date date = DateUtils.parseTimestamp("2025-01-01 12:30:45.123");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals("2025-01-01 12:30:45.123", sdf.format(date));
    }

    @Test(expected = ParseException.class)
    public void testParseTimestampInvalidFormat() throws ParseException {
        DateUtils.parseTimestamp("2025-01-01 12:30:45");
    }

    @Test
    public void testParseTimestampWithFormat() throws ParseException {
        Date date = DateUtils.parseTimestamp("2025-01-01 12:30:45", "yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("2025-01-01 12:30:45", sdf.format(date));
    }

    @Test
    public void testIsValidDateYYYYMMDD() {
        assertTrue(DateUtils.isValidDateYYYYMMDD("20250101"));
        assertFalse(DateUtils.isValidDateYYYYMMDD("2025-01-01"));
        assertFalse(DateUtils.isValidDateYYYYMMDD("20250230"));
        assertFalse(DateUtils.isValidDateYYYYMMDD("20251301"));
        assertFalse(DateUtils.isValidDateYYYYMMDD("20250132"));
        assertFalse(DateUtils.isValidDateYYYYMMDD("12345678"));
        assertFalse(DateUtils.isValidDateYYYYMMDD(""));
        assertFalse(DateUtils.isValidDateYYYYMMDD(null));
    }

    @Test
    public void testIsValidDateRange() {
        assertTrue(DateUtils.isValidDateRange("20250101", "20250101"));
        assertTrue(DateUtils.isValidDateRange("20250101", "20250131"));
        assertFalse(DateUtils.isValidDateRange("20250131", "20250101"));
        assertFalse(DateUtils.isValidDateRange("20250101", "invalid"));
        assertFalse(DateUtils.isValidDateRange("invalid", "20250101"));
        assertFalse(DateUtils.isValidDateRange("20250230", "20250101"));
        assertFalse(DateUtils.isValidDateRange("20250101", "20250230"));
    }
}