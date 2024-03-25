package com.linsheng.FATJS.config;

import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.content.Context;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson2.JSONObject;
import com.caoccao.javet.interop.V8Runtime;
import com.linsheng.FATJS.aione_editor.MainActivity;
import com.linsheng.FATJS.utils.FileUtils;
import com.linsheng.FATJS.utils.StringUtils;

import java.util.HashMap;

public class GlobalVariableHolder {
    public static boolean DEV_MODE = false; // FATJS 的开发者模式
    public static V8Runtime v8Runtime;
    public static final String PATH = "/FATJS_DIR/";
    public static final String FATJS_INFO = "\n\n\n\n\n\n\n\n\n\nauthor: 林笙\n\nwx: FATJS_Lin\n\nGitHub: FATJS";
    public static String ANDROID_ID = "";
    public static String PHONE_NAME = "";

    // 悬浮窗展示的文字
    public static TextView btnTextView = null;
    // 上下文
    public static Context context = null;
    public static MainActivity mainActivity = null;
    // ll
    public static LinearLayout ll = null;
    public static String checkedFileName = ""; // 当前选中的脚本文件名称

    public volatile static HashMap<String, Boolean> broadcast_map = new HashMap<String, Boolean>() {{
        put("jumpUid", false);
        put("msg", false);
    }};

    // 当前ActivityName
    public static volatile String currentActivityName = "";

    // 屏幕宽高
    public static int text_size = 11;
    public static int mWidth = 1440;
    public static int mHeight = 3040;
    public static int __mHeight = -1; //去掉导航栏和状态栏的高度
    public static int statusBarHeight = -1; //状态栏的高度
    public static int navigationBarHeight = -1; //导航栏的高度
    public static boolean navigationBarOpen = true; //导航栏是否开启
    // 悬浮球位置
    public static int float_x = 0;
    public static int float_y = 0;

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
    public static boolean killThread = false; // 是否强制关闭
    public static boolean isOpenFloatWin = false; // 悬浮窗是否打开
    public static void saveConfig() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DEV_MODE", DEV_MODE);
        jsonObject.put("CHECKED_FILE_NAME", checkedFileName);
        String config_string = jsonObject.toString();
        printLogMsg(config_string, 0);
        FileUtils.writeToTxt("/sdcard/FATJS_DIR/config.json", config_string);
    }

    public static void reviewConfig() {
        String readFile = FileUtils.readFile("/sdcard/FATJS_DIR/config.json");
        if (StringUtils.isEmpty(readFile)) {
            printLogMsg("reviewConfig empty", 0);
            saveConfig();
            return;
        }
        JSONObject parseObject = JSONObject.parseObject(readFile);
        DEV_MODE = parseObject.getBoolean("DEV_MODE");
        checkedFileName = parseObject.getString("CHECKED_FILE_NAME");
    }
    public static HashMap<String, Object> hashMapBuffer;
}
