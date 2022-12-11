package com.lcyanxi.manage;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/09/14/12:03 下午
 */
public class DataTest {
    public static void main(String[] args) {
        // 获取一天的开始时间
        LocalDateTime localDateTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        long timestamp = localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

        System.out.println("-----------" + localDateTime);
        System.out.println("-----------" + timestamp);

        String aa = null;
        System.out.println("," + aa);

        System.out.println(timeStamp2Date("1664189772637",null));
    }

    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.parseLong(seconds + "000")));
    }

}
