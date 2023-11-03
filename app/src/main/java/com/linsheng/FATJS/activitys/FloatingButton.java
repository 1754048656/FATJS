package com.linsheng.FATJS.activitys;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
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
    private int btn_h = (mWidth / 8);


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
        btn_h = (int) (btn_w); // 按照比例调整
        // 定义面板
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Log.e(TAG, "GlobalVariableHolder.context => " + GlobalVariableHolder.context);
        GlobalVariableHolder.btnTextView = new TextView(GlobalVariableHolder.context);
        ll = new LinearLayout(GlobalVariableHolder.context);

        ViewGroup.LayoutParams txtParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GlobalVariableHolder.btnTextView.setText("FATJS");
        GlobalVariableHolder.btnTextView.setTextSize((float) (text_size + 1));
        GlobalVariableHolder.btnTextView.setGravity(Gravity.CENTER); //文字居中
        GlobalVariableHolder.btnTextView.setTextColor(Color.argb(255,255,255,255));
        GlobalVariableHolder.btnTextView.setLayoutParams(txtParameters);

        // LinearLayout 容器
        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(180,0,0,0));
        ll.setGravity(Gravity.CENTER); //文字居中
        ll.setOrientation(LinearLayout.VERTICAL); //线性布局
        ll.setLayoutParams(llParameters);

        // 将面板设置为圆形
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.argb(180,0,0,0));
        shape.setStroke(6, Color.argb(180,255,255,255)); // 添加边框
        ll.setBackground(shape);

        // 设置面板
        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(parameters);
        parameters.x = (mWidth / 6);
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
                // btnClick(); // 改变悬浮窗大小
                splitCircles(parameters, txtParameters, llParameters);
            }
        });

        moveBtn(parameters);
    }

    private boolean isSmallCirclesVisible = false;
    private LinearLayout smallLL1, smallLL2;

    private void splitCircles(WindowManager.LayoutParams parameters, ViewGroup.LayoutParams txtParameters, LinearLayout.LayoutParams llParameters) {
        if (isSmallCirclesVisible) {
            // 隐藏小圆
            hideSmallCircles();
            return;
        }

        // 创建第一个小圆
        TextView smallCircle1 = new TextView(GlobalVariableHolder.context);
        smallCircle1.setText("停止");
        smallCircle1.setTextSize((float) (text_size));
        smallCircle1.setGravity(Gravity.CENTER);
        smallCircle1.setTextColor(Color.argb(255,255,255,255));
        smallCircle1.setLayoutParams(txtParameters);

        smallLL1 = new LinearLayout(GlobalVariableHolder.context);
        smallLL1.setBackgroundColor(Color.argb(180,0,0,0));
        smallLL1.setGravity(Gravity.CENTER);
        smallLL1.setOrientation(LinearLayout.VERTICAL);
        smallLL1.setLayoutParams(llParameters);

        GradientDrawable smallShape1 = new GradientDrawable();
        smallShape1.setShape(GradientDrawable.OVAL);
        smallShape1.setColor(Color.argb(180,0,0,0));
        smallShape1.setStroke(6, Color.argb(180,255,255,255));
        smallLL1.setBackground(smallShape1);

        WindowManager.LayoutParams smallParams1 = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(smallParams1);
        smallParams1.x = parameters.x + (2 * (btn_w * 4 / 3));
        smallParams1.y = parameters.y;
        smallParams1.gravity = Gravity.RIGHT | Gravity.TOP;
        smallParams1.setTitle("FATJS");

        smallLL1.addView(smallCircle1);
        wm.addView(smallLL1, smallParams1);

        // 添加小圆1的弹出动画效果
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(smallLL1, "translationX", btn_h, 0);
        animator1.setDuration(230);
        animator1.start();

        // 创建第二个小圆
        TextView smallCircle2 = new TextView(GlobalVariableHolder.context);
        if (isStop) {
            smallCircle2.setText("开始");
        } else {
            smallCircle2.setText("暂停");
        }
        smallCircle2.setTextSize((float) (text_size));
        smallCircle2.setGravity(Gravity.CENTER);
        smallCircle2.setTextColor(Color.argb(255,255,255,255));
        smallCircle2.setLayoutParams(txtParameters);

        smallLL2 = new LinearLayout(GlobalVariableHolder.context);
        smallLL2.setBackgroundColor(Color.argb(180,0,0,0));
        smallLL2.setGravity(Gravity.CENTER);
        smallLL2.setOrientation(LinearLayout.VERTICAL);
        smallLL2.setLayoutParams(llParameters);

        GradientDrawable smallShape2 = new GradientDrawable();
        smallShape2.setShape(GradientDrawable.OVAL);
        smallShape2.setColor(Color.argb(180,0,0,0));
        smallShape2.setStroke(6, Color.argb(180,255,255,255));
        smallLL2.setBackground(smallShape2);

        WindowManager.LayoutParams smallParams2 = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(smallParams2);
        smallParams2.x = parameters.x + (btn_w * 4 / 3);
        smallParams2.y = parameters.y;
        smallParams2.gravity = Gravity.RIGHT | Gravity.TOP;
        smallParams2.setTitle("FATJS");

        smallLL2.addView(smallCircle2);
        wm.addView(smallLL2, smallParams2);

        // 添加小圆2的弹出动画效果
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(smallLL2, "translationX", btn_h, 0);
        animator2.setDuration(150);
        animator2.start();

        // 设置小圆的点击事件，用于还原消失
        smallLL1.setOnClickListener((v) -> {
            // 判断是否有任务正在执行
            if (isRunning) {
                killThread = true;
                printLogMsg("有任务正在执行", 0);
                Toast.makeText(context, "有任务正在执行", Toast.LENGTH_SHORT).show();
                return;
            }
            hideSmallCircles();
        });
        smallLL2.setOnClickListener((v) -> {
            if (isRunning) {
                isStop = !isStop;
                if (isStop) {
                    printLogMsg("暂停中...", 0);
                    smallCircle2.setText("开始");
                } else {
                    printLogMsg("开始运行...", 0);
                    smallCircle2.setText("暂停");
                }
            }
            hideSmallCircles();
        });

        isSmallCirclesVisible = true;
    }



    private void hideSmallCircles() {
        if (isSmallCirclesVisible) {
            // 添加小圆的透明度动画效果
            ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(smallLL1, "alpha", 1f, 0f);
            alphaAnimator1.setDuration(100);
            alphaAnimator1.start();

            ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(smallLL2, "alpha", 1f, 0f);
            alphaAnimator2.setDuration(100);
            alphaAnimator2.start();

            // 添加小圆的平移动画效果
            ObjectAnimator translationAnimator1 = ObjectAnimator.ofFloat(smallLL1, "translationX", 0f, smallLL1.getWidth());
            translationAnimator1.setDuration(100);
            translationAnimator1.start();

            ObjectAnimator translationAnimator2 = ObjectAnimator.ofFloat(smallLL2, "translationX", 0f, smallLL2.getWidth());
            translationAnimator2.setDuration(230);
            translationAnimator2.start();

            // 延迟一段时间后移除小圆的视图
            new Handler().postDelayed(() -> {
                wm.removeView(smallLL1);
                wm.removeView(smallLL2);
            }, 100);

            isSmallCirclesVisible = false;
        }
    }

    private void moveBtn(WindowManager.LayoutParams parameters) {
        // 监听触摸，移动
        ll.setOnTouchListener(new View.OnTouchListener() {
            int x, y;
            float touchedX, touchedY;
            private static final int TOUCH_THRESHOLD = 10;
            private final WindowManager.LayoutParams updatedParameters = parameters;
            private boolean isMoving = false; // 是否正在移动
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;
                        touchedX = event.getRawX();
                        touchedY = event.getRawY();
                        isMoving = false; // 重置移动标志位
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - touchedX;
                        float deltaY = event.getRawY() - touchedY;
                        if (Math.abs(deltaX) > TOUCH_THRESHOLD || Math.abs(deltaY) > TOUCH_THRESHOLD) {
                            isMoving = true; // 设置移动标志位为true
                        }
                        int mx = (int) (x - (event.getRawX() - touchedX));
                        updatedParameters.x = mx <= 0 ? 0 : mx;
                        updatedParameters.y = (int) (y + (event.getRawY() - touchedY));
                        wm.updateViewLayout(ll, updatedParameters);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMoving) {
                            v.performClick(); // 触发点击事件
                        }
                        break;
                }
                return true;
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