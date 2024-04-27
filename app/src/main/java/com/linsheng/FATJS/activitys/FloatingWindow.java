package com.linsheng.FATJS.activitys;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.moveFloatWindow;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.linsheng.FATJS.config.GlobalVariableHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FloatingWindow extends Service {
    private static final String TAG = tag;
    private WindowManager wm;
    private  ScrollView sv;
    private int float_window_width = (int)(mWidth / 2.5);
    private int float_window_height = (int)(mHeight / 10.1333);
    private int offset_y = (mHeight / 5);

    // 广播
    DataReceiver dataReceiver = null;
    private static final String ACTIONR = "com.msg";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (dataReceiver == null) {
            dataReceiver = new DataReceiver();
            IntentFilter filter = new IntentFilter();//创建IntentFilter对象
            filter.addAction(ACTIONR);
            registerReceiver(dataReceiver, filter);//注册Broadcast Receiver
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // 广播内部类
    public class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("hide_mini".equals(intent.getStringExtra("msg"))) {
                Log.i(TAG, "onReceive: hide_mini");
                // 隐藏悬浮窗相关
                WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(0, 0, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                setTypePhone(parameters);
                parameters.x = 0;
                parameters.y = 0;
                parameters.gravity = Gravity.RIGHT | Gravity.TOP;
                isOpenFloatWin = false;

                wm.updateViewLayout(sv, parameters);
                windowAutoScroll();
            }else if("show_max".equals(intent.getStringExtra("msg"))) {
                Log.i(TAG, "onReceive: show_max");
                // 展开悬浮窗相关
                WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(float_window_width, float_window_height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                setTypePhone(parameters);
                parameters.x = 20;
                parameters.y = offset_y;
                parameters.gravity = Gravity.LEFT | Gravity.TOP;
                isOpenFloatWin = true;

                wm.updateViewLayout(sv, parameters);
                windowAutoScroll();
            }else if("full_screen".equals(intent.getStringExtra("msg"))) {
                Log.i(TAG, "onReceive: full_screen");
                // 全屏悬浮窗
                WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(mWidth, __mHeight - offset_y - 250, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                setTypePhone(parameters);
                parameters.x = 0;
                parameters.y = offset_y;
                parameters.gravity = Gravity.LEFT | Gravity.TOP;

                wm.updateViewLayout(sv, parameters);
                windowAutoScroll();
            } else if ("screen_log_clear".equals(intent.getStringExtra("msg"))) {
                // 清空悬浮窗日志
                ll.removeAllViews();
                ll.addView(createText("日志面板log"));
            } else {
                // 日志相关 日志打印
                printLog(intent.getStringExtra("msg"));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void setTypePhone(WindowManager.LayoutParams parameters) {
        Log.i(TAG, "onCreate: Build.VERSION.SDK_INT => " + Build.VERSION.SDK_INT);
        parameters.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }
    public TextView createText(String textContent) {
        TextView content_text = new TextView(context);;
        ViewGroup.LayoutParams txtParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        content_text.setText(textContent);
        content_text.setTextSize(text_size);

        // 退出程序处理
        if (textContent.contains("by zero")) {
            String curTime = getStringDate();
            String logTmp = curTime + " | 上个任务已强制结束";
            content_text.setText(logTmp);
            content_text.setLayoutParams(txtParameters);
            content_text.setTextColor(Color.argb(255, 233, 196, 112));
            return content_text;
        }

        if (textContent.contains("暂停中") || textContent.contains("开始运行") || textContent.contains("有任务正在执行")) {
            content_text.setLayoutParams(txtParameters);
            content_text.setTextColor(Color.argb(255, 233, 196, 112));
            return content_text;
        }

        if (textContent.contains("AT: ")) {
            content_text.setLayoutParams(txtParameters);
            content_text.setTextColor(Color.argb(255, 40, 178, 238));
            return content_text;
        }

        if (textContent.contains("Exception")) {
            content_text.setTextColor(Color.argb(255, 233, 196, 112));
        }else {
            content_text.setTextColor(Color.argb(255,255,255,255));
        }

        //content_text.setPadding(5,5,5,5);
        content_text.setLayoutParams(txtParameters);
        return content_text;
    }

    // 打印日志
    public void printLog(String msg) {
        // 日志模板
        String curTime = getStringDate();
        String logTmp = curTime + " | " + msg;
        ll.addView(createText(logTmp));
        Log.i(TAG, "printLog: " + logTmp);

        windowAutoScroll();
    }

    private void windowAutoScroll() {
        // scrollview 自动滚动
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(currentTime);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate FloatingWindow");
        // 定义面板
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        sv = new ScrollView(context);
        ll = new LinearLayout(context);

        // LinearLayout 容器
        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setPadding(5, 5, 5, 5);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(llParameters);

        // ScrollView容器
        ViewGroup.LayoutParams svParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setPadding(5, 5, 5, 5);
        sv.setVerticalScrollBarEnabled(true);
        sv.setLayoutParams(svParams);
        sv.setBackground(getRoundRectShapeWithBorder(15, Color.argb(188,0,0,0), 3, Color.WHITE));

        // 设置面板
//        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(float_window_width, float_window_height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
//        parameters.x = 20;
//        parameters.y = offset_y;
//        parameters.gravity = Gravity.LEFT | Gravity.TOP;

        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(0, 0, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        setTypePhone(parameters);
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.RIGHT | Gravity.TOP;

        parameters.setTitle("FATJS");

        // 添加元素到面板
        ll.addView(createText("日志面板log"));
        sv.addView(ll);
        wm.addView(sv, parameters);
        ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (DEV_MODE)
                    moveFloatWindow("隐藏");
                return true;
            }
        });

        // 监听触摸，移动
//        sv.setOnTouchListener(new View.OnTouchListener() {
//            int x, y;
//            float touchedX, touchedY;
//            private WindowManager.LayoutParams updatedParameters = parameters;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        x = updatedParameters.x;
//                        y = updatedParameters.y;
//
//                        float rawX = event.getRawX();
//                        touchedX = rawX;
//                        touchedY = event.getRawY();
//
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//
//                        updatedParameters.x = (int) (x + (event.getRawX() - touchedX));
//                        updatedParameters.y = (int) (y + (event.getRawY() - touchedY));
//
//                        wm.updateViewLayout(sv, updatedParameters);
//
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
    }
    // 获取圆角矩形形状带边框
    private static Drawable getRoundRectShapeWithBorder(int radius, int backgroundColor, int borderWidth, int borderColor) {
        float[] outerRadii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(backgroundColor);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setStrokeWidth(borderWidth);
        shapeDrawable.getPaint().setPathEffect(new DashPathEffect(new float[]{borderWidth, borderWidth}, 0));
        shapeDrawable.getPaint().setStrokeCap(Paint.Cap.ROUND);
        shapeDrawable.getPaint().setStrokeJoin(Paint.Join.ROUND);
        // shapeDrawable.getPaint().setShadowLayer(radius, 0, 0, borderColor);
        // shapeDrawable.getPaint().setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        return shapeDrawable;
    }

}
