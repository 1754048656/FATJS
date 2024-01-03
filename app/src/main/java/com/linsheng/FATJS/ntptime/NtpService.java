package com.linsheng.FATJS.ntptime;

import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NtpService {
    private static final String TAG = tag;
    // "ntp1.aliyun.com" 不太稳
    private static String[] ntpServerPool = {
            "ntp2.aliyun.com", "ntp3.aliyun.com", "ntp4.aliyun.com",
            "ntp5.aliyun.com", "ntp6.aliyun.com", "ntp7.aliyun.com"};

    public static long calibrationTime() {
        SntpClient sntpClient = new SntpClient();
        List<Long> ntpTimeList = new ArrayList<>();
        int index = 0;
        for (String serverHost : ntpServerPool) {
            if (sntpClient.requestTime(serverHost, 3000)) {
                long ntpTime = sntpClient.getNtpTime();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long ntpTimeReference = sntpClient.getNtpTimeReference();
                long now = sntpClient.getNtpTime() + SystemClock.elapsedRealtime() - sntpClient.getNtpTimeReference();
                // Log.d(TAG, String.format("Host:%s -> ntpTime = %s, elapsedRealtime = %s, ntpTimeReference = %s.", serverHost, ntpTime, elapsedRealtime, ntpTimeReference));
                long timeMillis = System.currentTimeMillis();
                long offset = (timeMillis - now);
                Log.i(TAG, "calibrationTime ntpNow: " + now);
                Log.i(TAG, "calibrationTime timeMillisNow: " + timeMillis);
                Log.i(TAG, "calibrationTime offset: " + offset);
                ntpTimeList.add(offset);
                index ++;
            }
        }
        if (ntpTimeList.size() == 0) {
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
