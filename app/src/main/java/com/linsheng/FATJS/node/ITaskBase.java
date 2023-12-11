package com.linsheng.FATJS.node;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public interface ITaskBase {
    public int[] _findMultiColorInRegionFuzzy(int mainColor, String subColors, double distance, int x1, int y1, int x2, int y2);
    public void _showLog();
    public void _hideLog();
    public void _fullScreenLog();
    public void _clearLog();
    public void _clip(String text);
    public Context _context();
    public void _startActivity(Intent intent);
    public Intent _intent(String jsonText);
    public boolean _lockScreen();
    public String _activityName();
    public float[] _getPoint(AccessibilityNodeInfo nodeInfo);
    public boolean _capture(String filePath);
    public void _mkdir(String dir);
    public String _readFile(String filePath);
    public boolean _mvFile(String from, String to);
    public List<String> _lsFolder(String folderPath);
    public boolean _renameFile(String sourceFilePath, String targetFilePath);
    public boolean _deleteFile(String filePath);
    public List<String> _readLines(String filePath);
    public String _getText(AccessibilityNodeInfo node);
    public String _getDesc(AccessibilityNodeInfo node);
    public int[] _screenSize();
    public void _sleep(int time);
    public void _open(String name);
    public void _openPkName(String packageName);
    public void _print(String msg);
    public boolean _clickNode(AccessibilityNodeInfo nodeInfo);
    public boolean _clickNodePoint(AccessibilityNodeInfo nodeInfo);
    public boolean _click(float x, float y);
    public boolean _clickExactPoint(float x, float y, long time);
    public boolean _doubleClick(float x, float y);
    public boolean _swipe(float x1, float y1, float x2, float y2, long duration);
    public void _scrollUp();
    public Rect _getBounds(AccessibilityNodeInfo nodeInfo);
    public boolean _back();
    public boolean _home();
    public AccessibilityNodeInfo _getRoot();
    public void _backToDesk();
}
