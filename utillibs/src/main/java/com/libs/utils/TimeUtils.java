package com.libs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Created by ql on 2016/11/8.
 */
public class TimeUtils {
    /**
     * 时间差计算
     *
     * @param timeStr 时间格式：yyyy-mm-dd hh:mm:ss.s
     * @return
     */
    public static String timeFormat(String timeStr) {
        String time = "";
        long days = 0, hours = 0, minutes = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
        try {//时间差计算
            Date d = format.parse(timeStr);
            long diff = System.currentTimeMillis() - d.getTime();
            days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (days == 0 && hours <= 0 && minutes >= 0) {
            time = minutes + "分钟前";
        } else if (days == 0 && hours > 0) {
            time = hours + "小时前";
        } else if (days > 0) {
            time = days + "天前";
        }
        return time;
    }

    /**
     * 时间戳转日期字符串
     *
     * @param timestamp 时间戳
     * @return 时间字符串 格式： yyyy-MM-dd
     */
    public static String dateTime2String(long timestamp) {
        return timeStamp2String(timestamp, "yyyy-MM-dd");
    }

    /**
     * 时间戳转日期字符串
     *
     * @param timestamp 时间戳
     * @param pattern   ForMat格式,如：yyyy-MM-dd/yyyymmdd/yyyy-MM-dd HH:mm:ss...
     * @return 时间字符串 格式： yyyy-MM-dd/yyyyMMdd...
     */
    public static String timeStamp2String(long timestamp, String pattern) {
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        date = format.format(new Date(timestamp));
        return date;
    }


    /**
     * 比较日期大小
     *
     * @param data1 日期1，格式yyyy-mm-dd
     * @param data2 日期2，格式yyyy-mm-dd
     * @return 1，大于；0，等于；-1，小于；-2,比较失败.
     */
    public static int compareDate(String data1, String data2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(data1);
            Date dt2 = df.parse(data2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    public static Date timeString2Date(String timeString) {
        return timeString2Date(timeString, "yyyy-MM-dd HH:mm");
    }

    public static Date timeString2Date(String timeString, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date dt1 = null;
        try {
            dt1 = df.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt1;
    }

    public static String date2TimeString(Date date, String pattern) {
        SimpleDateFormat s = new SimpleDateFormat(pattern);
        return s.format(date);
    }

    /**
     * 时间差计算,返回计算好的时间字符串和天、小时、分钟
     *
     * @param firstDate  第一个时间戳
     * @param secondDate 第二个时间戳
     * @param day        返回相差的天数
     * @param hour       返回相差的小时数
     * @param minute     返回相差的分钟数
     * @return 拼接好的字符串
     */
    public static String timeDiff(long firstDate, long secondDate, byte[] day, byte[] hour, byte[] minute) {
        String time = "";
        long days = 0, hours = 0, minutes = 0;

        long diff = firstDate - secondDate;

        days = diff / (1000 * 60 * 60 * 24);
        hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);

        if (days > 0) {
            time = days + "天";
        }
        if (days == 0 && hours > 0) {
            time += hours + "小时";
        }
        if (days == 0 && hours <= 0 && minutes >= 0) {
            time += minutes + "分钟";
        }
        ByteUtils.long2Bytes(day, days, 0);
        ByteUtils.long2Bytes(hour, hours, 0);
        ByteUtils.long2Bytes(minute, minutes, 0);
        return time;
    }
}
