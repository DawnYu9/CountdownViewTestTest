package cn.dawnyu.view.library;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * description:
 * date: 17/11/24
 * version:
 */

public class TimeUtils {
    /**
     * 比较2个日期的大小
     *
     * @param date1
     * @param date2
     * @param format 日期格式，不指定则默认为"yyyy-MM-dd"
     * @return 1大，0相等，-1小
     */
    public static int compareDate(String date1, String date2, String format) {
        if (Utils.isNullOrEmpty(format)) {
            format = "yyyy-MM-dd";
        }
        DateFormat df = new SimpleDateFormat(format, Locale.getDefault());

        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取2个时间相差多少年、月、天、小时、分钟、秒
     *
     * @param nextTime     传空则默认为当前时间
     * @param previousTime 传空则默认为当前时间
     * @param format       时间格式，传空则默认为"yyyy-MM-dd HH:mm:ss"
     * @return "年、月、天、小时、分钟、秒"拼接好的字符串
     */
    public static String getTimeInterval(String nextTime, String previousTime, String format) {
        if (Utils.isNullOrEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date nextDate;
        Date previousDate;
        Calendar nextCal = Calendar.getInstance();
        Calendar previousCal = Calendar.getInstance();

        try {
            nextDate = dateFormat.parse(Utils.isNullOrEmpty(nextTime) ? getStringDate(format) : nextTime);
            nextCal.setTime(nextDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            previousDate = dateFormat.parse(Utils.isNullOrEmpty(previousTime) ? getStringDate(format) : previousTime);
            previousCal.setTime(previousDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getTimeInterval(nextCal, previousCal);
    }

    /**
     * 获取2个日历类型的时间相差多少年、月、天、小时、分钟、秒
     *
     * @param nextCal     传空则默认为当前时间
     * @param previousCal 传空则默认为当前时间
     * @return "年、月、天、小时、分钟、秒"拼接好的字符串
     */
    public static String getTimeInterval(Calendar nextCal, Calendar previousCal) {
        try {
            int[] interval = getTimeIntervalArray(nextCal, previousCal);

            return interval[0] + "年" + interval[1] + "月" + interval[2] + "天" + interval[3] + "小时" + interval[4] + "分钟" + interval[5] + "秒";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取2个时间相差多少年、月、天、小时、分钟、秒
     *
     * @param nextTime     传空则默认为当前时间
     * @param previousTime 传空则默认为当前时间
     * @param format       时间格式，传空则默认为"yyyy-MM-dd HH:mm:ss"
     * @return "年、月、天、小时、分钟、秒"的数组形式
     */
    public static int[] getTimeIntervalArray(String nextTime, String previousTime, String format) {
        if (Utils.isNullOrEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date nextDate;
        Date previousDate;
        Calendar nextCal = Calendar.getInstance();
        Calendar previousCal = Calendar.getInstance();

        try {
            nextDate = dateFormat.parse(Utils.isNullOrEmpty(nextTime) ? getStringDate(format) : nextTime);
            nextCal.setTime(nextDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            previousDate = dateFormat.parse(Utils.isNullOrEmpty(previousTime) ? getStringDate(format) : previousTime);
            previousCal.setTime(previousDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getTimeIntervalArray(nextCal, previousCal);
    }

    /**
     * 获取2个日历类型的时间相差多少年、月、天、小时、分钟、秒
     *
     * @param nextCal     传空则默认为当前时间
     * @param previousCal 传空则默认为当前时间
     * @return "年、月、天、小时、分钟、秒"的数组形式
     */
    public static int[] getTimeIntervalArray(Calendar nextCal, Calendar previousCal) {
        int year = nextCal.get(Calendar.YEAR) - previousCal.get(Calendar.YEAR);

        int month = nextCal.get(Calendar.MONTH) - previousCal.get(Calendar.MONTH);
        if (month < 0 && year > 0) {
            year--;
            month += 12;
        }

        int day = nextCal.get(Calendar.DAY_OF_MONTH) - previousCal.get(Calendar.DAY_OF_MONTH);
        if (day < 0 && month > 0) {
            month--;
            day += nextCal.get(Calendar.DAY_OF_MONTH);
        }

        int hour = nextCal.get(Calendar.HOUR_OF_DAY) - previousCal.get(Calendar.HOUR_OF_DAY);
        if (hour < 0 && day > 0) {
            day--;
            hour += 24;
        }

        int min = nextCal.get(Calendar.MINUTE) - previousCal.get(Calendar.MINUTE);
        if (min < 0 && hour > 0) {
            hour--;
            min += 60;
        }

        int second = nextCal.get(Calendar.SECOND) - previousCal.get(Calendar.SECOND);
        if (second < 0 && min > 0) {
            min--;
            second += 60;
        }

        return new int[]{year, month, day, hour, min, second};
    }

    /**
     * 获取当前时间的 template 格式
     *
     * @param template 传空，则默认为"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getStringDate(String template) {
        if (Utils.isNullOrEmpty(template)) {
            template = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(template, Locale.getDefault());
        return formatter.format(new Date());
    }
}
