package com.histdata.etl.util;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Utility class for date parsing and formatting operations.
 * Supports multiple date formats used throughout the ETL tool.
 */
public class DateUtils {

    private static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    private static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Parses a date string in YYYYMMDD format (e.g., "20250101").
     *
     * @param dateStr Date string in YYYYMMDD format
     * @return Parsed Date object
     * @throws ParseException if the date string is invalid
     */
    public static Date parseDateYYYYMMDD(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD);
        return sdf.parse(dateStr);
    }

    /**
     * Parses a date string in yyyy-MM-dd format (e.g., "2025-01-01").
     *
     * @param dateStr Date string in yyyy-MM-dd format
     * @return Parsed Date object
     * @throws ParseException if the date string is invalid
     */
    public static Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
        return sdf.parse(dateStr);
    }

    /**
     * Parses a date string in yyyy-MM-dd format with specified format.
     *
     * @param dateStr Date string
     * @param format Date format pattern
     * @return Parsed Date object
     * @throws ParseException if the date string is invalid
     */
    public static Date parseDate(String dateStr, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(dateStr);
    }

    /**
     * Parses a timestamp string in yyyy-MM-dd HH:mm:ss.SSS format.
     * Used for parsing transact_time and recv_time from CSV files.
     *
     * @param timestampStr Timestamp string
     * @return Parsed Date object
     * @throws ParseException if the timestamp string is invalid
     */
    public static Date parseTimestamp(String timestampStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
        return sdf.parse(timestampStr);
    }

    /**
     * Parses a timestamp string with specified format.
     *
     * @param timestampStr Timestamp string
     * @param format Timestamp format pattern
     * @return Parsed Date object
     * @throws ParseException if the timestamp string is invalid
     */
    public static Date parseTimestamp(String timestampStr, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(timestampStr);
    }

    /**
     * Validates if a date string is in valid YYYYMMDD format.
     *
     * @param dateStr Date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateYYYYMMDD(String dateStr) {
        if (dateStr == null || dateStr.length() != 8) {
            return false;
        }
        try {
            Integer.parseInt(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD);
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Validates if start date is not after end date.
     *
     * @param startDate Start date in YYYYMMDD format
     * @param endDate End date in YYYYMMDD format
     * @return true if start <= end, false otherwise
     */
    public static boolean isValidDateRange(String startDate, String endDate) {
        if (!isValidDateYYYYMMDD(startDate) || !isValidDateYYYYMMDD(endDate)) {
            return false;
        }
        try {
            Date start = parseDateYYYYMMDD(startDate);
            Date end = parseDateYYYYMMDD(endDate);
            return !start.after(end);
        } catch (ParseException e) {
            return false;
        }
    }
}
