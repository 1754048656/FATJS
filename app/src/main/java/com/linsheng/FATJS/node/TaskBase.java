package com.linsheng.FATJS.node;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.linsheng.FATJS.aione_editor.MainActivity;
import com.linsheng.FATJS.findColor.ScreenLib;
import com.linsheng.FATJS.utils.ExceptionUtil;
import com.linsheng.FATJS.utils.FileUtils;
import com.linsheng.FATJS.utils.OkHttpUtils;
import com.linsheng.FATJS.utils.ScreenshotUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TaskBase extends UiSelector {
    public TaskBase() {
        _width = mWidth;
        _height = mHeight;
        _statusBarHeight = statusBarHeight;
        _navigationBarHeight = navigationBarHeight;
        _navigationBarOpen = navigationBarOpen;
    }
    private static String base;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initJavet(String script_path) {
        try {
            v8Runtime = V8Host.getV8Instance().createV8Runtime();
            isRunning = true;
            isStop = false;
            killThread = false;
            String script = FileUtils.readFile(script_path);
            if (base == null) {
                base = loadScriptFromAssets("base.js");
            }
            v8Runtime.setConverter(new JavetProxyConverter()); // 配置可调用Java方法
            v8Runtime.getGlobalObject().set("engines", TaskBase.class);
            v8Runtime.getGlobalObject().set("http", OkHttpUtils.class);
            v8Runtime.getGlobalObject().set("UiObject", UiObject.class);
            v8Runtime.getGlobalObject().set("app", App.class);
            v8Runtime.getGlobalObject().set("Intent", Intent.class);
            if (!script.contains("let task = new engines()")) {
                script = base + "\n" + script;
            }
            v8Runtime.getExecutor(script).executeVoid();
        } catch (Exception e) {
            killThread = false;
            printLogMsg(e.toString());
        }finally {
            isRunning = false;
            isStop = false;
            killThread = false;
        }
    }

    /**********************************************************************************************/
    public int _width;
    public int _height;
    public int _statusBarHeight;
    public int _navigationBarHeight;
    public boolean _navigationBarOpen;
    public int[] _findMultiColorInRegionFuzzy(int mainColor, String subColors, double distance, int x1, int y1, int x2, int y2) {
        int[] color = ScreenLib.findColor(mainColor, subColors, distance, x1, y1, x2, y2);
        if (color == null) {
            printLogMsg("color: not found", 0);
        }else {
            printLogMsg("color: " + Arrays.toString(color), 0);
        }
        return color;
    }
    public void _showLog() {
        try {
            moveFloatWindow("打开");
        }catch (Exception e) {}
    }
    public void _hideLog() {
        try {
            moveFloatWindow("隐藏");
        }catch (Exception e) {}
    }
    public void _fullScreenLog() {
        try {
            moveFloatWindow("全屏");
        }catch (Exception e) {}
    }
    public void _clearLog() {}
    public void _clip(String text) {
        clip(text);
    }
    public Context _context() {
        return context;
    }
    public void _startActivity(Intent intent) {
        mainActivity.startActivity(intent);
    }
    public Intent _intent(String jsonText) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonText);
            String data = jsonObject.getString("data");
            Intent intent = new Intent();
            intent.setData(Uri.parse(data));
            return intent;
        }catch (Exception e) {
            printLogMsg(ExceptionUtil.toString(e), 0);
        }
        return null;
    }
    public boolean _lockScreen() {
        return lockScreenNow();
    }
    public String _activityName() {
        timeSleep(waitOneSecond);
        return currentActivityName;
    }
    public float[] _getPoint(AccessibilityNodeInfo nodeInfo) {
        return getPoint(nodeInfo);
    }
    public static boolean execScript(String script) {
        try {
            v8Runtime.getExecutor(script).executeVoid();
            return true;
        } catch (JavetException e) {
            killThread = false;
            printLogMsg(e.toString());
        }
        return false;
    }
    public boolean _capture(String filePath) {
        return ScreenshotUtils.capture(filePath);
    }
    public void _mkdir(String dir) {
        FileUtils.createDirectory(dir);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String _readFile(String filePath) {
        return FileUtils.readFile(filePath);
    }
    public boolean _mvFile(String from, String to) {
        return FileUtils.moveFile(from, to);
    }
    public List<String> _lsFolder(String folderPath) {
        return FileUtils.pathFileList(folderPath);
    }
    public boolean _renameFile(String sourceFilePath, String targetFilePath) {
        return FileUtils.renameFile(sourceFilePath, targetFilePath);
    }
    public boolean _deleteFile(String filePath) {
        return FileUtils.deleteFile(filePath);
    }
    public List<String> _readLines(String filePath) {
        return FileUtils.readLines(filePath);
    }
    public String _getText(AccessibilityNodeInfo node) {
        return String.valueOf(node.getText());
    }
    public String _getDesc(AccessibilityNodeInfo node) {
        return String.valueOf(node.getContentDescription());
    }
    public int[] _screenSize() {
        return new int[]{mWidth, mHeight};
    }
    public void _sleep(int time) {
        timeSleep(time);
    }
    public void _open(String name) {
        openApp(name);
    }
    public void _openPkName(String packageName) {
        startApplication(context, packageName);
    }
    public void _print(String msg) {
        printLogMsg(msg);
    }
    public boolean _clickNode(AccessibilityNodeInfo nodeInfo) {
        return clickParentCanClick(nodeInfo);
    }
    public boolean _clickNodePoint(AccessibilityNodeInfo nodeInfo) {
        return clickNodeByPoint(nodeInfo);
    }
    public boolean _click(float x, float y) {
        return click(x, y);
    }
    public boolean _clickExactPoint(float x, float y, long time) {
        return clickExactPoint(x, y, time);
    }
    public boolean _doubleClick(float x, float y) {
        return doubleClickPoint(x , y, new Random().nextInt(50) + 95);
    }
    public boolean _swipe(float x1, float y1, float x2, float y2, long duration) {
        return swipe(x1, y1, x2, y2, duration);
    }
    public void _scrollUp() {
        scrollUp();
    }
    public Rect _getBounds(AccessibilityNodeInfo nodeInfo) {
        return getBounds(nodeInfo);
    }
    public boolean _back() {
        return back();
    }
    public boolean _home() {
        return home();
    }
    public AccessibilityNodeInfo _getRoot() {
        return getRootInActiveMy();
    }
    public void _move(String str) {
        try {
            moveFloatWindow(str);
        }catch (Exception e) {}
    }
    public void _backToDesk() {
        backToDesktop();
    }
    public void _viewVideo(int num) {
        viewVideo(num);
    }

    /**********************************************************************************************/
    public UiSelector _text(String str){
        return text(str);
    }
    public UiSelector _textContains(String str){
        return textContains(str);
    }
    public UiSelector _textStartsWith(String prefix){
        return textStartsWith(prefix);
    }
    public UiSelector _textEndsWith(String suffix){
        return textEndsWith(suffix);
    }
    public UiSelector _textMatches(String reg){
        return textMatches(reg);
    }
    public UiSelector _desc(String str){
        return desc(str);
    }
    public UiSelector _descContains(String str){
        return descContains(str);
    }
    public UiSelector _descStartsWith(String prefix){
        return descStartsWith(prefix);
    }
    public UiSelector _descEndsWith(String suffix){
        return descEndsWith(suffix);
    }
    public UiSelector _descMatches(String reg){
        return descMatches(reg);
    }
    public UiSelector _id(String resId){
        return id(resId);
    }
    public UiSelector _idContains(String str){
        return idContains(str);
    }
    public UiSelector _idStartsWith(String prefix){
        return idStartsWith(prefix);
    }
    public UiSelector _idEndsWith(String suffix){
        return idEndsWith(suffix);
    }
    public UiSelector _idMatches(String reg){
        return idMatches(reg);
    }
    public UiSelector _className(String str){
        return className(str);
    }
    public UiSelector _classNameContains(String str){
        return classNameContains(str);
    }
    public UiSelector _classNameStartsWith(String prefix){
        return classNameStartsWith(prefix);
    }
    public UiSelector _classNameEndsWith(String suffix){
        return classNameEndsWith(suffix);
    }
    public UiSelector _classNameMatches(String reg){
        return classNameMatches(reg);
    }
    public UiSelector _packageName(String str){
        return packageName(str);
    }
    public UiSelector _packageNameContains(String str){
        return packageNameContains(str);
    }
    public UiSelector _packageNameStartsWith(String prefix){
        return packageNameStartsWith(prefix);
    }
    public UiSelector _packageNameEndsWith(String suffix){
        return packageNameEndsWith(suffix);
    }
    public UiSelector _packageNameMatches(String reg){
        return packageNameMatches(reg);
    }
    public UiSelector _bounds(int left, int top, int right, int bottom){
        return bounds(left, top, right, bottom);
    }
    public UiSelector _boundsInScreen(){
        return boundsInScreen();
    }
    public UiSelector _boundsInside(int left, int top, int right, int bottom){
        return boundsInside(left, top, right, bottom);
    }
    public UiSelector _boundsContains(int left, int top, int right, int bottom){
        return boundsContains(left, top, right, bottom);
    }
    public UiSelector _drawingOrder(int order){
        return drawingOrder(order);
    }
    public UiSelector _clickable(boolean b){
        return clickable(b);
    }
    public UiSelector _longClickable(boolean b){
        return longClickable(b);
    }
    public UiSelector _checkable(boolean b){
        return checkable(b);
    }
    public UiSelector _selected(boolean b){
        return selected(b);
    }
    public UiSelector _enabled(boolean b){
        return enabled(b);
    }
    public UiSelector _scrollable(boolean b){
        return scrollable(b);
    }
    public UiSelector _editable(boolean b){
        return editable(b);
    }
    public UiSelector _multiLine(boolean b){
        return multiLine(b);
    }
    public UiObject _untilFindOne() {
        return untilFindOne();
    }
    public UiObject _untilFindOne(int time) {
        return untilFindOne(time);
    }
    public UiObject _findOne() {
        return findOne();
    }
    public UiObject _findOne(AccessibilityNodeInfo accNodeInfo) {
        return findOne(accNodeInfo);
    }
    public UiObject _findOne(int i) {
        return findOne(i);
    }
    public UiCollection _find() {
        return find();
    }
    public boolean _exists() {
        return exists();
    }
    public void _waitFor() {
        waitFor();
    }
}
