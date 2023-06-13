package com.linsheng.FATJS.activitys;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.AccUtils;
import com.linsheng.FATJS.bean.Variable;

public class FloatingButton extends Service {
    private static final String TAG = "FATJS";
    private WindowManager wm;
    private LinearLayout ll;
    private int offset_y = -730;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return null;
    }

    private void setTypePhone(WindowManager.LayoutParams parameters) {
        Log.i(TAG, "onCreate: Build.VERSION.SDK_INT => " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT < 28) {
            parameters.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 定义面板
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Variable.btnTextView = new TextView(Variable.context);
        ll = new LinearLayout(Variable.context);

        ViewGroup.LayoutParams txtParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Variable.btnTextView.setText("打开");
        Variable.btnTextView.setTextSize(13);
        Variable.btnTextView.setTextColor(Color.argb(200,10,250,0));
        Variable.btnTextView.setPadding(5,2,5,5);
        Variable.btnTextView.setLayoutParams(txtParameters);

        // LinearLayout 容器
        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(180,0,0,0));
        ll.setPadding(5, 2, 5, 5);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(llParameters);

        // 设置面板
        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(90, 60, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(parameters); //悬浮窗适配低版本安卓
        parameters.x = 20;
        parameters.y = offset_y;
        parameters.gravity = Gravity.RIGHT | Gravity.CENTER;
        parameters.setTitle("FATJS");

        // 添加元素到面板
        ll.addView(Variable.btnTextView);
        wm.addView(ll, parameters);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 测试方法
                //testMethod(); // 这个和下面这个 btnClick() 不能同时开启，只能开一个，否则会冲突

                // 改变悬浮窗大小
                btnClick();
            }
        });
    }

    private void btnClick() {
        if ("打开".contentEquals(Variable.btnTextView.getText())) {
            Log.i(TAG, "onClick: 打开 --> 全屏");
            Variable.btnTextView.setText("全屏");

            // 展开悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "show_max");
            Variable.context.sendBroadcast(intent);
        }else if("隐藏".contentEquals(Variable.btnTextView.getText())){
            Log.i(TAG, "onClick: 隐藏 --> 打开");
            Variable.btnTextView.setText("打开");

            // 隐藏悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "hide_mini");
            Variable.context.sendBroadcast(intent);
        }else if("全屏".contentEquals(Variable.btnTextView.getText())) {
            Log.i(TAG, "onClick: 全屏 --> 隐藏");
            Variable.btnTextView.setText("隐藏");

            // 隐藏悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "full_screen");
            Variable.context.sendBroadcast(intent);
        }
    }

    /**
     * 测试方法
     */
    private void testMethod() {
        try {

            // 将测试的动作写到这里，点击悬浮船的 打开 按钮，就可以执行
            AccUtils.printLogMsg("返回桌面"); // 悬浮窗打印日志
            AccUtils.home();// 返回桌面

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}