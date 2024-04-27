package com.linsheng.FATJS.service;

import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;

import com.linsheng.FATJS.R;
import com.linsheng.FATJS.config.GlobalVariableHolder;

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
            printLogMsg("提高进程优先级", 0);
            // intent.putExtra("main", "hello ");

            //---------------------------------------------
            String CHANNEL_ONE_ID = "com.primedu.cn";
            String CHANNEL_ONE_NAME = "Channel One";

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            //--------------------------------------------------------
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            Notification.Builder builder = new Notification.Builder(this).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo))
                    .setContentTitle("FATJS正在运行中")
                    .setContentIntent(pendingIntent)
                    .setContentText("MyService is running......") // 设置上下文内容
                    .setActions();

            /*以下是对Android 8.0的适配*/
            //普通notification适配
            builder.setChannelId("notification_id");
            //前台服务notification适配
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);

            Notification notification = builder.build(); // 获取构建好的Notification
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
            startForeground(1, notification);

            //notification.flags |= Notification.FLAG_NO_CLEAR;
            //startForeground(1, notification);

            return START_STICKY;
        }catch (Exception ignored) {}
        return 1;
    }
}