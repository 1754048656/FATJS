package com.linsheng.FATJS.bean;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class Variable {
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
    public static int mWidth;
    public static int mHeight;
}
