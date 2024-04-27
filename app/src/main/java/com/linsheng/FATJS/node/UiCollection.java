package com.linsheng.FATJS.node;

import static com.linsheng.FATJS.node.AccUtils.printLogMsg;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * UiCollection, 控件集合, 通过选择器的find(), untilFind()方法返回的对象。
 * UiCollection"继承"于数组，实际上是一个 UiObject 的数组，因此可以使用数组的函数和属性，
 * 例如使用 length 属性获取 UiCollection 的大小，使用 forEach 函数来遍历 UiCollection。
 * 例如，采用 forEach 遍历屏幕上所有的文本控件并打印出文本内容的代码为：
 * console.show();
 * className("TextView").find().forEach(function(tv){
 *     if(tv.text() != ""){
 *         log(tv.text());
 *     }
 * });
 * 也可以使用传统的数组遍历方式：
 * console.show();
 * var uc = className("TextView").find();
 * for(var i = 0; i < uc.length; i++){
 *     var tv = uc[i];
 *     if(tv.text() != ""){
 *         log(tv.text());
 *     }
 * }
 * UiCollection 的每一个元素都是 UiObject，我们可以取出他的元素进行操作，例如取出第一个 UiObject 并点击的代码为ui[0].click()。
 * 如果想要对该集合的所有元素进行操作，可以直接在集合上调用相应的函数，例如uc.click()，该代码会对集合上所有 UiObject 执行点击操作并返回是否全部点击成功。
 * 因此，UiCollection 具有所有 UiObject 对控件操作的函数，包括click(), longClick(), scrollForward()等等，不再赘述。
 */
public class UiCollection implements IUiCollection{

    public UiCollection(List<AccessibilityNodeInfo> nodeList) {
        this.nodeList = nodeList;
    }

    public UiCollection() {
    }

    public List<AccessibilityNodeInfo> nodeList = new ArrayList<>();

    /**
     * 返回 nodeList 数组长度
     * @return
     */
    public int size() {
        return nodeList.size();
    }

    /**
     * 获取nodeList第 i 个节点，返回UiObject
     * @param i
     * @return
     */
    public UiObject get(int i) {
        UiObject uiObject = new UiObject();
        if (nodeList.isEmpty()) {
            return uiObject;
        }

        uiObject.node = nodeList.get(i);
        return uiObject;
    }

    /**
     * 遍历打印accNodeInfoList
     */
    public void foreachPrint() {
        this.nodeList.forEach(node -> {
            printLogMsg("foreachPrint: " + node);
        });
    }

    public interface FilterCondition<AccessibilityNodeInfo> {
        boolean shouldKeep(AccessibilityNodeInfo item);
    }

    /**
     * 按条件过滤，需要实现FilterCondition的shouldKeep，可以是匿名，例如：
     * acc.className("RecyclerView").find().filterOne(item -> {
     *     return item.getText().length > 0
     * });
     * @param condition
     * @return UiObject
     */
    public UiObject filterOne(FilterCondition<AccessibilityNodeInfo> condition) {
        List<AccessibilityNodeInfo> filteredList = new ArrayList<>();
        for (AccessibilityNodeInfo item : this.nodeList) {
            if (condition.shouldKeep(item)) {
                //printLogMsg("shouldKeep: " + item);
                filteredList.add(item);
                break;
            }else
                item.recycle(); // 释放过滤掉的
        }
        UiObject uiObject = new UiObject();
        if (!filteredList.isEmpty()) {
            uiObject.node = filteredList.get(0);
        }
        return uiObject;
    }

    /**
     * 按条件过滤，需要实现FilterCondition的shouldKeep，可以是匿名，例如：
     * acc.className("RecyclerView").find().filterOne(item -> {
     *     return item.getText().length > 0
     * });
     * @param condition
     * @return List<AccessibilityNodeInfo>
     */
    public List<AccessibilityNodeInfo> filter(FilterCondition<AccessibilityNodeInfo> condition) {
        List<AccessibilityNodeInfo> filteredList = new ArrayList<>();
        for (AccessibilityNodeInfo item : this.nodeList) {
            if (condition.shouldKeep(item)) {
                //printLogMsg("shouldKeep: " + item);
                filteredList.add(item);
            }else
                item.recycle(); // 释放过滤掉的
        }
        return filteredList;
    }
}
