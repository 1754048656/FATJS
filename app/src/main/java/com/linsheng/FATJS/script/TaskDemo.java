package com.linsheng.FATJS.script;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.*;

import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.linsheng.FATJS.node.UiSelector;

public class TaskDemo extends UiSelector {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start_run() {
        first();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void first() {
        printLogMsg("搜索视频任务启动");
        printLogMsg("打开抖音");
        openApp("抖音");
        timeSleep(waitSixSecond * 3);
        printLogMsg("初始化页面");
        backToDesktop();
        timeSleep(waitTwoSecond);
        printLogMsg("打开抖音");
        openApp("抖音");
        timeSleep(waitFiveSecond);
        viewVideo(8);
        className("Button").desc("搜索").findOne().click();
        timeSleep(waitTwoSecond);
        text("不感兴趣").findOne().click();
        timeSleep(waitTwoSecond);
        text("关闭").findOne().click();
        timeSleep(waitTwoSecond);
        className("EditText").findOne().setText("抖音自动化引流"); // 自动化获客系统、做抖音、
        timeSleep(waitTwoSecond);
        className("TextView").text("搜索").findOne().clickPoint();
        timeSleep(waitSixSecond * 2);
        className("Button").text("视频").findOne().click();
        timeSleep(waitSixSecond);
        className("ViewGroup").descContains("筛选").findOne().click();
        timeSleep(waitThreeSecond);
        className("TextView").text("最多点赞").findOne().click();
        timeSleep(waitTwoSecond);
        className("RecyclerView").find().foreachPrint();
        printLogMsg("点击第一个视频");
        className("RecyclerView").find().filterOne(item -> {
            Rect rect = getBounds(item);
            return rect.right > 1000;
        }).child(0).click();
        timeSleep(waitFourSecond);
        loopVideo(0, 300);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loopVideo(int index, int target) {
        if (index >= target) {
            return;
        }
        index ++;
        printLogMsg("index => " + index);

        // 这里写循环的里面的业务逻辑
        className("ImageView").descMatches("评论(\\d+)，按钮").boundsInScreen().findOne().click();
        timeSleep(waitFiveSecond);

        click(mWidth / 2 + 180, 390);
        timeSleep(waitOneSecond);
        click(mWidth / 2 + 180, 390);
        timeSleep(waitOneSecond);
        click(mWidth / 2 + 180, 390);
        timeSleep(waitThreeSecond);

        scrollUp();
        timeSleep(waitSixSecond);

        loopVideo(index, target);
    }
}
