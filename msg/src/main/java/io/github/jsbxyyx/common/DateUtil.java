package io.github.jsbxyyx.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author
 * @since
 */
public class DateUtil {

    public static String format(Date date, String pattern) {
        String format = new SimpleDateFormat(pattern).format(date);
        return format;
    }

    public static Date parse(String date, String pattern) {
        try {
            Date parse = new SimpleDateFormat(pattern).parse(date);
            return parse;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
