package com.linsheng.FATJS.activitys;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.node.TaskBase;
import com.linsheng.FATJS.utils.ExceptionUtil;

public class FloatingButton extends Service {
    private static final String TAG = GlobalVariableHolder.tag;
    private WindowManager wm;
    private LinearLayout ll;
    private final int btn_w = (mWidth / 8);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        printLogMsg("onBind: ", 0);
        return null;
    }

    private void setTypePhone(WindowManager.LayoutParams parameters) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parameters.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RtlHardcoded")
    @Override
    public void onCreate() {
        super.onCreate();
        printLogMsg("Build.VERSION.SDK: " + Build.VERSION.SDK_INT, 0);
        printLogMsg("---------------", 0);
        printLogMsg("mWidth: " + mWidth, 0);
        printLogMsg("mHeight: " + mHeight, 0);
        printLogMsg("__mHeight: " + __mHeight, 0);
        printLogMsg("statusBarHeight: " + statusBarHeight, 0);
        printLogMsg("navigationBarHeight: " + navigationBarHeight, 0);
        printLogMsg("navigationBarOpen: " + navigationBarOpen, 0);
        Log.i(TAG, "onCreate FloatingButton");
        int btn_h = (int) (btn_w * 0.5638461538); // 按照比例调整
        // 定义面板
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Log.e(TAG, "GlobalVariableHolder.context => " + GlobalVariableHolder.context);
        GlobalVariableHolder.btnTextView = new TextView(GlobalVariableHolder.context);
        ll = new LinearLayout(GlobalVariableHolder.context);

        ViewGroup.LayoutParams txtParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GlobalVariableHolder.btnTextView.setText("打开");
        GlobalVariableHolder.btnTextView.setTextSize((float) (text_size + 2));
        GlobalVariableHolder.btnTextView.setGravity(Gravity.CENTER); //文字居中
        GlobalVariableHolder.btnTextView.setTextColor(Color.argb(255,255,255,255));
        GlobalVariableHolder.btnTextView.setLayoutParams(txtParameters);

        // LinearLayout 容器
        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(180,0,0,0));
        ll.setGravity(Gravity.CENTER); //文字居中
        ll.setOrientation(LinearLayout.VERTICAL); //线性布局
        ll.setLayoutParams(llParameters);

        // 设置面板
        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(parameters);
        parameters.x = 130;
        //private int offset_y = (mHeight / 8);
        parameters.y = 0;
        parameters.gravity = Gravity.RIGHT | Gravity.TOP;
        parameters.setTitle("FATJS");

        // 添加元素到面板
        ll.addView(GlobalVariableHolder.btnTextView);
        wm.addView(ll, parameters);

        ll.setOnClickListener((v) -> {
            if (DEV_MODE) {
                // FATJS 的开发者模式
                testMethodPre();
            } else {
                // 改变悬浮窗大小
                btnClick();
            }
        });

        moveBtn(parameters);
    }

    private void moveBtn(WindowManager.LayoutParams parameters) {
        // 监听触摸，移动
        ll.setOnTouchListener(new View.OnTouchListener() {
            int x, y;
            float touchedX, touchedY;
            private final WindowManager.LayoutParams updatedParameters = parameters;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;
                        touchedX = event.getRawX();
                        touchedY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x - (event.getRawX() - touchedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - touchedY));
                        wm.updateViewLayout(ll, updatedParameters);
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public static class CustomLinearLayout extends LinearLayout {
        public CustomLinearLayout(Context context) {
            super(context);
        }

        // 构造方法和其他方法

        @Override
        public boolean performClick() {
            // 在这里处理点击事件
            printLogMsg("xxx");
            // 在点击时执行其他逻辑或动作

            return super.performClick();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void testMethodPre() {
        // 判断是否有任务正在执行
        if (isRunning) {
            killThread = true;
            printLogMsg("有任务正在执行", 0);
            Toast.makeText(context, "有任务正在执行", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            try {
                printLogMsg("w => " + mWidth + ", h => " + mHeight);
                // 测试方法
                testMethod();
            } catch (Exception e) {
                printLogMsg(ExceptionUtil.toString(e));
            }
        }).start();
    }

    private void btnClick() {
        if ("打开".contentEquals(GlobalVariableHolder.btnTextView.getText())) {
            Log.i(TAG, "onClick: 打开 --> 全屏");
            if (isRunning) {
                isStop = false;
                printLogMsg("开始运行", 0);
            }
            GlobalVariableHolder.btnTextView.setText("全屏");

            // 展开悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "show_max");
            GlobalVariableHolder.context.sendBroadcast(intent);
        }else if("隐藏".contentEquals(GlobalVariableHolder.btnTextView.getText())){
            Log.i(TAG, "onClick: 隐藏 --> 打开");
            if (isRunning) {
                isStop = true;
                printLogMsg("暂停中", 0);
            }
            GlobalVariableHolder.btnTextView.setText("打开");

            // 隐藏悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "hide_mini");
            GlobalVariableHolder.context.sendBroadcast(intent);
        }else if("全屏".contentEquals(GlobalVariableHolder.btnTextView.getText())) {
            Log.i(TAG, "onClick: 全屏 --> 隐藏");
            if (isRunning) {
                isStop = true;
                printLogMsg("暂停中", 0);
            }
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void testMethod() {
        // 将测试的动作写到这里，点击悬浮窗的 打开 按钮，就可以执行
        TaskBase taskDemo = new TaskBase();
        @SuppressLint("SdCardPath") String script_path = "/sdcard/FATJS_DIR/dev_script.js";
        printLogMsg("script_path => " + script_path, 0);
        taskDemo.initJavet(script_path);
    }
}