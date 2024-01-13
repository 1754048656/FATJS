package com.linsheng.FATJS.node;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

public interface IUiObject {
    public boolean exists();
    public float[] getPoint();
    public boolean click();
    public boolean clickPoint();
    public boolean clickExactPoint(long time);
    public boolean longClick();
    public boolean setText(String text);
    public boolean copy();
    public boolean cut();
    public boolean paste();
    public boolean setSelection(int start, int end);
    public boolean scrollForward();
    public boolean scrollBackward();
    public boolean select();
    public boolean collapse();
    public boolean expand();
    // public boolean show();
    public boolean scrollUp();
    public boolean scrollDown();
    public boolean scrollLeft();
    public boolean scrollRight();
    public UiCollection children();
    public int childCount();
    public UiObject child(int i);
    public UiObject parent();
    public Rect bounds();
    public Rect boundsInParent();
    public int drawingOrder();
    public String id();
    public String text();
    public String desc();
}
