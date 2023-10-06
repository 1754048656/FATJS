package com.linsheng.FATJS.script.dyService;

import static com.linsheng.FATJS.config.GlobalVariableHolder.mHeight;
import static com.linsheng.FATJS.config.GlobalVariableHolder.mWidth;
import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;
import static com.linsheng.FATJS.config.GlobalVariableHolder.waitHrefOfSecond;
import static com.linsheng.FATJS.config.GlobalVariableHolder.waitOneSecond;
import static com.linsheng.FATJS.config.GlobalVariableHolder.waitSixSecond;
import static com.linsheng.FATJS.config.GlobalVariableHolder.waitThreeSecond;
import static com.linsheng.FATJS.config.GlobalVariableHolder.waitTwoSecond;
import static com.linsheng.FATJS.node.AccUtils.back;
import static com.linsheng.FATJS.node.AccUtils.moveFloatWindow;
import static com.linsheng.FATJS.node.AccUtils.openApp;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;
import static com.linsheng.FATJS.node.AccUtils.swipe;
import static com.linsheng.FATJS.node.AccUtils.timeSleep;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.node.AccUtils;
import com.linsheng.FATJS.node.UiSelector;
import com.linsheng.FATJS.utils.ExceptionUtil;

public class DyTaskService extends UiSelector {

    private static final String TAG = tag;

    @RequiresApi(24)
    public void main() {
        try {
            //run();
        }catch (Exception e) {
            AccUtils.printLogMsg(ExceptionUtil.toString(e)); //打印异常信息到悬浮窗日志上
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void run() {
        moveFloatWindow("打开");
        printLogMsg("w => " + mWidth + ", h => " + mHeight);
        printLogMsg("打开抖音");
        openApp("抖音");
        timeSleep(waitSixSecond * 2);
        printLogMsg("初始化页面");
        for (int i = 0; i < 5; i++) {
            back();
            timeSleep(waitOneSecond + waitHrefOfSecond);
        }
        printLogMsg("打开抖音");
        openApp("抖音");
        timeSleep(waitThreeSecond);

        printLogMsg("刷几个视频，点掉弹窗");
        for (int i = 0; i < 3; i++) {
            text("我知道了").findOne().click();
            timeSleep(waitOneSecond);
            text("关闭").findOne().click();
            timeSleep(waitOneSecond);
            text("以后再说").findOne().click();
            timeSleep(waitOneSecond);
            printLogMsg("滑动");
            swipe((int)(mWidth / 2), mHeight - 500, (int)(mWidth / 2), 200, 450);
            timeSleep(waitThreeSecond);
        }

        classNameContains("TextView").desc("我，按钮").findOne().clickPoint();
        timeSleep(waitTwoSecond);
        text("关闭").findOne().click();
        timeSleep(waitTwoSecond);
        text("保持公开").findOne().click();
        timeSleep(waitTwoSecond);
        for (int i = 0; i < 3; i++) {
            back();
            timeSleep(waitOneSecond);
        }
        printLogMsg("完成");
    }
}
