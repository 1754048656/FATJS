package com.linsheng.FATJS;

import android.os.Environment;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.rpa.dyService.DyTaskService;
import com.linsheng.FATJS.utils.TimeUtil;

import java.io.File;

public class MyAccessibilityService extends AccUtils {

    private static final String TAG = "FATJS";

    public MyAccessibilityService() {
    }

    /**
     * 监听事件的发生
     * @param accessibilityEvent
     */
    @Override
    @RequiresApi(24)
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        // 刷新当前 Activity()
        super.refreshCurrentActivity(accessibilityEvent);
    }
}
