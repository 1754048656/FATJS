package com.linsheng.FATJS.config;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caoccao.javet.interop.V8Runtime;

import java.util.HashMap;

public class GlobalVariableHolder {
    public static boolean DEV_MODE = false; // FATJS 的开发者模式
    public static V8Runtime v8Runtime;
    public static final String PATH = "/FATJS_DIR/";
    public static final String FATJS_INFO = "author: 林笙\n\nwx: FATJS_Lin\n\nGitHub: FATJS";
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
    public static int __mHeight = -1; //去掉导航栏和状态栏的高度
    public static int statusBarHeight = -1; //状态栏的高度
    public static int navigationBarHeight = -1; //导航栏的高度

    public static String tag = "FATJS_LOG";
    // 停顿时长
    public static final int waitHrefOfSecond  =   500;
    public static final int waitOneSecond     =   1000;
    public static final int waitTwoSecond     =   2000;
    public static final int waitThreeSecond   =   3000;
    public static final int waitFourSecond    =   4000;
    public static final int waitFiveSecond    =   5000;
    public static final int waitSixSecond     =   6000;
    public static boolean isStop = false; // 暂停标识
    public static boolean isRunning = false; // 是否有任务在运行
    public static boolean killThread = false;
}
