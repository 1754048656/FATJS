package com.linsheng.FATJS.activitys;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.isAccessibilityServiceOn;
import static com.linsheng.FATJS.node.AccUtils.moveFloatWindow;
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
import android.provider.Settings;
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

import com.linsheng.FATJS.node.TaskBase;
import com.linsheng.FATJS.utils.ExceptionUtil;
import com.linsheng.FATJS.utils.StringUtils;

public class FloatingButton extends Service {
    private static final String TAG = tag;
    private WindowManager wm;
    private LinearLayout ll;
    ViewGroup.LayoutParams txtParameters;
    ViewGroup.LayoutParams llParameters;
    TextView smallCircle2;
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
        parameters.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
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
        Log.e(TAG, "context => " + context);
        btnTextView = new TextView(context);
        ll = new LinearLayout(context);

        txtParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnTextView.setText("FATJS");
        btnTextView.setTextSize((float) (text_size + 2));
        btnTextView.setGravity(Gravity.CENTER); //文字居中
        btnTextView.setTextColor(Color.argb(255,255,255,255));
        btnTextView.setLayoutParams(txtParameters);

        // LinearLayout 容器
        llParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams();
        parameters.width = btn_w;
        parameters.height = btn_h;

        parameters.format = PixelFormat.TRANSLUCENT;
        parameters.flags =
                //在此模式下，系统会将当前Window区域以外的单击事件传递给底层的Window，
                // 当前Window区域以内的单击事件则自己处理，这个标记很重要，
                // 一般来说都需要开启此标记，否则其他Window将无法收到单击事件
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        //表示Window不需要获取焦点，也不需要接收各种输入事件，最终事件会直接传递给下层的具有焦点的Window
                        //不加入该Flag能响应返回键回调，但是返回键一直被屏蔽，加入后又不能收到监听
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        //加入该Flag，让浮窗层级在软键盘之下，否则如果软键盘覆盖弹窗后，点击软键盘和浮窗重合位置会被浮窗响应
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                        //忽略周围的装饰，例如状态栏。解决切换全屏模式时，位置上移的问题
                        // | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        //允许悬浮窗范围越界到屏幕外
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        //设置该Flag，当触摸事件在悬浮窗以外区域时，发送一个MotionEvent.ACTION_OUTSIDE事件
                        //不会接收到悬浮窗区域以外的move、up事件，只有一次ACTION_OUTSIDE事件
                        //这里设置这个Flag，来关闭悬浮窗
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        //允许窗体浮动在锁屏之上，国产ROM无效
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        parameters.x = (mWidth / 6);
        //private int offset_y = (mHeight / 8);
        parameters.y = 0;
        parameters.gravity = Gravity.RIGHT | Gravity.TOP;
        parameters.setTitle("FATJS");
        setTypePhone(parameters);

        // 添加元素到面板
        ll.addView(btnTextView);
        wm.addView(ll, parameters);

