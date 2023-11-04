package com.linsheng.FATJS.provider;

import android.app.Application;
import android.content.Context;

/**
 * FileName: ContextProvider
 * Describe:给外部调用的单例管理器
 */
public class ContextProvider {
    private static volatile ContextProvider mInstance;
    private Context mContext;

    /**
     * 获取实例
     */
    public static ContextProvider get() {
        if (mInstance == null) {
            synchronized (ContextProvider.class) {
                if (mInstance == null) {
                    if (ApplicationContextProvider.mContext == null) {
                        throw new IllegalStateException("context == null");
                    }
                    mInstance = new ContextProvider();
                }
            }
        }
        return mInstance;
    }

    public void attachContext(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return mContext;
    }

    public Application getApplication() {
        return (Application) mContext.getApplicationContext();
    }
}
