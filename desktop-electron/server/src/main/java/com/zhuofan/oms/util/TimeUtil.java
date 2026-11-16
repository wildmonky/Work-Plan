package com.zhuofan.oms.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public final static DateTimeFormatter LOCAL_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(LOCAL_DATE_TIME_PATTERN);
    }

}
