package com.linsheng.FATJS.script;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.*;

import android.graphics.Rect;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.node.AccUtils;
import com.linsheng.FATJS.node.UiSelector;
import com.linsheng.FATJS.utils.ExceptionUtil;

public class JavaTaskDemo extends UiSelector {

    private static final String TAG = tag;

    public void main() {
        try {
            //run();
        }catch (Exception e) {
            AccUtils.printLogMsg(ExceptionUtil.toString(e)); //打印异常信息到悬浮窗日志上
        }
    }

    private void run() {
        moveFloatWindow("打开");
        printLogMsg("w => " + mWidth + ", h => " + mHeight);
        openApp("xx");
        timeSleep(waitSixSecond * 2);
        printLogMsg("初始化页面");
        for (int i = 0; i < 5; i++) {
            back();
            timeSleep(waitOneSecond + waitHrefOfSecond);
        }
        openApp("xx");
        timeSleep(waitThreeSecond);

        for (int i = 0; i < 3; i++) {
            text("关闭").findOne().click();
            timeSleep(waitOneSecond);
            swipe((int)(mWidth / 2), mHeight - 500, (int)(mWidth / 2), 200, 450);
            timeSleep(waitThreeSecond);
        }

        classNameContains("TextView").desc("xx按钮").findOne().clickPoint();
        timeSleep(waitTwoSecond);
        for (int i = 0; i < 3; i++) {
            back();
            timeSleep(waitOneSecond);
        }
        printLogMsg("完成");
        text("xx").find().filterOne(item -> {
            Rect bounds = getBounds(item);
            return true;
        });
    }
}
