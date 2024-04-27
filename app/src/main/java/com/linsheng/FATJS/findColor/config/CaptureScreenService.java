package com.linsheng.FATJS.findColor.config;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import com.linsheng.FATJS.R;
import com.linsheng.FATJS.activitys.aione_editor.MainActivity;

import java.util.Objects;

public class CaptureScreenService extends Service {
    private int mResultCode;
    private Intent mResultData;
    private MediaProjectionManager projectionManager;
    private MediaProjection mMediaProjection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        mResultCode = intent.getIntExtra("code", -1);
        mResultData = intent.getParcelableExtra("data");

        this.projectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMediaProjection = projectionManager.getMediaProjection(mResultCode, Objects.requireNonNull(mResultData));

        ScreenCaptureManager.getInstance().start(mMediaProjection);
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class); //点击后跳转的界面，可以设置跳转数据

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("屏幕捕捉正在运行") // 设置下拉列表里的标题
                .setSmallIcon(R.drawable.logo) // 设置状态栏内的小图标@mipmap/ic_launcher
                .setContentText("CaptureScreenService is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        /*以下是对Android 8.0的适配*/
        //普通notification适配
        builder.setChannelId("notification_id");
        //前台服务notification适配
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(channel);

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);

    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
        stopForeground(true);
    }
}
