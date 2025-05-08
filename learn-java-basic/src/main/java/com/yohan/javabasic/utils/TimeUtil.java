package com.yohan.javabasic.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * 时间工具.
 *
 * @author yohan
 * @Date 2025/05/08
 */
@Slf4j
public class TimeUtil extends DateUtils {

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static LocalTime END_TIME = LocalTime.of(23, 59, 59);

    public static String TIME_FORMAT = "HH:mm";

    public static String SECOND_FORMAT = "HH:mm:ss";

    public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间戳转为LocalDateTime
     */
    public static LocalDateTime fromStampMillis(long timeMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
    }

    /**
     * 时间戳转为LocalDateTime
     */
    public static LocalDateTime fromStampSecs(long timeSecs) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeSecs), ZoneId.systemDefault());
    }

    public static LocalDateTime longTOLocalDateTime(long timeMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
    }

    public static LocalDateTime longSecTOLocalDateTime(long timeSecs) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeSecs), ZoneId.systemDefault());
    }

    public static LocalDateTime secondTime2LocalDateTime(long timeSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeSeconds), ZoneId.systemDefault());
    }

    /**
     * yyyyMMdd格式日期转为LocalDate
     */
    public static LocalDate from_yyyyMMdd(String yyyyMMdd) {
        return LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * HHmm格式时间转为LocalTime
     */
    public static LocalTime from_HHmm(String HHmm) {
        return LocalTime.parse(HHmm, DateTimeFormatter.ofPattern("HHmm"));
    }

    public static String getInitialTime(String time) {
        String hour = "00";//小时
        String minutes = "00";//分钟
        String outTime = "00:00";

        String[] times = time.split(" ");
        StringTokenizer st = new StringTokenizer(times[1], ":");
        List<String> inTime = new ArrayList<String>();
        while (st.hasMoreElements()) {
            inTime.add(st.nextToken());
        }
        hour = inTime.get(0).toString();
        minutes = inTime.get(1).toString();
        if (Integer.parseInt(minutes) > 30) {
            hour = (Integer.parseInt(hour) + 1) + "";
            outTime = hour + ":00";
        } else {
            hour = Integer.parseInt(hour) + "";
            outTime = hour + ":30";
        }
        outTime = times[0] + " " + outTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            outTime = sdf.format(sdf.parse(outTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outTime;
    }

    /**
     * 比对俩个时间大小
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return boolean
     */
    public static boolean compare(Date date1, Date date2) {

        return date1.getTime() > date2.getTime();
    }

    /**
     * 时间字符串转时间
     *
     * @param time 格式yyyy-MM-dd HH:mm:ss
     * @return date
     */
    public static Date strToDate(Long time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time * 1000);
        format.format(date);
        return date;
    }

    public static String dateToStr(Date date, SimpleDateFormat format) {
        if (date == null || format == null) {
            return null;
        }
        return format.format(date);
    }

    public static String dateToStr(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String getNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static String getDataYMD(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    /**
     * 时间戳转字符串
     *
     * @param time 时间戳
     * @return
     */
    public static String longToStr(long time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(time);
    }

    /**
     * 时间戳转字符串
     * 分时计价
     *
     * @param time 时间戳
     * @return
     */
    public static String stampToStr(long time) {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(time * 1000);
    }

    public static String stampToStr1(long time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time * 1000);
    }

    /**
     * 字符串转时间戳
     *
     * @param time 时间戳
     * @return
     */
    public static long strToLong(String time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }

    public static long strToLong1(String time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }

    /**
     * 获取到今天最后一秒为止的时间戳
     *
     * @return long
     */
    public static long getDayEndSecond() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = sdf.format(new Date());
        String nowEnd = nowDay.concat(" 23:59:59");
        return strToDate(Long.parseLong(nowEnd)).getTime() / 1000;
    }

    /**
     * 获取当前时区的秒
     * @param now
     * @return
     */
    public static long getEpochSecond(LocalDateTime now){
        return now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取yyyyMMddHHmmss格式的当前时间字符串
     *
     * @return String
     */
    public static String getNowForString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    /**
     *
     */
    public static Date strToDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * HH:mm:ss 字符串转Date
     * 分时计价
     *
     * @param time
     * @return
     */
    public static Date shortToDate(String time) {
        Date date = new Date();
        try {
            //注意format的格式要与日期String的格式相匹配
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Long localDate2Long(LocalDate date) {
        LocalDate localDate = LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String format = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Long.parseLong(format);
    }

    public static Long localDateTime2Long(LocalDateTime date){
        return date.toEpochSecond(ZoneOffset.of("+8"));
    }

    public static Integer localDate2Int(LocalDate date) {
        LocalDate localDate = LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String format = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Integer.parseInt(format);

    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     * 分时计价
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {

        //时间左闭右开取值方式，比如08:00-10:00起步价为30，10:00-12:00起步价为35，下单时间8:00整时起步价为30，下单时间10:00整起步价为35
        if (nowTime.getTime() == startTime.getTime()) {
            return true;
        }

        //针对23:59:59 这种时间进行兜底判断
        LocalDateTime localDateTime = date2LocalDateTime(nowTime);
        LocalTime localTime = localDateTime.toLocalTime();
        if (END_TIME.equals(localTime)) {
            return true;
        }


        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEffectiveLocalDateTime(LocalDateTime nowTime,LocalDateTime startTime,LocalDateTime endTime){
        return isEffectiveDate(localDateTimeToDate(nowTime),localDateTimeToDate(startTime),localDateTimeToDate(endTime));
    }


    /**
     * 获取当前时间， 去除毫秒
     */
    public static LocalDateTime getNowTime() {
        String format = DATE_TIME_FORMATTER.format(getNowDateTime());
        return LocalDateTime.parse(format, DATE_TIME_FORMATTER);
    }

    /**
     * 获取当前时间，
     * 以后统一用该方法，方便simulate进行切面处理
     *
     * @return
     */
    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now();
    }

    public static Long getCurrentTimeMillis() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 两个date相减得秒数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getTimeDelta(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        long timeDelta = (date1.getTime() - date2.getTime()) / 1000;//单位是秒
        return timeDelta > 0 ? (int) timeDelta : (int) Math.abs(timeDelta);
    }

    /**
     * java.comparator.Date --> java.time.LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime uDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * java.time.LocalDateTime --> java.comparator.Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static LocalDateTime formatStr2LocalDateTime(String time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return uDateToLocalDateTime(sdf.parse(time));
        } catch (ParseException e) {
            return null;
        }
    }

    /** LocalDateTime转String-默认版 */
    public static String localDateTimeToStr(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(localDateTime);
    }

    /** LocalDateTime转String-formatter版 */
    public static String localDateTimeToStr(LocalDateTime localDateTime,DateTimeFormatter formatter) {
        if (localDateTime == null || formatter == null) {
            return null;
        }
        return formatter.format(localDateTime);
    }

    /** LocalDateTime转String-pattern版 */
    public static String localDateTimeToStr(LocalDateTime localDateTime,String pattern) {
        if (localDateTime == null || StringUtils.isEmpty(pattern)) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    public static long getSecondTimestamp(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return 0L;
        }
        return localDateTime.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 时间戳转成时间点
     *
     * @param time 时间戳 秒
     * @return
     */
    public static LocalTime stampToLocalTime(long time) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time * 1000), ZoneId.systemDefault());
        return localDateTime.toLocalTime();
    }


    public static LocalTime formatStr2LocalTime(String time, String pattern) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(pattern)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return uDateToLocalTime(sdf.parse(time));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatLocalTime2Str(LocalTime time, String pattern) {
        if (time == null || StringUtils.isEmpty(pattern)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return time.format(formatter);
    }

    public static LocalDateTime formatDateStr2LocalDateTime(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        LocalDate dateTime = LocalDate.parse(str, DATE_FORMATTER);
        return LocalDateTime.of(dateTime, LocalTime.MIN);
    }

    /**
     * java.comparator.Date --> java.time.LocalTime
     *
     * @param date
     * @return
     */
    public static LocalTime uDateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 订单是否在30分钟内
     *
     * @param orderTimeS 订单时间 - 单位秒
     */
    public static boolean isOrderLessThen30Min(Long orderTimeS, long nowSeconds) {
        long _30MinSecond = 1800;
        if (orderTimeS == null) {
            orderTimeS = System.currentTimeMillis() / 1000;
        }
        if (orderTimeS < nowSeconds + _30MinSecond) {
            return true;
        }
        return false;
    }

    public static boolean isOrderLessThen(Long orderTimeS, long nowSeconds, long diff) {
        if (orderTimeS == null) {
            orderTimeS = System.currentTimeMillis() / 1000;
        }
        if (orderTimeS < nowSeconds + diff) {
            return true;
        }
        return false;
    }

    /**
     * 订单是否在30分钟内
     *
     * @param orderTimeS 订单时间 - 单位秒
     */
    public static boolean isOrderLessThen30Min(Long orderTimeS) {
        return isOrderLessThen30Min(orderTimeS, System.currentTimeMillis() / 1000);
    }

    /**
     * 订单是否在30分钟内
     *
     * @param orderTimeSStr 订单时间
     */
    public static boolean isOrderLessThen30Min(String orderTimeSStr) {
        Long orderTimeS = System.currentTimeMillis() / 1000;
        try {
            orderTimeS = Long.valueOf(orderTimeSStr);
        } catch (Exception e) {
            log.error("isOrderLessThen30Min : ", e);
        }
        return isOrderLessThen30Min(orderTimeS);
    }

    /**
     * 返回
     * from <= waitCompare <= end
     *
     * @param waitCompareSecond
     * @param from
     * @param end
     * @return
     */
    public static boolean isBetween(Long waitCompareSecond, LocalDateTime from, LocalDateTime end) {
        LocalDateTime waitCompare = TimeUtil.secondTime2LocalDateTime(waitCompareSecond);
        if (from == null || end == null) {
            return false;
        }
        return !waitCompare.isBefore(from) && !waitCompare.isAfter(end);
    }

    public static boolean isBetween(LocalDateTime waitCompare, LocalDateTime from, LocalDateTime end) {
        return !waitCompare.isBefore(from) && !waitCompare.isAfter(end);
    }

    public static boolean isBetween(LocalTime waitCompare, LocalTime from, LocalTime end) {
        return !waitCompare.isBefore(from) && !waitCompare.isAfter(end);
    }

    /**
     * 左闭右开
     *
     * @param waitCompare
     * @param from
     * @param end
     * @return
     */
    public static boolean leftCloseRightOpenBetween(LocalTime waitCompare, LocalTime from, LocalTime end) {
        return (from.isBefore(waitCompare) || from.equals(waitCompare)) && end.isAfter(waitCompare);
    }

    /**
     * 左闭右闭
     *
     * @param waitCompare
     * @param from
     * @param end
     * @return
     */
    public static boolean leftCloseRightCloseBetween(LocalTime waitCompare, LocalTime from, LocalTime end) {
        return (from.isBefore(waitCompare) || from.equals(waitCompare)) && (end.isAfter(waitCompare) || end.equals(waitCompare));
    }

    /**
     * 返回
     * from <= waitCompare <= end
     *
     * @param waitCompare
     * @param from
     * @param end
     * @return
     */
    public static boolean isBetween(Long waitCompare, Long from, Long end) {
        return waitCompare >= from && waitCompare <= end;
    }

    public static Long localDateTimeToMills(LocalDateTime localDateTime){
        if(localDateTime==null){
            return null;
        }
        return localDateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }


}
