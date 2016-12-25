package com.monkeyk.sos.infrastructure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Shengzhao Li
 */
public abstract class DateUtils {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    /**
     * Private constructor
     */
    private DateUtils() {
    }

    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }


    public static String toDateTime(Date date) {
        return toDateTime(date, DEFAULT_DATE_TIME_FORMAT);
    }

    public static String toDateTime(Date dateTime, String pattern) {
    	pattern = pattern == null ? "yyyy-MM-dd HH:mm:ss" : pattern;
    	SimpleDateFormat format = new SimpleDateFormat(pattern);
		String datastring = format.format(dateTime);
		return datastring;
    }



    public static String toDateText(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        pattern = pattern == null ? "yyyy-MM-dd" : pattern;
    	SimpleDateFormat format = new SimpleDateFormat(pattern);
		String datastring = format.format(date);
		return datastring;
    }


}