        moveBtn(parameters);
    }

    private boolean isSmallCirclesVisible = false;
    private LinearLayout smallLL1, smallLL2, smallLL3, smallLL4;

    @SuppressLint("RtlHardcoded")
    private void splitCircles(WindowManager.LayoutParams parameters) {
        if (isSmallCirclesVisible) {
            // 隐藏小圆
            hideSmallCircles();
            return;
        }

        // 创建第一个小圆
        TextView smallCircle1 = new TextView(context);
        smallCircle1.setText("停止");
        smallCircle1.setTextSize((float) (text_size + 2));
        smallCircle1.setGravity(Gravity.CENTER);
        smallCircle1.setTextColor(Color.argb(255, 255, 255, 255));
        smallCircle1.setLayoutParams(txtParameters);

        smallLL1 = new LinearLayout(context);
        smallLL1.setBackgroundColor(Color.argb(180, 0, 0, 0));
        smallLL1.setGravity(Gravity.CENTER);
        smallLL1.setOrientation(LinearLayout.VERTICAL);
        smallLL1.setLayoutParams(llParameters);

        GradientDrawable smallShape1 = new GradientDrawable();
        smallShape1.setShape(GradientDrawable.OVAL);
        smallShape1.setColor(Color.argb(180, 0, 0, 0));
        smallShape1.setStroke(6, Color.argb(180, 255, 255, 255));
        smallLL1.setBackground(smallShape1);

        WindowManager.LayoutParams smallParams1 = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(smallParams1);
        smallParams1.x = parameters.x;
        smallParams1.y = parameters.y + (2 * (btn_w * 6 / 5));
        smallParams1.gravity = Gravity.RIGHT | Gravity.TOP;
        smallParams1.setTitle("FATJS");

        smallLL1.addView(smallCircle1);
        wm.addView(smallLL1, smallParams1);

        // 添加小圆1的弹出动画效果
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(smallLL1, "translationY", -btn_h, 0);
        animator1.setDuration(100);
        animator1.start();

        // 创建第二个小圆
        smallCircle2 = new TextView(context);
        if (!isRunning) {
            smallCircle2.setText("开始");
        }else {
            if (isStop) {
                smallCircle2.setText("开始");
            } else {
                smallCircle2.setText("暂停");
            }
        }
        smallCircle2.setTextSize((float) (text_size + 2));
        smallCircle2.setGravity(Gravity.CENTER);
        smallCircle2.setTextColor(Color.argb(255, 255, 255, 255));
        smallCircle2.setLayoutParams(txtParameters);

        smallLL2 = new LinearLayout(context);
        smallLL2.setBackgroundColor(Color.argb(180, 0, 0, 0));
        smallLL2.setGravity(Gravity.CENTER);
        smallLL2.setOrientation(LinearLayout.VERTICAL);
        smallLL2.setLayoutParams(llParameters);

        GradientDrawable smallShape2 = new GradientDrawable();
        smallShape2.setShape(GradientDrawable.OVAL);
        smallShape2.setColor(Color.argb(180, 0, 0, 0));
        smallShape2.setStroke(6, Color.argb(180, 255, 255, 255));
        smallLL2.setBackground(smallShape2);

        WindowManager.LayoutParams smallParams2 = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(smallParams2);
        smallParams2.x = parameters.x;
        smallParams2.y = parameters.y + (btn_w * 6 / 5);
        smallParams2.gravity = Gravity.RIGHT | Gravity.TOP;
        smallParams2.setTitle("FATJS");

        smallLL2.addView(smallCircle2);
        wm.addView(smallLL2, smallParams2);

        // 添加小圆2的弹出动画效果
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(smallLL2, "translationY", -btn_h, 0);
        animator2.setDuration(150);
        animator2.start();

        // 创建第三个小圆
        TextView smallCircle3 = new TextView(context);
        if (isOpenFloatWin) {
            smallCircle3.setText("隐藏");
        }else {
            smallCircle3.setText("打开");
        }
        smallCircle3.setTextSize((float) (text_size + 2));
        smallCircle3.setGravity(Gravity.CENTER);
        smallCircle3.setTextColor(Color.argb(255, 255, 255, 255));
        smallCircle3.setLayoutParams(txtParameters);

        smallLL3 = new LinearLayout(context);
        smallLL3.setBackgroundColor(Color.argb(180, 0, 0, 0));
        smallLL3.setGravity(Gravity.CENTER);
        smallLL3.setOrientation(LinearLayout.VERTICAL);
        smallLL3.setLayoutParams(llParameters);

        GradientDrawable smallShape3 = new GradientDrawable();
        smallShape3.setShape(GradientDrawable.OVAL);
        smallShape3.setColor(Color.argb(180, 0, 0, 0));
        smallShape3.setStroke(6, Color.argb(180, 255, 255, 255));
        smallLL3.setBackground(smallShape3);

        WindowManager.LayoutParams smallParams3 = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(smallParams3);
        smallParams3.x = parameters.x;
        smallParams3.y = parameters.y + (3 * (btn_w * 6 / 5));
        smallParams3.gravity = Gravity.RIGHT | Gravity.TOP;
        smallParams3.setTitle("FATJS");

        smallLL3.addView(smallCircle3);
        wm.addView(smallLL3, smallParams3);

        // 添加小圆3的弹出动画效果
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(smallLL3, "translationY", -btn_h, 0);
        animator3.setDuration(200);
        animator3.start();

        // 创建第四个小圆
        TextView smallCircle4 = new TextView(context);
        smallCircle4.setText("全屏");
        smallCircle4.setTextSize((float) (text_size + 2));
        smallCircle4.setGravity(Gravity.CENTER);
        smallCircle4.setTextColor(Color.argb(255, 255, 255, 255));
        smallCircle4.setLayoutParams(txtParameters);

        smallLL4 = new LinearLayout(context);
        smallLL4.setBackgroundColor(Color.argb(180, 0, 0, 0));
        smallLL4.setGravity(Gravity.CENTER);
        smallLL4.setOrientation(LinearLayout.VERTICAL);
        smallLL4.setLayoutParams(llParameters);

        GradientDrawable smallShape4 = new GradientDrawable();
        smallShape4.setShape(GradientDrawable.OVAL);
        smallShape4.setColor(Color.argb(180, 0, 0, 0));
        smallShape4.setStroke(6, Color.argb(180, 255, 255, 255));
        smallLL4.setBackground(smallShape4);

        WindowManager.LayoutParams smallParams4 = new WindowManager.LayoutParams(btn_w, btn_h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(smallParams4);
        smallParams4.x = parameters.x;
        smallParams4.y = parameters.y + (4 * (btn_w * 6 / 5));
        smallParams4.gravity = Gravity.RIGHT | Gravity.TOP;
        smallParams4.setTitle("FATJS");

        smallLL4.addView(smallCircle4);
        wm.addView(smallLL4, smallParams4);

        // 添加小圆4的弹出动画效果
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(smallLL4, "translationY", -btn_h, 0);
        animator4.setDuration(300);
        animator4.start();

        // 设置小圆的点击事件，用于还原消失
        smallLL1.setOnClickListener((v) -> {
            hideSmallCircles();
            // 判断是否有任务正在执行
            if (isRunning) {
                killThread = true;
                if (isStop) {
                    smallCircle2.setText("暂停");
                    isStop = false;
                }
                printLogMsg("有任务正在执行", 0);
            }
        });
        smallLL2.setOnClickListener((v) -> {
            hideSmallCircles();
            if (isRunning) {
                isStop = !isStop;
                if (isStop) {
                    printLogMsg("暂停中...", 0);
                    smallCircle2.setText("开始");
                } else {
                    printLogMsg("开始运行...", 0);
                    smallCircle2.setText("暂停");
                }
            }else {
                // 开始执行选中的脚本
                testMethodPre();
            }
        });
        smallLL3.setOnClickListener((v) -> {
            hideSmallCircles();
            // TODO: 处理小圆3的点击事件
            if (isOpenFloatWin) {
                moveFloatWindow("隐藏");
                smallCircle3.setText("打开");
            }else {
                moveFloatWindow("打开");
                smallCircle3.setText("隐藏");
            }
        });
        smallLL4.setOnClickListener((v) -> {
            hideSmallCircles();
            // TODO: 处理小圆4的点击事件
            moveFloatWindow("全屏");
        });

        isSmallCirclesVisible = true;
    }

    private void hideSmallCircles() {
        if (isSmallCirclesVisible) {
            // 添加小圆的透明度动画效果
            ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(smallLL1, "alpha", 1f, 0f);
            alphaAnimator1.setDuration(201);
            alphaAnimator1.start();

            ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(smallLL2, "alpha", 1f, 0f);
            alphaAnimator2.setDuration(201);
            alphaAnimator2.start();

            ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(smallLL3, "alpha", 1f, 0f);
            alphaAnimator3.setDuration(201);
            alphaAnimator3.start();

            ObjectAnimator alphaAnimator4 = ObjectAnimator.ofFloat(smallLL4, "alpha", 1f, 0f);
            alphaAnimator4.setDuration(201);
            alphaAnimator4.start();

            // 添加小圆的平移动画效果
            ObjectAnimator translationAnimator1 = ObjectAnimator.ofFloat(smallLL1, "translationY", 0f, -smallLL1.getWidth());
            translationAnimator1.setDuration(201);
            translationAnimator1.start();

            ObjectAnimator translationAnimator2 = ObjectAnimator.ofFloat(smallLL2, "translationY", 0f, -smallLL2.getWidth());
            translationAnimator2.setDuration(201);
            translationAnimator2.start();

            ObjectAnimator translationAnimator3 = ObjectAnimator.ofFloat(smallLL3, "translationY", 0f, -smallLL3.getWidth());
            translationAnimator3.setDuration(201);
            translationAnimator3.start();

            ObjectAnimator translationAnimator4 = ObjectAnimator.ofFloat(smallLL4, "translationY", 0f, -smallLL4.getWidth());
            translationAnimator4.setDuration(201);
            translationAnimator4.start();

            // 延迟一段时间后移除小圆的视图
            new Handler().postDelayed(() -> {
                if (smallLL1.getWindowToken() != null)
                    wm.removeView(smallLL1);
                if (smallLL2.getWindowToken() != null)
                    wm.removeView(smallLL2);
                if (smallLL3.getWindowToken() != null)
                    wm.removeView(smallLL3);
                if (smallLL4.getWindowToken() != null)
                    wm.removeView(smallLL4);
            }, 50);
            isSmallCirclesVisible = false;
        }
    }

    static boolean isAction = false;
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
                        if (isMoving && !isAction) {
                            isAction = true;
                            hideSmallCircles();
                            isAction = false;
                        }
                        int mx = (int) (x - (event.getRawX() - touchedX));
                        int my = (int) (y + (event.getRawY() - touchedY));
                        updatedParameters.x = mx <= 0 ? 0 : (mx + btn_w) >= mWidth ? mWidth - btn_w : mx; // 防止悬浮球移出屏幕 x轴
                        updatedParameters.y = my <= 0 ? 0 : (my + btn_h) >= __mHeight ? __mHeight - btn_h : my; // 防止悬浮球移出屏幕 y轴
                        wm.updateViewLayout(ll, updatedParameters);
                        // 获取悬浮球的位置坐标
                        float_x = mWidth - updatedParameters.x;
                        float_y = updatedParameters.y + statusBarHeight + btn_h/2;
                        // printLogMsg(String.valueOf(float_y), 0);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMoving) {
                            // v.performClick(); // 触发点击事件
                            if (DEV_MODE) {
                                // FATJS 的开发者模式
                                testMethodPre();
                            } else {
                                // btnClick(); // 改变悬浮窗大小
                                splitCircles(parameters);
                            }
                            // VibratorUtil.startVibrator(); // 震动
                        }
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        // 处理ACTION_OUTSIDE事件
                        // 关闭悬浮窗
                        hideSmallCircles();
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


    private void testMethodPre() {
        if (StringUtils.isEmpty(checkedFileName)) {
            printLogMsg("未选中脚本", 0);
            Toast.makeText(context, "未选中脚本", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkedFileName.endsWith(".js")) {
            printLogMsg(checkedFileName + " is not a js file", 0);
            Toast.makeText(context, checkedFileName + " is not a js file", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAccessibilityServiceOn()){
            printLogMsg("请开启无障碍服务", 0);
            Toast.makeText(context, "请开启无障碍服务", Toast.LENGTH_SHORT).show();
            mainActivity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }
        // 判断是否有任务正在执行
        if (isRunning) {
            killThread = true;
            printLogMsg("有任务正在执行", 0);
            Toast.makeText(context, "有任务正在执行", Toast.LENGTH_SHORT).show();
            return;
        }
        printLogMsg("开始运行...", 0);
        if (smallCircle2 != null)
            smallCircle2.setText("暂停");
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
        if ("打开".contentEquals(btnTextView.getText())) {
            Log.i(TAG, "onClick: 打开 --> 全屏");
            if (isRunning) {
                isStop = false;
                printLogMsg("开始运行", 0);
            }
            btnTextView.setText("全屏");

            // 展开悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "show_max");
            context.sendBroadcast(intent);
        }else if("隐藏".contentEquals(btnTextView.getText())){
            Log.i(TAG, "onClick: 隐藏 --> 打开");
            if (isRunning) {
                isStop = true;
                printLogMsg("暂停中", 0);
            }
            btnTextView.setText("打开");

            // 隐藏悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "hide_mini");
            context.sendBroadcast(intent);
        }else if("全屏".contentEquals(btnTextView.getText())) {
            Log.i(TAG, "onClick: 全屏 --> 隐藏");
            if (isRunning) {
                isStop = true;
                printLogMsg("暂停中", 0);
            }
            btnTextView.setText("隐藏");

            // 隐藏悬浮窗
            Intent intent = new Intent();
            intent.setAction("com.msg");
            intent.putExtra("msg", "full_screen");
            context.sendBroadcast(intent);
        }
    }

    /**
     * 测试方法
     */
    private void testMethod() {
        // 将测试的动作写到这里，点击悬浮窗的 打开 按钮，就可以执行
        // Test 1
        TaskBase taskDemo = new TaskBase();
        printLogMsg("run script " + checkedFileName, 0);
        String script_path = ABSOLUTE_PATH + checkedFileName;
        // printLogMsg("script_path => " + script_path, 0);
        taskDemo.initJavet(script_path);
    }
}