package com.linsheng.FATJS.config;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class GlobalVariableHolder {
    public static String ANDROID_ID = "";
    public static String PHONE_NAME = "";

    // 悬浮窗展示的文字
    public static TextView btnTextView = null;
    // 上下文
    public static Context context = null;
    // ll
    public static LinearLayout ll = null;

    public volatile static HashMap<String, Boolean> broadcast_map = new HashMap<String, Boolean>() {{
        put("jumpUid", false);
        put("msg", false);
    }};

    // 当前ActivityName
    public static volatile String currentActivityName;

    // 屏幕宽高
    public static int text_size = 11;
    public static int mWidth = 1440;
    public static int mHeight = 3040;
    public static int __mHeight; //去掉导航栏和状态栏的高度

    public static String tag = "FATJS_LOG";
    // 停顿时长
    public static final int waitHrefOfSecond  =   500;
    public static final int waitOneSecond     =   1000;
    public static final int waitTwoSecond     =   2000;
    public static final int waitThreeSecond   =   3000;
    public static final int waitFourSecond    =   4000;
    public static final int waitFiveSecond    =   5000;
    public static final int waitSixSecond     =   6000;
}
