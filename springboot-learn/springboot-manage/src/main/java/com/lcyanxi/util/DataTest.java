package com.lcyanxi.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRules;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/25/4:58 下午
 */
public class DataTest {

    public static void main(String[] args) {
        String timezone = "America/Atka";
        long timeMillis = System.currentTimeMillis();
        // 3.28 零点
        long time2 = 1711728000000L;

        long timeOffsetWithEast8 = getTimeOffsetWithEast8(time2, timezone);
        long a1 = timeOffsetWithEast8 / 1000 / 60 / 60;
        long currentTimeOffsetWithEast8 = getCurrentTimeOffsetWithEast8(timezone);
        long a2 = currentTimeOffsetWithEast8 / 1000 / 60 / 60;
        System.out.println("当前时间差值：" + a2 + " 凌晨：" + a1);
        System.out.println("当前时间：" + timeMillis + " 分区时间：" + (timeMillis - currentTimeOffsetWithEast8));
        System.out.println("当前时间：" + time2 + " 分区时间：" + (time2 + Math.abs(timeOffsetWithEast8)));
        long b = -1000;
        System.out.println(Math.abs(b));

    }


    private static long getTimeOffsetWithEast8(long timestamp, String timezone) {
        ZoneId zone1 = ZoneId.of("Asia/Shanghai");
        ZoneId zone2 = ZoneId.of(timezone);
        ZoneId standardZone = ZoneId.of("UTC"); // Standard time zone

        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime currentTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 获取两个时区下的时间
        ZonedDateTime time1 = ZonedDateTime.of(currentTime, zone1);
        ZonedDateTime time2 = ZonedDateTime.of(currentTime, zone2);

        // 计算时间差
        return Duration.between(time1, time2).toMillis();
    }

    private static long getCurrentTimeOffsetWithEast8(String timezone) {
        ZoneId zone1 = ZoneId.of("Asia/Shanghai");
        ZoneId zone2 = ZoneId.of(timezone);

        LocalDateTime currentTime = LocalDateTime.now();

        // 获取两个时区下的时间
        ZonedDateTime time1 = ZonedDateTime.of(currentTime, zone1);
        ZonedDateTime time2 = ZonedDateTime.of(currentTime, zone2);

        // 计算时间差
        return Duration.between(time1, time2).toMillis();
    }
}
