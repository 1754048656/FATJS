package com.linsheng.FATJS.node;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccUtils extends AccessibilityService {
    @SuppressLint("StaticFieldLeak")
    public static AccessibilityService AccessibilityHelper;
    private static final String TAG = tag;

    public AccUtils() {
        AccessibilityHelper = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {}

    // 返回的节点
    private volatile static AccessibilityNodeInfo nodeInfoOut;
    private volatile static List<AccessibilityNodeInfo> nodeInfoOutList;
    private volatile static List<String> textList;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     ************************************************工具方法*********************************************
     */
    // 从字符串中提取第一个整数
    public static int extractFirstIntFromString(String str){
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);

        int i = -1;
        if (m.find()){
            i = Integer.parseInt(m.group());
            AccUtils.printLogMsg("提取到的第一个整数是：" + i);
        } else {
            AccUtils.printLogMsg("在字符串中没有找到整数！");
        }
        return i;
    }

    // 返回到桌面
    public static void backToDesktop() {
        for (int i = 0; i < 5; i++) {
            back();
            timeSleep(waitOneSecond);
        }
    }

    // 刷视频
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void viewVideo(int num) {
        UiSelector uiSelector = new UiSelector();
        printLogMsg("刷" + num + "个视频，点掉弹窗");
        for (int i = 0; i < num; i++) {
            uiSelector.text("我知道了").findOne().click();
            timeSleep(waitOneSecond);
            uiSelector.text("关闭").findOne().click();
            timeSleep(waitOneSecond);
            uiSelector.text("以后再说").findOne().click();
            timeSleep(waitOneSecond);
            swipe((int)(mWidth / 2) , mHeight - 480, (int)(mWidth / 2) + 80, 200, 450);
            timeSleep(waitFiveSecond + new Random().nextInt(waitFiveSecond));
        }
    }
    /**
     ************************************************工具方法*********************************************
     */

    // 移动悬浮窗
    public static void moveFloatWindow(String val) {
        try {
            AccUtils.printLogMsg("移动悬浮窗 => " + val);
            Intent intent = new Intent();
            intent.setAction("com.msg");
            switch (val) {
                case "打开":
                    intent.putExtra("msg", "show_max");
                    // 更新按钮
                    btnTextView.setText("全屏");
                    break;
                case "隐藏":
                    intent.putExtra("msg", "hide_mini");
                    // 更新按钮
                    btnTextView.setText("打开");
                    break;
                case "全屏":
                    intent.putExtra("msg", "full_screen");
                    // 更新按钮
                    btnTextView.setText("隐藏");
                    break;
            }
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 日志打印
    public static void printLogMsg(String msg) {
        Intent intent = new Intent();
        intent.setAction("com.msg");
        intent.putExtra("msg", msg);
        context.sendBroadcast(intent);
    }

    /**
     * *******************************************自带方法封装**************************************************
     */

    /**
     * getRootInActiveWindow
     * @return
     */
    public static AccessibilityNodeInfo getRootInActiveMy() {
        for (int i = 0; i < 5; i++) {
            AccessibilityNodeInfo root = AccessibilityHelper.getRootInActiveWindow();
            if (root != null) {
                return root;
            }
            timeSleep(500);
        }
        printLogMsg( "getRootInActiveMy: do not find window");
        throw new StopRunException();
    }

    @SuppressLint({"InvalidWakeLockTag", "NewApi"})
    public static Boolean lockScreenNow() {
        // 立即锁屏
        try {
            return AccessibilityHelper.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean back() {
        try {
            AccessibilityHelper.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean home() {
        try {
            AccessibilityHelper.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取坐标
     * @param nodeInfo
     * @return
     */
    public static Rect bounds(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        // 回收
        return rect;
    }

    static class StopRunException extends RuntimeException {
    }

    public static void waitToAlarmClose() {
        printLogMsg("\n---------\n---------\n---------\n---------\n---------\n---------\n---------\n---------\n");
        printLogMsg("W: " + mWidth + " H: " + mHeight);
        timeSleep(6000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollUp() {
        swipe((int)(mWidth / 2) + new Random().nextInt(100), mHeight - 420 + new Random().nextInt(100),
            (int)(mWidth / 2) + new Random().nextInt(200), 310 - new Random().nextInt(100),
                350 + new Random().nextInt(100));
    }

    /**
     * 手势滑动
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param duration
     */
    @RequiresApi(24)
    public static Boolean swipe(float x1,float y1,float x2,float y2,long duration) {
        try {
            Path path=new Path();
            path.moveTo(x1 + new Random().nextInt(10) - 5,y1 + new Random().nextInt(10) - 5);
            path.lineTo(x2 + new Random().nextInt(10) - 5,y2 + new Random().nextInt(10) - 5);
            GestureDescription.Builder builder=new GestureDescription.Builder();
            GestureDescription gestureDescription=builder
                    .addStroke(new GestureDescription.StrokeDescription(path,0,duration))
                    .build();
            return AccessibilityHelper.dispatchGesture(gestureDescription,new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription1) {
                    super.onCompleted(gestureDescription1);
                    Log.e(TAG,"滑动结束..."+ gestureDescription1.getStrokeCount());
                }
                @Override
                public void onCancelled(GestureDescription gestureDescription1) {
                    super.onCancelled(gestureDescription1);
                    Log.e(TAG,"滑动取消");
                }
            },null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean click(int x, int y) {
        return clickPoint(x, y, 75 + new Random().nextInt(50));
    }

    /**
     * 点击坐标
     * @param x1
     * @param y1
     * @param duration
     */
    @RequiresApi(24)
    public static boolean clickPoint(float x1, float y1, long duration) {
        Path path=new Path();
        x1 = x1 + new Random().nextInt(9) - 4;
        y1 = y1 + new Random().nextInt(9) - 4;
        printLogMsg("[x => " + x1 + ", y => " + y1 + "]");
        if (x1 > mWidth || y1 > mHeight || x1 < 0 || y1 < 0) {   // 2220是荣耀20i下面的导航栏按钮
            printLogMsg("mWidth: " + mWidth);
            printLogMsg("mHeight: " + mHeight);
            printLogMsg("超出了点击范围");
            return false;
        }
        path.moveTo(x1, y1);
        GestureDescription.Builder builder=new GestureDescription.Builder();
        GestureDescription gestureDescription=builder
                .addStroke(new GestureDescription.StrokeDescription(path,0,duration))
                .build();

        return AccessibilityHelper.dispatchGesture(gestureDescription,new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription1) {
                super.onCompleted(gestureDescription1);
                Log.e(TAG,"点击结束..."+ gestureDescription1.getStrokeCount());
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription1) {
                super.onCancelled(gestureDescription1);
                Log.e(TAG,"点击取消");
            }
        },null);
    }

    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */
    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇以下方法将逐渐被弃用⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */


    static class StopMsgException extends RuntimeException {
    }

    /**
     * 获取当前Activity
     */
    public static String currentActivityName() {
        return currentActivityName;
    }

    /**
     * 刷新当前 Activity()
     * @param accessibilityEvent
     */
    protected void refreshCurrentActivity(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo source = accessibilityEvent.getSource();

        if (source != null) {
            CharSequence packageName = source.getPackageName();
            if (accessibilityEvent.getEventType() == accessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

                //获取当前窗口activity名
                ComponentName componentName = new ComponentName(
                        accessibilityEvent.getPackageName().toString(),
                        accessibilityEvent.getClassName().toString()
                );

                try {
                    String activityName = getPackageManager().getActivityInfo(componentName, 0).toString();
                    activityName = activityName.substring(activityName.indexOf(" "), activityName.indexOf("}"));
                    currentActivityName = activityName.trim();
                    //Log.i(TAG, " => " + currentActivityName);
                } catch (PackageManager.NameNotFoundException e) {}
            }
        }
    }

    /**
     * 返回一个文字列表，包含root下所有文字内容
     * @param root
     * @return
     */
    public static List<String> findAllText(AccessibilityNodeInfo root) {
        try {
            textList = new ArrayList<>();
            recursionFindAllText(root);
            return textList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<String> findAllText() {
        try {
            textList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindAllText(root);
            return textList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindAllText(AccessibilityNodeInfo root) {
        String text_tmp = String.valueOf(root.getText());
        if (text_tmp != null && !"".equals(text_tmp) && !"null".equals(text_tmp)) {
            textList.add("text\t" + text_tmp);
        }
        String desc_tmp = String.valueOf(root.getContentDescription());
        if (desc_tmp != null && !"".equals(desc_tmp) && !"null".equals(desc_tmp)) {
            textList.add("desc\t" + desc_tmp);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindAllText(root.getChild(i));
        }
    }

    /**
     * 返回一个文字列表，包含root下所有id内容
     * @param root
     * @return
     */
    public static List<String> findAllIds(AccessibilityNodeInfo root) {
        try {
            textList = new ArrayList<>();
            recursionFindAllIds(root);
            return textList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<String> findAllIds() {
        try {
            textList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindAllIds(root);
            return textList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindAllIds(AccessibilityNodeInfo root) {
        String id_tmp = String.valueOf(root.getViewIdResourceName());
        if (id_tmp != null && !"".equals(id_tmp) && !"null".equals(id_tmp)) {
            textList.add(id_tmp);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindAllIds(root.getChild(i));
        }
    }

    /**
     * 返回一个文字列表，包含root下所有className内容
     * @param root
     * @return
     */
    public static List<String> findAllClassName(AccessibilityNodeInfo root) {
        try {
            textList = new ArrayList<>();
            recursionFindAllClassName(root);
            return textList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<String> findAllClassName() {
        try {
            textList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindAllClassName(root);
            return textList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindAllClassName(AccessibilityNodeInfo root) {
        String classname_tmp = String.valueOf(root.getClassName());
        if (classname_tmp != null && !"".equals(classname_tmp) && !"null".equals(classname_tmp)) {
            textList.add(classname_tmp);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindAllClassName(root.getChild(i));
        }
    }

    /**
     * 根据text 查找元素 原生方法
     * @param str_param
     * @return
     */
    public static AccessibilityNodeInfo findAccessibilityNodesByText(String str_param) {
        AccessibilityNodeInfo root = getRootInActiveMy();
        List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText(str_param);
        if (nodeInfoList.size() > 0) {
            return nodeInfoList.get(0);
        }
        return null;
    }

    /**
     * 看看text为xxx的第一个元素并且可点击
     * @param root
     * @param str_param
     * @return
     */
    public static AccessibilityNodeInfo findElementTextCanClick(AccessibilityNodeInfo root, String str_param) {
        try {
            recursionFindElementTextCanClick(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    public static AccessibilityNodeInfo findElementTextCanClick( String str_param) {
        try {
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementTextCanClick(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    // 递归找元素 包含某字符串
    private static void recursionFindElementTextCanClick(AccessibilityNodeInfo root, String str_param) {
        CharSequence rootText = root.getText();
        if (rootText != null) {
            String textStr = String.valueOf(rootText);
            if (textStr.equals(str_param) && root.isClickable()) {
                nodeInfoOut = root;
                throw new StopMsgException();
            }
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementTextCanClick(root.getChild(i), str_param);
        }
    }

    /**
     * 根据resourceId 查找元素
     * @param root
     * @param str_param
     * @return
     */
    public static AccessibilityNodeInfo findElementById(AccessibilityNodeInfo root, String str_param) {
        try {
            recursionFindElementById(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    public static AccessibilityNodeInfo findElementById(String str_param) {
        try {
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementById(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindElementById(AccessibilityNodeInfo root, String str_param) {
        String viewIdResourceName = root.getViewIdResourceName();
        if (str_param.equals(viewIdResourceName)) {
            nodeInfoOut = root;
            throw new StopMsgException();
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementById(root.getChild(i), str_param);
        }
    }

    /**
     * 根据resourceId 查找所有符合的元素  返回list node都是包含关系
     * @param root
     * @param str_param
     * @return
     */
    public static List<AccessibilityNodeInfo> findElementListByContainId(AccessibilityNodeInfo root, String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            recursionFindElementListByContainId(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<AccessibilityNodeInfo> findElementListByContainId(String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementListByContainId(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindElementListByContainId(AccessibilityNodeInfo root, String str_param) {
        String viewIdResourceName = root.getViewIdResourceName();
        if (viewIdResourceName != null && viewIdResourceName.contains(str_param)) {
            nodeInfoOutList.add(root);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementListByContainId(root.getChild(i), str_param);
        }
    }

    /**
     * 根据text 查找元素
     * @param root
     * @param str_param
     * @return
     */
    public static AccessibilityNodeInfo findElementByText(AccessibilityNodeInfo root, String str_param) {
        try {
            recursionFindElementByText(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    public static AccessibilityNodeInfo findElementByText(String str_param) {
        try {
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementByText(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindElementByText(AccessibilityNodeInfo root, String str_param) {
        CharSequence rootText = root.getText();
        if (rootText != null) {
            String textStr = String.valueOf(rootText);
            if (str_param.equals(textStr)) {
                nodeInfoOut = root;
                throw new StopMsgException();
            }
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementByText(root.getChild(i), str_param);
        }
    }

    /**
     * 根据className 查找所有符合的元素  返回list node都是包含关系
     * @param root
     * @param str_param
     * @return
     */
    public static List<AccessibilityNodeInfo> findElementListByContainClassName(AccessibilityNodeInfo root, String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            recursionFindElementListByContainClassName(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<AccessibilityNodeInfo> findElementListByContainClassName(String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementListByContainClassName(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindElementListByContainClassName(AccessibilityNodeInfo root, String str_param) {
        String rootClassName = String.valueOf(root.getClassName());
        if (!"".equals(rootClassName) && rootClassName.contains(str_param)) {
            nodeInfoOutList.add(root);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementListByContainClassName(root.getChild(i), str_param);
        }
    }

    /**
     * 查看等于Description的第一个元素
     * @param root
     * @param str_param
     * @return
     */
    public static List<AccessibilityNodeInfo> findElementListDescription(AccessibilityNodeInfo root, String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            recursionFindElementListDescription(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<AccessibilityNodeInfo> findElementListDescription(String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementListDescription(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素 包含某字符串
    private static void recursionFindElementListDescription(AccessibilityNodeInfo root, String str_param) {
        CharSequence rootText = root.getContentDescription();
        if (rootText != null) {
            String textStr = String.valueOf(rootText);
            if (str_param.equals(textStr)) {
                nodeInfoOutList.add(root);
            }
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementListDescription(root.getChild(i), str_param);
        }
    }

    /**
     * 查看等于Description的第一个元素
     * @param root
     * @param str_param
     * @return
     */
    public static AccessibilityNodeInfo findElementByDescription(AccessibilityNodeInfo root, String str_param) {
        try {
            recursionFindElementByDescription(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    public static AccessibilityNodeInfo findElementByDescription(String str_param) {
        try {
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementByDescription(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    // 递归找元素 包含某字符串
    private static void recursionFindElementByDescription(AccessibilityNodeInfo root, String str_param) {
        String textStr = String.valueOf(root.getContentDescription());
        if (str_param.equals(textStr)) {
            nodeInfoOut = root;
            throw new StopMsgException();
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementByDescription(root.getChild(i), str_param);
        }
    }

    /**
     * 查看包含Description的第一个元素
     * @param root
     * @param str_param
     * @return
     */
    public static AccessibilityNodeInfo findElementByContainDescription(AccessibilityNodeInfo root, String str_param) {
        try {
            recursionFindElementByContainDescription(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    public static AccessibilityNodeInfo findElementByContainDescription(String str_param) {
        try {
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementByContainDescription(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    // 递归找元素 包含某字符串
    private static void recursionFindElementByContainDescription(AccessibilityNodeInfo root, String str_param) {
        if (root != null) {
            CharSequence rootText = root.getContentDescription();
            if (rootText != null) {
                String textStr = String.valueOf(rootText);
                if (textStr.contains(str_param)) {
                    nodeInfoOut = root;
                    throw new StopMsgException();
                }
            }
            for (int i = 0; i < root.getChildCount(); i++) {
                recursionFindElementByContainDescription(root.getChild(i), str_param);
            }
        }
    }

    /**
     * 根据Desc 查找所有符合的元素  返回list node都是包含关系
     * @param root
     * @param str_param
     * @return
     */
    public static List<AccessibilityNodeInfo> findElementListByContainDescription(AccessibilityNodeInfo root, String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            recursionFindElementListByContainDescription(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<AccessibilityNodeInfo> findElementListByContainDescription(String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementListByContainDescription(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindElementListByContainDescription(AccessibilityNodeInfo root, String str_param) {
        String text = String.valueOf(root.getContentDescription());
        if (text.contains(str_param)) {
            nodeInfoOutList.add(root);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementListByContainDescription(root.getChild(i), str_param);
        }
    }

    /**
     * 看看包含text的第一个元素
     * @param root
     * @param str_param
     * @return
     */
    public static AccessibilityNodeInfo findElementByContainText(AccessibilityNodeInfo root, String str_param) {
        try {
            recursionFindElementByContainText(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    public static AccessibilityNodeInfo findElementByContainText(String str_param) {
        try {
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementByContainText(root, str_param);
        }catch (StopMsgException e) {
            return nodeInfoOut;
        }
        return null;
    }
    // 递归找元素 包含某字符串
    private static void recursionFindElementByContainText(AccessibilityNodeInfo root, String str_param) {
        String textStr = String.valueOf(root.getText());
        if (textStr.contains(str_param)) {
            nodeInfoOut = root;
            throw new StopMsgException();
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementByContainText(root.getChild(i), str_param);
        }
    }

    /**
     * 根据text 查找所有符合的元素  返回list node都是包含关系
     * @param root
     * @param str_param
     * @return
     */
    public static List<AccessibilityNodeInfo> findElementListByContainText(AccessibilityNodeInfo root, String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            recursionFindElementListByContainText(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<AccessibilityNodeInfo> findElementListByContainText(String str_param) {
        try {
            nodeInfoOutList = new ArrayList<>();
            AccessibilityNodeInfo root = getRootInActiveMy();
            recursionFindElementListByContainText(root, str_param);
            return nodeInfoOutList;
        }catch (StopMsgException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 递归找元素
    private static void recursionFindElementListByContainText(AccessibilityNodeInfo root, String str_param) {
        String text = String.valueOf(root.getText());
        if (text.contains(str_param)) {
            nodeInfoOutList.add(root);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            recursionFindElementListByContainText(root.getChild(i), str_param);
        }
    }

    /**
     * *******************************************查找元素**************************************************
     */


    @Override
    public void onInterrupt() {
    }


    /**
     * *******************************************基本操作**************************************************
     */
    /**
     * 输入文本 指定元素上输入文本
     * @param str_param
     * @return
     */
    public static Boolean inputTextByNode(AccessibilityNodeInfo nodeInfo ,String str_param) {
        timeSleep(500);
        if (nodeInfo != null) {
            // 搜索对应的名字
            android.os.Bundle rg_ShuJuBao19 = new android.os.Bundle ();
            rg_ShuJuBao19.putString ("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", str_param);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, rg_ShuJuBao19);
            nodeInfo.recycle();
            timeSleep(500);
            return true;
        }
        return false;
    }

    /**
     * 获取坐标
     * @param nodeInfo
     * @return
     */
    public static List<Integer> getPoint(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            int x=(rect.left+rect.right)/2;
            int y = (rect.top + rect.bottom) / 2;
            List<Integer> list = new ArrayList<>();
            list.add(x);
            list.add(y);
            // 回收
            nodeInfo.recycle();
            return list;
        }
        return null;
    }

    /**
     * 根据坐标点击
     * @param nodeInfo
     * @return
     */
    @RequiresApi(24)
    public static Boolean clickNodeByPoint(AccessibilityNodeInfo nodeInfo) {
        try {
            if (nodeInfo != null) {
                Rect rect = new Rect();
                nodeInfo.getBoundsInScreen(rect);
                int x = (rect.left + rect.right) / 2;
                int y = (rect.top + rect.bottom) / 2;
                Boolean aBoolean = clickPoint(x, y, new Random().nextInt(54) + 50);
                // 回收
                nodeInfo.recycle();
                return aBoolean;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据坐标点击加偏移量
     * @param nodeInfo
     * @return
     */
    @RequiresApi(24)
    public static Boolean clickNodeByPointOffset(AccessibilityNodeInfo nodeInfo, int offset_x, int offset_y) {
        try {
            if (nodeInfo != null) {
                Rect rect = new Rect();
                nodeInfo.getBoundsInScreen(rect);
                int x = (rect.left + rect.right) / 2;
                x += offset_x;
                int y = (rect.top + rect.bottom) / 2;
                y += offset_y;
                Boolean aBoolean = clickPoint(x, y, new Random().nextInt(54) + 50);
                // 回收
                nodeInfo.recycle();
                return aBoolean;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 点击父级能点击的节点
     * @param nodeInfo
     * @return
     */
    public static Boolean clickParentCanClick(AccessibilityNodeInfo nodeInfo) {
        try {
            if (nodeInfo != null) {
                boolean action1 = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if (action1) {
                    // 回收
                    nodeInfo.recycle();
                    return true;
                }

                while (!nodeInfo.isClickable()) {
                    nodeInfo = nodeInfo.getParent();
                }
                if (nodeInfo.isClickable()) {
                    boolean action = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    // 回收
                    nodeInfo.recycle();
                    return action;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 双击坐标
     * @param x1
     * @param y1
     * @param duration
     */
    @RequiresApi(24)
    public static boolean doubleClickPoint(float x1,float y1,long duration){
        Path path=new Path();
        path.moveTo(x1,y1);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription=builder
                .addStroke(new GestureDescription.StrokeDescription(path,0,duration))
                .addStroke(new GestureDescription.StrokeDescription(path, new Random().nextInt(60) + 220, duration)).build();
        return AccessibilityHelper.dispatchGesture(gestureDescription,new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e(TAG,"点击结束..."+gestureDescription.getStrokeCount());
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG,"点击取消");
            }
        },null);
    }

    /**
     * 等待
     * @param time
     */
    public static void timeSleep(int time) {
        try {
            Thread.sleep(time + new Random().nextInt(waitOneSecond));
        } catch (InterruptedException e) {
            e.printStackTrace();
            int i = 1/0;
        }
    }

    /**
     * *******************************************基本操作**************************************************
     */

    public static Boolean startApplication(Context ctx, String pkName){
        PackageManager packageManager= ctx.getPackageManager();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pkName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        ResolveInfo ri = apps.iterator().next();
        if (ri != null ) {
            String packageName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            ctx.startActivity(intent);
            return true;
        }
        return false;
    }


    /**
     * 打开应用
     * @param appName
     * @return
     */
    @RequiresApi(24)
    public static boolean openApp(String appName) {
        // 打开应用
        home();
        timeSleep(2 * 1000);
        return new UiSelector().text(appName).findOne().click();
    }

    @RequiresApi(24)
    public static boolean swipeFYS(float x1,float y1,float x2,float y2,long duration){
        Path pathA=new Path();
        Path pathB=new Path();
        pathA.moveTo(x1,y1);
        pathA.lineTo(x2,y2);
        pathB.moveTo(x1,y1);
        pathB.lineTo(x2,y2);
        Log.e(TAG,"MyAccessibilityService 中缩放 zoom()方法滑动点,reset：（ "+x1+","+y1+"),("+x2+","+y2+"),（ "+x1+","+y1+"),("+x2+","+y2+"),"+"时长是:"+duration);
        GestureDescription.Builder builder=new GestureDescription.Builder();
        GestureDescription gestureDescription=builder
                .addStroke(new GestureDescription.StrokeDescription(pathA,0,duration))
                .addStroke(new GestureDescription.StrokeDescription(pathB,50,200))
                .build();
        return AccessibilityHelper.dispatchGesture(gestureDescription,new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e(TAG,"缩放结束..."+gestureDescription.getStrokeCount());
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG,"缩放取消");
            }
        },null);
    }

    @RequiresApi(24)
    public static boolean zoom(float x1,float y1,float x2,float y2, float x3,float y3,float x4,float y4,long duration){
        Path pathA=new Path();
        Path pathB=new Path();
        pathA.moveTo(x1,y1);
        pathA.lineTo(x2,y2);
        pathB.moveTo(x3,y3);
        pathB.lineTo(x4,y4);
        Log.e(TAG,"MyAccessibilityService 中缩放 zoom()方法滑动点,reset：（ "+x1+","+y1+"),("+x2+","+y2+"),（ "+x3+","+y3+"),("+x4+","+y4+"),"+"时长是:"+duration);
        GestureDescription.Builder builder=new GestureDescription.Builder();
        GestureDescription gestureDescription=builder
                .addStroke(new GestureDescription.StrokeDescription(pathA,0,duration))
                .addStroke(new GestureDescription.StrokeDescription(pathB,0,duration))
                .build();
        return AccessibilityHelper.dispatchGesture(gestureDescription,new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e(TAG,"缩放结束..."+gestureDescription.getStrokeCount());
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG,"缩放取消");
            }
        },null);
    }
}
