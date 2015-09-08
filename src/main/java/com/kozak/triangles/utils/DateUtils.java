package com.kozak.triangles.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /** The maximum date possible. */
    public static Date MAX_DATE = new Date(Long.MAX_VALUE);

    /**
     * <p>
     * Checks if two dates are on the same day ignoring time.
     * </p>
     * 
     * @param date1
     *            the first date, not altered, not null
     * @param date2
     *            the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException
     *             if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * <p>
     * Checks if two calendars represent the same day ignoring time.
     * </p>
     * 
     * @param cal1
     *            the first calendar, not altered, not null
     * @param cal2
     *            the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException
     *             if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
                .get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * <p>
     * Checks if a date is today.
     * </p>
     * 
     * @param date
     *            the date, not altered, not null.
     * @return true if the date is today.
     * @throws IllegalArgumentException
     *             if the date is <code>null</code>
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }

    /**
     * <p>
     * Checks if a calendar date is today.
     * </p>
     * 
     * @param cal
     *            the calendar, not altered, not null
     * @return true if cal date is today
     * @throws IllegalArgumentException
     *             if the calendar is <code>null</code>
     */
    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    /**
     * <p>
     * Checks if the first date is before the second date ignoring time.
     * </p>
     * 
     * @param date1
     *            the first date, not altered, not null
     * @param date2
     *            the second date, not altered, not null
     * @return true if the first date day is before the second date day.
     * @throws IllegalArgumentException
     *             if the date is <code>null</code>
     */
    public static boolean isBeforeDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isBeforeDay(cal1, cal2);
    }

    /**
     * <p>
     * Checks if the first calendar date is before the second calendar date ignoring time.
     * </p>
     * 
     * @param cal1
     *            the first calendar, not altered, not null.
     * @param cal2
     *            the second calendar, not altered, not null.
     * @return true if cal1 date is before cal2 date ignoring time.
     * @throws IllegalArgumentException
     *             if either of the calendars are <code>null</code>
     */
    public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA))
            return true;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA))
            return false;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
            return true;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
            return false;
        return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * <p>
     * Checks if the first date is after the second date ignoring time.
     * </p>
     * 
     * @param date1
     *            the first date, not altered, not null
     * @param date2
     *            the second date, not altered, not null
     * @return true if the first date day is after the second date day.
     * @throws IllegalArgumentException
     *             if the date is <code>null</code>
     */
    public static boolean isAfterDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isAfterDay(cal1, cal2);
    }

    /**
     * <p>
     * Checks if the first calendar date is after the second calendar date ignoring time.
     * </p>
     * 
     * @param cal1
     *            the first calendar, not altered, not null.
     * @param cal2
     *            the second calendar, not altered, not null.
     * @return true if cal1 date is after cal2 date ignoring time.
     * @throws IllegalArgumentException
     *             if either of the calendars are <code>null</code>
     */
    public static boolean isAfterDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA))
            return false;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA))
            return true;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
            return false;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
            return true;
        return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * <p>
     * Checks if a date is after today and within a number of days in the future.
     * </p>
     * 
     * @param date
     *            the date to check, not altered, not null.
     * @param days
     *            the number of days.
     * @return true if the date day is after today and within days in the future .
     * @throws IllegalArgumentException
     *             if the date is <code>null</code>
     */
    public static boolean isWithinDaysFuture(Date date, int days) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return isWithinDaysFuture(cal, days);
    }

    /**
     * <p>
     * Checks if a calendar date is after today and within a number of days in the future.
     * </p>
     * 
     * @param cal
     *            the calendar, not altered, not null
     * @param days
     *            the number of days.
     * @return true if the calendar date day is after today and within days in the future .
     * @throws IllegalArgumentException
     *             if the calendar is <code>null</code>
     */
    public static boolean isWithinDaysFuture(Calendar cal, int days) {
        if (cal == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar today = Calendar.getInstance();
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_YEAR, days);
        return (isAfterDay(cal, today) && !isAfterDay(cal, future));
    }

    /** Returns the given date with the time set to the start of the day. */
    public static Date getStart(Date date) {
        return clearTime(date);
    }

    /** Returns the given date with the time values cleared. */
    public static Date clearTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Determines whether or not a date has any time values (hour, minute, seconds or millisecondsReturns the given date
     * with the time values cleared.
     */

    /**
     * Determines whether or not a date has any time values.
     * 
     * @param date
     *            The date.
     * @return true iff the date is not null and any of the date's hour, minute, seconds or millisecond values are
     *         greater than zero.
     */
    public static boolean hasTime(Date date) {
        if (date == null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (c.get(Calendar.HOUR_OF_DAY) > 0) {
            return true;
        }
        if (c.get(Calendar.MINUTE) > 0) {
            return true;
        }
        if (c.get(Calendar.SECOND) > 0) {
            return true;
        }
        if (c.get(Calendar.MILLISECOND) > 0) {
            return true;
        }
        return false;
    }

    /** Returns the given date with time set to the end of the day */
    public static Date getEnd(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * Returns the maximum of two dates. A null date is treated as being less than any non-null date.
     */
    public static Date max(Date d1, Date d2) {
        if (d1 == null && d2 == null)
            return null;
        if (d1 == null)
            return d2;
        if (d2 == null)
            return d1;
        return (d1.after(d2)) ? d1 : d2;
    }

    /**
     * Returns the minimum of two dates. A null date is treated as being greater than any non-null date.
     */
    public static Date min(Date d1, Date d2) {
        if (d1 == null && d2 == null)
            return null;
        if (d1 == null)
            return d2;
        if (d2 == null)
            return d1;
        return (d1.before(d2)) ? d1 : d2;
    }

    // ///////////////////////////////////// my methods

    /**
     * 
     * @param d1
     *            - Date - first date
     * @param d2
     *            - Date - second date
     * @return the difference between two Dates
     */
    public static int daysBetween(Date d1, Date d2) {
        return (int) Math.round((double) (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 
     * @param c1
     *            - Calendar - first date
     * @param c2
     *            - Calendar - second date
     * @return the difference between two Calendar Instances
     */
    public static int daysBetween(Calendar c1, Calendar c2) {
        Date d1 = c1.getTime();
        Date d2 = c2.getTime();
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static Date stringToDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String result = df.format(date);

        return result;
    }

    /**
     * 
     * @param d1
     *            - Date - first date
     * @param d2
     *            - Date - second date
     * @return the difference between two Dates
     */
    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60));
    }

    /**
     * @param start
     *            - дата к которой добавить дни
     * @return start + COUNT of days
     */
    public static Date addDays(Date start, int count) {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.DATE, count);

        return c.getTime();
    }

    /**
     * 
     * @return now - 24 hours
     */
    public static Date getYesterday() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, -1);

        return tomorrow.getTime();
    }

    /**
     * 
     * @return now + COUNT of month
     */
    public static Date addMonths(int count) {
        Calendar res = Calendar.getInstance();
        res.add(Calendar.MONTH, count);

        return res.getTime();
    }

    /**
     * @param dateStr
     *            строковая дата
     * @return дата для запроса (начало дня)
     */
    public static Date getStartDateForQuery(String dateStr) throws ParseException {
        if (dateStr.isEmpty()) {
            return new Date(Long.MIN_VALUE);
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date parsed = df.parse(dateStr);
        return getStart(parsed);
    }

    /**
     * @param dateStr
     *            строковая дата
     * @return дата для запроса (конец дня)
     */
    public static Date getEndDateForQuery(String dateStr) throws ParseException {
        if (dateStr.isEmpty()) {
            dateStr = "9999-12-31";
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date parsed = df.parse(dateStr);
        return getEnd(parsed);
    }

    public static Date addHours(int hoursToConstruct) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, hoursToConstruct);

        return c.getTime();
    }
}
