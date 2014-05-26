package com.gs.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.gs.common.exception.UnknownException;

/**
 * 日期工具类
 * 
 * @author fuqu
 */
public class DateUtils {


	private static SimpleDateFormat getCachedDateFormat(String mask) {
		return new SimpleDateFormat(mask);
	}

	public static String formatDate(Date aDate) {
		return format("yyyy-MM-dd", aDate);
	}

	public static String formatDateTime(Date date) {
		return format("yyyy-MM-dd HH:mm:ss", date);
	}

	public static String formatTime(Date date) {
		return format("HH:mm:ss", date);
	}

	public static String format(String aMask, Date aDate) {
		if (aDate == null) {
			return null;
		} else {
			return getCachedDateFormat(aMask).format(aDate);
		}
	}

	public static Date parseDate(String strDate) {
		return parse("yyyy-MM-dd", strDate);
	}

	public static Date parseDateTime(String strDate) {
		return parse("yyyy-MM-dd HH:mm:ss", strDate);
	}

	public static Date parseTime(String strDate) {
		strDate = formatDate(new Date()) + " " + strDate;
		return parse("yyyy-MM-dd HH:mm:ss", strDate);
	}

	public static Date parse(String aMask, String strDate) {
		if (strDate == null) {
			return null;
		} else {
			try {
				return getCachedDateFormat(aMask).parse(strDate);
			} catch (ParseException e) {
				throw new UnknownException(e);
			}
		}
	}


	public static Date addYears(Date date, int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	public static Date addMonths(Date date, int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	public static Date addWeeks(Date date, int amount) {
		return add(date, Calendar.WEEK_OF_YEAR, amount);
	}

	public static Date addDays(Date date, int amount) {
		return add(date, Calendar.DAY_OF_YEAR, amount);
	}

	public static Date addHours(Date date, int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	public static Date addMinutes(Date date, int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	public static Date addSeconds(Date date, int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	public static String getTodayString() {
		return format("yyyyMMdd", new Date());
	}

	private static Date add(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(calendarField, amount);
			return c.getTime();
		}
	}

	public static String nextDate(String strdate) throws ParseException {
		Date temp = parse("yyyy-MM-dd", strdate);
		Date next = new Date(temp.getTime() + 1 * 24 * 3600 * 1000);
		return formatDate(next);
	}

	public static Date parse(String valueStr) {
		return parse("yyyy-MM-dd HH:mm:ss".substring(0,valueStr.length()), valueStr);
	}

}
