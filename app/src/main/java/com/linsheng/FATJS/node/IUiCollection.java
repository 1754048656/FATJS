package com.linsheng.FATJS.node;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public interface IUiCollection {
    public int size();
    public UiObject get(int i);
    public void foreachPrint();
    public UiObject filterOne(UiCollection.FilterCondition<AccessibilityNodeInfo> condition);
    public List<AccessibilityNodeInfo> filter(UiCollection.FilterCondition<AccessibilityNodeInfo> condition);
}
