package com.linsheng.FATJS.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public TimeUtil() {
    }

    public static String strDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        return formatter.format(currentTime);
    }

    public static String getYesterdayDate() {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date d = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        return formatter.format(d);
    }

    //判断当前是上午中午下午
    int getTimeType(Date time) {
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(time);
        int a = Integer.parseInt(str);
        //上午
        if (a >= 4 && a <= 12) {
            return 1;
        }
        //下午
        if (a > 12 && a <= 24) {
            return 2;
        }
        return 1;
    }

    public static String getStrDate(String pattern) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(currentTime);
    }

    // 根据年月日对比时间大小   传入的格式必须是  2000年12月6日   , 如果是一样的也返回 false
    public static Boolean leftLargerTimeStr(String timeStr, String targetTimeStr) {

        String year_str = timeStr.split("年")[0];
        String month_str = timeStr.split("年")[1].split("月")[0];
        String day_str = timeStr.split("年")[1].split("月")[1].split("日")[0];

        String target_year_str = targetTimeStr.split("年")[0];
        String target_month_str = targetTimeStr.split("年")[1].split("月")[0];
        String target_day_str = targetTimeStr.split("年")[1].split("月")[1].split("日")[0];

        int year = Integer.parseInt(year_str); // 获取年
        int month = Integer.parseInt(month_str); // 获取月
        int day = Integer.parseInt(day_str); // 获取日

        int target_year = Integer.parseInt(target_year_str); // 获取年
        int target_month = Integer.parseInt(target_month_str); // 获取月
        int target_day = Integer.parseInt(target_day_str); // 获取日

        if (year > target_year) {
            return true;
        }else if (year < target_year) {
            return false;
        }

        if (month > target_month) {
            return true;
        }else if (month < target_month) {
            return false;
        }

        return day > target_day;
    }

    // 如果只有 月和日 则添加上年份  param 格式  2月2日
    public static String fixTimeStrAddYear(String timeStr) {
        if (timeStr.contains("年")) {
            return timeStr;
        }
        String year_str = getStrDate("yyyy年");
        return year_str + timeStr;
    }
}


// 2022年5月30日
// 5月30日
//        String strDate = TimeUtil.getStrDate("yyyy年MM月dd日");
//        thiz.printLogMsg("strDate => " + strDate);
//
//        String strDate_0 = TimeUtil.getStrDate("MM月dd日");
//        strDate_0 = TimeUtil.fixTimeStrAddYear(strDate_0);
//        thiz.printLogMsg("strDate_0 => " + strDate_0);
//
//        String strDate_1 = TimeUtil.getStrDate("yyyy年");
//        thiz.printLogMsg("strDate_1 => " + strDate_1);
//
//        Boolean leftLargerTimeStr = TimeUtil.leftLargerTimeStr("2023年02月15日", "2023年2月17日");
//        thiz.printLogMsg("leftLargerTimeStr => " + leftLargerTimeStr);
//
//        thiz.timeSleep(waitSixSecond * 900);
