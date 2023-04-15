package com.linsheng.FATJS;

import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.bean.Variable;
import com.linsheng.FATJS.rpa.wechatSendMsg.WechatSendMsgService;
import com.linsheng.FATJS.utils.TimeUtil;

import java.io.File;

public class MyAccessibilityService extends AccUtils {

    private static final String TAG = "FATJS";

    public MyAccessibilityService() {
    }

    WechatSendMsgService wechatSendMsgService = new WechatSendMsgService();

    /**
     * 监听事件的发生
     * @param accessibilityEvent
     */
    @Override
    @RequiresApi(24)
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        // 记录运行日志到文本
        if (runTimeFile == null) {
            runTimeFile = new File(Environment.getExternalStorageDirectory() + "/FATJS_DIR/MonitorEvent.txt");
        }

        // 刷新当前 Activity()
        super.refreshCurrentActivity(accessibilityEvent);

        String formatStr = TimeUtil.getDIYStringDate("HH:mm:ss") + "    => " + accessibilityEvent.getEventType() + "\t\t => "
                + accessibilityEvent.getPackageName() + "\t\t => " + getCurrentActivityName() + "\n";
        Log.e(TAG, formatStr);

        // test
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                Boolean.TRUE.equals(Variable.function_label_map.get("wechat_send_msg_V"))
        ) {
            Log.i(TAG, "onAccessibilityEvent: wechat_send_msg_V start");
            Variable.function_label_map.put("wechat_send_msg_V", false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        wechatSendMsgService.main();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
