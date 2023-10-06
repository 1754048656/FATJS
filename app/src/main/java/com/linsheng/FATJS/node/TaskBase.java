package com.linsheng.FATJS.node;

import static com.linsheng.FATJS.config.GlobalVariableHolder.isRunning;
import static com.linsheng.FATJS.config.GlobalVariableHolder.isStop;
import static com.linsheng.FATJS.config.GlobalVariableHolder.killThread;
import static com.linsheng.FATJS.config.GlobalVariableHolder.mHeight;
import static com.linsheng.FATJS.config.GlobalVariableHolder.mWidth;
import static com.linsheng.FATJS.config.GlobalVariableHolder.v8Runtime;
import static com.linsheng.FATJS.node.AccUtils.back;
import static com.linsheng.FATJS.node.AccUtils.backToDesktop;
import static com.linsheng.FATJS.node.AccUtils.click;
import static com.linsheng.FATJS.node.AccUtils.clickNodeByPoint;
import static com.linsheng.FATJS.node.AccUtils.clickParentCanClick;
import static com.linsheng.FATJS.node.AccUtils.doubleClickPoint;
import static com.linsheng.FATJS.node.AccUtils.getBounds;
import static com.linsheng.FATJS.node.AccUtils.getRootInActiveMy;
import static com.linsheng.FATJS.node.AccUtils.home;
import static com.linsheng.FATJS.node.AccUtils.loadScriptFromAssets;
import static com.linsheng.FATJS.node.AccUtils.moveFloatWindow;
import static com.linsheng.FATJS.node.AccUtils.openApp;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;
import static com.linsheng.FATJS.node.AccUtils.scrollUp;
import static com.linsheng.FATJS.node.AccUtils.swipe;
import static com.linsheng.FATJS.node.AccUtils.timeSleep;
import static com.linsheng.FATJS.node.AccUtils.viewVideo;

import android.graphics.Rect;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.linsheng.FATJS.utils.ExceptionUtil;
import com.linsheng.FATJS.utils.FileUtils;

import java.util.Random;

public class TaskBase extends UiSelector {
    public TaskBase() {
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
            if (!script_path.contains("FATJS_DIR/dev_script.js")) { // 测试方法，不拼接base.js 用作代码提示
                script = base + "\n" + script;
            }
            v8Runtime.setConverter(new JavetProxyConverter()); // 配置可调用Java方法
            v8Runtime.getGlobalObject().set("Task", TaskBase.class);
            v8Runtime.getExecutor(script).executeVoid();
        } catch (Exception e) {
            printLogMsg(ExceptionUtil.toString(e));
        }finally {
            isRunning = false;
            isStop = false;
            killThread = false;
        }
    }

    /**********************************************************************************************/
    public int[] _screenSize() {
        return new int[]{mWidth, mHeight};
    }
    public void _sleep(int time) {
        timeSleep(time);
    }
    public void _open(String name){
        openApp(name);
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
    public boolean _click(int x, int y) {
        return click(x, y);
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
