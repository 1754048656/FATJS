package com.linsheng.FATJS.reporter;

import android.content.Context;
import android.util.Log;

import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 *
 * Generated application for tinker life cycle
 *
 */
public class SampleApplication extends TinkerApplication {


    private static final String TAG = GlobalVariableHolder.tag;

    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, SampleApplicationLike.class.getName());
    }

    @Override
    protected void attachBaseContext(Context base) {
        Log.i(TAG, "attachBaseContext: xxx");
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: MyApp --> onCreate Method");
    }

}