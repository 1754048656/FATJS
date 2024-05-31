package com.linsheng.FATJS.ntptime;

import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NtpService {
    private static final String TAG = tag;
    // "ntp1.aliyun.com" 不太稳
    private static String[] ntpServerPoolAli = {
            "ntp2.aliyun.com", "ntp3.aliyun.com", "ntp4.aliyun.com",
            "ntp5.aliyun.com", "ntp6.aliyun.com", "ntp7.aliyun.com"};

    private static String[] ntpServerPoolTencent = {
            "ntp.tencent.com", "ntp1.tencent.com", "ntp2.tencent.com",
            "ntp3.tencent.com", "ntp4.tencent.com", "ntp5.tencent.com"};

    public static long calibrationTimeTencent() {
        SntpClient sntpClient = new SntpClient();
        List<Long> ntpTimeList = new ArrayList<>();
        int index = 0;
        for (String serverHost : ntpServerPoolTencent) {
            if (sntpClient.requestTime(serverHost, 3000)) {
                long ntpTime = sntpClient.getNtpTime();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long ntpTimeReference = sntpClient.getNtpTimeReference();
                long now = sntpClient.getNtpTime() + SystemClock.elapsedRealtime() - sntpClient.getNtpTimeReference();
                // Log.d(TAG, String.format("Host:%s -> ntpTime = %s, elapsedRealtime = %s, ntpTimeReference = %s.", serverHost, ntpTime, elapsedRealtime, ntpTimeReference));
                long timeMillis = System.currentTimeMillis();
                long offset = (timeMillis - now);
                // Log.i(TAG, "calibrationTime Tencent ntpNow: " + now);
                // Log.i(TAG, "calibrationTime Tencent timeMillisNow: " + timeMillis);
                Log.i(TAG, "calibrationTime Tencent offset: " + offset);
                ntpTimeList.add(offset);
                index ++;
            }
        }
        if (ntpTimeList.isEmpty()) {
            return -1;
        }
        Log.i(TAG, "calibrationTime: index: " + index);
        // 计算平均值
        return calculateAverage(ntpTimeList);
    }

    public static long calibrationTimeAli() {
        SntpClient sntpClient = new SntpClient();
        List<Long> ntpTimeList = new ArrayList<>();
        int index = 0;
        for (String serverHost : ntpServerPoolAli) {
            if (sntpClient.requestTime(serverHost, 3000)) {
                long ntpTime = sntpClient.getNtpTime();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long ntpTimeReference = sntpClient.getNtpTimeReference();
                long now = sntpClient.getNtpTime() + SystemClock.elapsedRealtime() - sntpClient.getNtpTimeReference();
                // Log.d(TAG, String.format("Host:%s -> ntpTime = %s, elapsedRealtime = %s, ntpTimeReference = %s.", serverHost, ntpTime, elapsedRealtime, ntpTimeReference));
                long timeMillis = System.currentTimeMillis();
                long offset = (timeMillis - now);
                // Log.i(TAG, "calibrationTime Ali ntpNow: " + now);
                // Log.i(TAG, "calibrationTime Ali timeMillisNow: " + timeMillis);
                Log.i(TAG, "calibrationTime Ali offset: " + offset);
                ntpTimeList.add(offset);
                index ++;
            }
        }
        if (ntpTimeList.isEmpty()) {
            return -1;
        }
        Log.i(TAG, "calibrationTime: index: " + index);
        // 计算平均值
        return calculateAverage(ntpTimeList);
    }

    public static long sysTime() {
        return System.currentTimeMillis();
    }

    private static long calculateAverage(List<Long> list) {
        long sum = 0;
        // 计算总和
        for (long value : list) {
            sum += value;
        }
        // 计算平均值
        return sum / list.size();
    }
}
