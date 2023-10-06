package com.linsheng.FATJS.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.linsheng.FATJS.R;
import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.node.AccUtils;

public class MyService extends Service {
    private static final String TAG = GlobalVariableHolder.tag;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // 在该方法中实现服务的核心业务
    @SuppressLint("ResourceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // 提高进程优先级
            AccUtils.printLogMsg("提高进程优先级");
            intent.putExtra("main", "hello ");

            //---------------------------------------------
            String CHANNEL_ONE_ID = "com.primedu.cn";
            String CHANNEL_ONE_NAME = "Channel One";
            NotificationChannel notificationChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
            }

            //--------------------------------------------------------
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            Notification notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this).setChannelId(CHANNEL_ONE_ID)
                        .setTicker("Nature")
//                    .setSmallIcon(R.layout.about_btn)
                        .setContentTitle("FATJS_DIR")
                        .setContentIntent(pendingIntent)
                        .setActions()
                        .getNotification();
                notification.flags |= Notification.FLAG_NO_CLEAR;
                startForeground(1, notification);
            }
            return START_STICKY;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
}