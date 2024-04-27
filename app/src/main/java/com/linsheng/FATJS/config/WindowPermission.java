package com.linsheng.FATJS.config;

import android.app.Activity;
import android.provider.Settings;

public class WindowPermission {
    public static boolean checkPermission(Activity activity){
        return Settings.canDrawOverlays(activity);
    }
}