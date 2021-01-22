package com.lcyanxi.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author lichang
 * @date 2020/12/7
 */
public class DateUtils {

    /**
     * 获得当前时间之后指定天的时间
     * @return date
     */
    public static Date getNextDate(int days){
        //获得当前时间之后指定天数
        LocalDateTime tomorrow=LocalDateTime.now().plusDays(days);
        ZoneId zoneId=ZoneId.systemDefault();
        ZonedDateTime zonedDateTime=tomorrow.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 获得当前时间之后指定小时的时间
     * @return date
     */
    public static Date getNextHours(int hour){
        //获得当前时间之后指定天数
        LocalDateTime tomorrow=LocalDateTime.now().minusHours(hour);
        ZoneId zoneId=ZoneId.systemDefault();
        ZonedDateTime zonedDateTime=tomorrow.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }
}
