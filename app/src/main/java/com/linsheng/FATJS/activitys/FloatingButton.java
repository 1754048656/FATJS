package com.linsheng.FATJS.activitys;

import static com.linsheng.FATJS.node.AccUtils.*;
import static com.linsheng.FATJS.config.GlobalVariableHolder.*;

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

import com.linsheng.FATJS.node.AccUtils;
import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.node.UiSelector;
import com.linsheng.FATJS.utils.ExceptionUtil;

public class FloatingButton extends Service {
    private static final String TAG = GlobalVariableHolder.tag;
    private WindowManager wm;
    private LinearLayout ll;
    private int offset_y = (mHeight / 7);
    private int btn_w = (mWidth / 11);
    private int btn_h = (mHeight / 42);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        printLogMsg("onBind: ");
        return null;
    }

    private void setTypePhone(WindowManager.LayoutParams parameters) {
        printLogMsg("onCreate: Build.VERSION.SDK_INT => " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT < 27) {
            parameters.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        btn_h = (int)(btn_w * 0.5638461538); // 按照比例调整
        // 定义面板
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        GlobalVariableHolder.btnTextView = new TextView(GlobalVariableHolder.context);
        ll = new LinearLayout(GlobalVariableHolder.context);

        ViewGroup.LayoutParams txtParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GlobalVariableHolder.btnTextView.setText("打开");
        GlobalVariableHolder.btnTextView.setTextSize((float) (text_size + 2));
        GlobalVariableHolder.btnTextView.setGravity(Gravity.CENTER); //文字居中
        GlobalVariableHolder.btnTextView.setTextColor(Color.argb(200,10,250,0));
        GlobalVariableHolder.btnTextView.setLayoutParams(txtParameters);

        // LinearLayout 容器
        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(180,0,0,0));
        ll.setGravity(Gravity.CENTER); //文字居中
        ll.setOrientation(LinearLayout.VERTICAL); //线性布局
        ll.setLayoutParams(llParameters);

        // 设置面板
        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(parameters); //悬浮窗适配低版本安卓
        parameters.x = 20;
        parameters.y = offset_y;
        parameters.gravity = Gravity.RIGHT | Gravity.TOP;
        parameters.setTitle("FATJS");

        // 添加元素到面板
        ll.addView(GlobalVariableHolder.btnTextView);
        wm.addView(ll, parameters);

        ll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                // 测试方法
//                testMethodPre(); // 这个和下面这个 btnClick() 不能同时开启，只能开一个，否则会冲突

                // 改变悬浮窗大小
                btnClick();

            }
        });
    }

    private void testMethodPre() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    // 测试方法
                    testMethod();
                } catch (Exception e) {
                    AccUtils.printLogMsg(ExceptionUtil.toString(e));
                }
            }
        }).start();
    }

    private void btnClick() {
        if ("打开".contentEquals(GlobalVariableHolder.btnTextView.getText())) {
            Log.i(TAG, "onClick: 打开 --> 全屏");
            GlobalVariableHolder.btnTextView.setText("全屏");

            // 展开悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "show_max");
            GlobalVariableHolder.context.sendBroadcast(intent);
        }else if("隐藏".contentEquals(GlobalVariableHolder.btnTextView.getText())){
            Log.i(TAG, "onClick: 隐藏 --> 打开");
            GlobalVariableHolder.btnTextView.setText("打开");

            // 隐藏悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "hide_mini");
            GlobalVariableHolder.context.sendBroadcast(intent);
        }else if("全屏".contentEquals(GlobalVariableHolder.btnTextView.getText())) {
            Log.i(TAG, "onClick: 全屏 --> 隐藏");
            GlobalVariableHolder.btnTextView.setText("隐藏");

            // 隐藏悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "full_screen");
            GlobalVariableHolder.context.sendBroadcast(intent);
        }
    }

    /**
     * 测试方法
     */
    UiSelector acc = new UiSelector();
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void testMethod() {

        // 将测试的动作写到这里，点击悬浮窗的 打开 按钮，就可以执行
        moveFloatWindow("打开");
        printLogMsg("mWidth => " + mWidth + ", mHeight => " + mHeight);

    }
}