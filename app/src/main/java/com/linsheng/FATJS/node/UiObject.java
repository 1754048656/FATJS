package com.linsheng.FATJS.node;

import static com.linsheng.FATJS.node.AccUtils.AccessibilityHelper;
import static com.linsheng.FATJS.node.AccUtils.findAllText;
import static com.linsheng.FATJS.node.AccUtils.getBounds;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * UiObject 表示一个控件，可以通过这个对象获取到控件的属性，也可以对控件进行点击、长按等操作。
 * 获取一个 UiObject 通常通过选择器的findOne(), findOnce()等函数，
 * 也可以通过 UiCollection 来获取，或者通过UiObject.child(), UiObject.parent()等函数来获取一个控件的子控件或父控件。
 */
public class UiObject implements IUiObject{

    public AccessibilityNodeInfo node;

    public UiObject(AccessibilityNodeInfo node) {
        this.node = node;
    }

    public UiObject() {
    }

    /**
     * 判断node是否为null
     * @return
     */
    public boolean exists() {
        return node != null;
    }

    /**
     * 获取中心坐标
     * @return
     */
    public float[] getPoint() {
        if (node == null) {
            return null;
        }
        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        float left = rect.left;
        float right = rect.right;
        float top = rect.top;
        float bottom = rect.bottom;
        float x = (left + right) / 2;
        float y = (top + bottom) / 2;
        float[] ints = new float[2];
        ints[0] = x;
        ints[1] = y;
        // 回收
        node.recycle();
        return ints;
    }

    /**
     * 返回 {Boolean}
     * 点击该控件，并返回是否点击成功。
     * 如果该函数返回 false，可能是该控件不可点击(clickable 为 false)，当前界面无法响应该点击等。
     * @return
     */
    public boolean click() {
        if (node == null) {
            return false;
        }
        try {
            boolean action1 = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (action1) {
                // 回收
                node.recycle();
                return true;
            }

            while (!node.isClickable()) {
                node = node.getParent();
            }
            if (node.isClickable()) {
                boolean action = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                // 回收
                node.recycle();
                return action;
            }
        }catch (Exception e) {}
        return false;
    }

    /**
     * 手势点击坐标
     * @return
     */
    public boolean clickPoint() {
        if (node == null) {
            return false;
        }

        printLogMsg("DrawingOrder => " + node.getDrawingOrder());

        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        float left = rect.left;
        float right = rect.right;
        float top = rect.top;
        float bottom = rect.bottom;
        float x = (left + right) / 2;
        float y = (top + bottom) / 2;
        boolean aBoolean = clickByPoint(x, y, new Random().nextInt(55) + 60);
        // 回收
        node.recycle();
        return aBoolean;
    }

    /**
     * 手势点击精确坐标，不带偏移，必须指定点击时长
     * @return
     */
    public boolean clickExactPoint(long time) {
        if (node == null) {
            return false;
        }

        printLogMsg("DrawingOrder => " + node.getDrawingOrder());

        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        float left = rect.left;
        float right = rect.right;
        float top = rect.top;
        float bottom = rect.bottom;
        float x = (left + right) / 2;
        float y = (top + bottom) / 2;
        boolean aBoolean = clickByExactPoint(x, y, time);
        // 回收
        node.recycle();
        return aBoolean;
    }

    /**
     * 点击坐标
     * @param x1
     * @param y1
     * @param duration
     * @return
     */
    private boolean clickByPoint(float x1, float y1, long duration) {
        Path path=new Path();
        x1 = x1 + new Random().nextInt(9) - 4;
        y1 = y1 + new Random().nextInt(9) - 4;
        printLogMsg("[x => " + x1 + ", y => " + y1 + "]");
        if (x1 > GlobalVariableHolder.mWidth || y1 > GlobalVariableHolder.mHeight || x1 < 0 || y1 < 0) {
            printLogMsg("Variable.mWidth: " + GlobalVariableHolder.mWidth);
            printLogMsg("Variable.mHeight: " + GlobalVariableHolder.mHeight);
            printLogMsg("超出了点击范围");
            return false;
        }
        path.moveTo(x1, y1);
        GestureDescription.Builder builder=new GestureDescription.Builder();
        GestureDescription gestureDescription=builder
                .addStroke(new GestureDescription.StrokeDescription(path,0,duration))
                .build();

        return AccessibilityHelper.dispatchGesture(gestureDescription,new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription1) {
                super.onCompleted(gestureDescription1);
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription1) {
                super.onCancelled(gestureDescription1);
            }
        },null);
    }

    /**
     * 点击精准坐标
     * @param x1
     * @param y1
     * @param duration
     * @return
     */
    private boolean clickByExactPoint(float x1, float y1, long duration) {
        Path path = new Path();
        printLogMsg("[x => " + x1 + ", y => " + y1 + "]");
        if (x1 > GlobalVariableHolder.mWidth || y1 > GlobalVariableHolder.mHeight || x1 < 0 || y1 < 0) {
            printLogMsg("mWidth: " + GlobalVariableHolder.mWidth);
            printLogMsg("mHeight: " + GlobalVariableHolder.mHeight);
            printLogMsg("超出了点击范围");
            return false;
        }
        path.moveTo(x1, y1);
        GestureDescription.Builder builder=new GestureDescription.Builder();
        GestureDescription gestureDescription=builder
                .addStroke(new GestureDescription.StrokeDescription(path,0,duration))
                .build();

        return AccessibilityHelper.dispatchGesture(gestureDescription,new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription1) {
                super.onCompleted(gestureDescription1);
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription1) {
                super.onCancelled(gestureDescription1);
            }
        },null);
    }

    /**
     * 返回 {Boolean}
     * 长按该控件，并返回是否点击成功。
     * 如果该函数返回 false，可能是该控件不可点击(longClickable 为 false)，当前界面无法响应该点击等。
     * @return
     */
    public boolean longClick() {
        if (node == null) {
            return false;
        }
        return node.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
    }

    /**
     * text {string} 文本
     * 返回 {Boolean}
     * 设置输入框控件的文本内容，并返回是否设置成功。
     * 该函数只对可编辑的输入框(editable 为 true)有效。
     * @param text
     * @return
     */
    public boolean setText(String text) {
        if (node == null) {
            return false;
        }

        // 创建Bundle对象，用于存储需要设置的文本内容
        Bundle args = new Bundle();

        // 将文本内容存入Bundle对象
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);

        // 调用performAction()方法执行设置文本操作
        return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
    }


    /**
     * 返回 {Boolean}
     * 对输入框文本的选中内容进行复制，并返回是否操作成功。
     * 该函数只能用于输入框控件，并且当前输入框控件有选中的文本。可以通过setSelection()函数来设置输入框选中的内容。
     * var et = className("EditText").findOne();
     * //选中前两个字
     * et.setSelection(0, 2);
     * //对选中内容进行复制
     * if(et.copy()){
     *     print("复制成功");
     * }else{
     *     print("复制失败");
     * }
     * @return
     */
    public boolean copy() {
        if (node == null) {
            return false;
        }
        return node.performAction(AccessibilityNodeInfo.ACTION_COPY);
    }

    /**
     * 对输入框文本的选中内容进行剪切，并返回是否操作成功。
     * 该函数只能用于输入框控件，并且当前输入框控件有选中的文本。可以通过setSelection()函数来设置输入框选中的内容。
     * @return
     */
    public boolean cut() {
        if (node == null) {
            return false;
        }
        return node.performAction(AccessibilityNodeInfo.ACTION_CUT);
    }

    /**
     * 返回 {Boolean}
     * 对输入框控件进行粘贴操作，把剪贴板内容粘贴到输入框中，并返回是否操作成功。
     * //设置剪贴板内容为“你好”
     * setClip("你好");
     * var et = className("EditText").findOne();
     * et.paste();
     * @return
     */
    public boolean paste() {
        if (node == null) {
            return false;
        }
        return node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }

    /**
     * start {number} 选中内容起始位置
     * end {number} 选中内容结束位置(不包括)
     * 返回 {Boolean}
     * 对输入框控件设置选中的文字内容，并返回是否操作成功。
     * 索引是从 0 开始计算的；并且，选中内容不包含 end 位置的字符。例如，如果一个输入框内容为"123456789"，要选中"4567"的文字的代码为et.setSelection(3, 7)。
     * 该函数也可以用来设置光标位置，只要参数的 end 等于 start，即可把输入框光标设置在 start 的位置。例如et.setSelection(1, 1)会把光标设置在第一个字符的后面。
     * @param start
     * @param end
     * @return
     */
    public boolean setSelection(int start, int end) {
        if (node == null) {
            return false;
        }

        // 创建Bundle对象，用于存储开始和结束位置的参数值
        Bundle args = new Bundle();

        // 将开始和结束位置的值存入Bundle对象
        args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, start);
        args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, end);

        // 执行选中文本操作
        return node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args);
    }

    /**
     * 返回 {Boolean}
     * 对控件执行向前滑动的操作，并返回是否操作成功。
     * 向前滑动包括了向右和向下滑动。如果一个控件既可以向右滑动和向下滑动，
     * 那么执行scrollForward()的行为是未知的(这是因为 Android 文档没有指出这一点，同时也没有充分的测试可供参考)。
     * @return
     */
    public boolean scrollForward() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 返回 {Boolean}
     * 对控件执行向后滑动的操作，并返回是否操作成功。
     * 向后滑动包括了向右和向下滑动。如果一个控件既可以向右滑动和向下滑动，
     * 那么执行scrollForward()的行为是未知的(这是因为 Android 文档没有指出这一点，同时也没有充分的测试可供参考)。
     * @return
     */
    public boolean scrollBackward() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 返回 {Boolean}
     * 对控件执行"选中"操作，并返回是否操作成功。"选中"和isSelected()的属性相关，但该操作十分少用。
     * @return
     */
    public boolean select() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_SELECT);
    }

    /**
     * 返回 {Boolean}
     * 对控件执行折叠操作，并返回是否操作成功。
     * @return
     */
    public boolean collapse() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_COLLAPSE);
    }

    /**
     * 返回 {Boolean}
     * 对控件执行操作，并返回是否操作成功。
     * @return
     */
    public boolean expand() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_EXPAND);
    }

    /**
     * 对集合中所有控件执行显示操作，并返回是否全部操作成功。
     * @return
     */
    public boolean show() {
        if (node == null) {
            return false;
        }

        return false;
    }

    /**
     * 对集合中所有控件执行向上滑的操作，并返回是否全部操作成功。
     * @return
     */
    public boolean scrollUp() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 对集合中所有控件执行向下滑的操作，并返回是否全部操作成功。
     * @return
     */
    public boolean scrollDown() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 对集合中所有控件执行向左滑的操作，并返回是否全部操作成功。
     * @return
     */
    public boolean scrollLeft() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 对集合中所有控件执行向右滑的操作，并返回是否全部操作成功。
     * @return
     */
    public boolean scrollRight() {
        if (node == null) {
            return false;
        }

        return node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 返回 UiCollection
     * 返回该控件的所有子控件组成的控件集合。可以用于遍历一个控件的子控件，例如：
     * className("AbsListView").findOne().children()
     *     .forEach(function(child){
     *         log(child.className());
     *     });
     * @return
     */
    public UiCollection children() {
        if (node == null) {
            return null;
        }

        UiCollection uiCollection = new UiCollection();
        for (int i = 0; i < node.getChildCount(); i++) {
            uiCollection.nodeList.add(node.getChild(i));
        }
        return uiCollection;
    }


    /**
     * 返回 {number}
     * 返回子控件数目。
     * @return
     */
    public int childCount() {
        if (node == null) {
            return -1;
        }

        return node.getChildCount();
    }


    /**
     * i {number} 子控件索引
     * 返回 {UiObject}
     * 返回第 i+1 个子控件。如果 i>=控件数目或者小于 0，则抛出异常。
     * 需要注意的是，由于布局捕捉的问题，该函数可能返回null，也就是可能获取不到某个子控件。
     * 遍历子控件的示例：
     * var list = className("AbsListView").findOne();
     * for(var i = 0; i < list.childCount(); i++){
     *     var child = list.child(i);
     *     log(child.className());
     * }
     * @param i
     * @return
     */
    public UiObject child(int i) {
        if (node == null) {
            return null;
        }

        UiObject uiObject = new UiObject();
        uiObject.node = node.getChild(i);
        return uiObject;
    }

    /**
     * 返回 {UiObject}
     * 返回该控件的父控件。如果该控件没有父控件，返回null。
     * @return
     */
    public UiObject parent() {
        if (node == null) {
            return null;
        }

        UiObject uiObject = new UiObject();
        uiObject.node = node.getParent();
        return uiObject;
    }


    /**
     * 返回 Rect
     * 返回控件在屏幕上的范围，其值是一个Rect对象。
     * 示例：
     * var b = text("Auto.js").findOne().bounds();
     * toast("控件在屏幕上的范围为" + b);
     * 如果一个控件本身无法通过click()点击，那么我们可以利用bounds()函数获取其坐标，再利用坐标点击。例如：
     * var b = desc("打开侧拉菜单").findOne().bounds();
     * click(b.centerX(), b.centerY());
     * //如果使用root权限，则用 Tap(b.centerX(), b.centerY());
     * @return
     */
    public Rect bounds() {
        if (node == null) {
            return null;
        }

        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        return rect;
    }

    /**
     * 返回 Rect
     * 返回控件在父控件中的范围，其值是一个Rect对象。
     * @return
     */
    public Rect boundsInParent() {
        if (node == null) {
            return null;
        }

        Rect rect = new Rect();
        node.getBoundsInParent(rect);
        return rect;
    }

    /**
     * 返回 {number}
     * 返回控件在父控件中的绘制次序。该函数在安卓 7.0 及以上才有效，7.0 以下版本调用会返回 0。
     * @return
     */
    public int drawingOrder() {
        if (node == null) {
            return -1;
        }

        return node.getDrawingOrder();
    }

    /**
     * 返回 {string}
     * 获取控件的 id，如果一个控件没有 id，则返回null。
     * @return
     */
    public String id() {
        if (node == null) {
            return null;
        }

        return node.getViewIdResourceName();
    }

    /**
     * 返回 {string}
     * 获取控件的文本，如果控件没有文本，返回""。
     * @return
     */
    public String text() {
        if (node == null) {
            return null;
        }

        return String.valueOf(node.getText());
    }

    /**
     * 返回 {string}
     * 获取控件的desc，如果控件没有desc，返回""。
     * @return
     */
    public String desc() {
        if (node == null) {
            return null;
        }

        return String.valueOf(node.getContentDescription());
    }

    /**
     * 返回 {string}
     * 获取控件的className，如果控件没有className，返回""。
     * @return
     */
    public String className() {
        if (node == null) {
            return null;
        }

        return String.valueOf(node.getClassName());
    }

    /**
     * 打印 node 信息
     */
    public void printInfo() {
        if (node == null) {
            return;
        }

        StringBuilder builder = new StringBuilder("--------\n");

        builder.append("package: ").append(node.getPackageName()).append("\n");
        builder.append("clickable: ").append(node.isClickable()).append("\n");
        builder.append("scrollable: ").append(node.isScrollable()).append("\n");
        builder.append("childCount: ").append(node.getChildCount()).append("\n");
        // index (getDrawingOrder) 和 id (getViewIdResourceName) 可能需要根据实际需求来判断是否添加
        builder.append("index: ").append(node.getDrawingOrder()).append("\n");

        String idResourceName = node.getViewIdResourceName();
        if (StringUtils.isNotEmpty(idResourceName))
            builder.append("id: ").append(idResourceName).append("\n");

        CharSequence text = node.getText();
        if (StringUtils.isNotEmpty(text))
            builder.append("text: ").append(text).append("\n");

        CharSequence desc = node.getContentDescription();
        if (StringUtils.isNotEmpty(desc))
            builder.append("desc: ").append(desc).append("\n");

        builder.append("class: ").append(node.getClassName()).append("\n");

        Rect bounds = getBounds(node);
        String rectString = "[" + bounds.left + "," + bounds.top + "]" + "[" + bounds.right + "," + bounds.bottom + "]";
        builder.append("bounds: ").append(rectString);

        printLogMsg(builder.toString());
    }

    /**
     * 打印节点下所有文本
     */
    public void printText() {
        if (node == null) {
            return;
        }
        List<String> allText = findAllText(node);
        if (allText != null && !allText.isEmpty()) {
            StringBuilder text = new StringBuilder();
            text.append("------\n");
            for (String item : allText) {
                text.append(item).append("\n");
            }
            String trim = text.toString().trim();
            printLogMsg(trim);
        }
    }

    /**
     * str {string} 文本
     * 返回 UiCollection
     * 根据文本 text 在子控件中递归地寻找并返回文本或描述(desc)包含这段文本 str 的控件，返回它们组成的集合。
     * 该函数会在当前控件的子控件，孙控件，曾孙控件...中搜索 text 或 desc 包含 str 的控件，并返回它们组合的集合。
     * @param str
     * @return
     */
    public UiCollection findByText(String str) {
        // 检查accNodeInfo是否为空
        if (node == null) {
            return null;
        }

        // 创建存储匹配元素的列表
        List<AccessibilityNodeInfo> nodeInfoOutList = new ArrayList<>();

        // 递归查找匹配的元素
        recursionByText(node, str, nodeInfoOutList);

        // 封装匹配元素列表到UiCollection对象中并返回
        UiCollection uiCollection = new UiCollection();
        uiCollection.nodeList = nodeInfoOutList;
        return uiCollection;
    }
    /**
     * 递归查找文本内容匹配的AccessibilityNodeInfo元素
     * @param accNodeInfo 要查找的AccessibilityNodeInfo节点
     * @param str 要匹配的文本内容
     * @param nodeInfoOutList 存储匹配元素的列表
     */
    private static void recursionByText(AccessibilityNodeInfo accNodeInfo, String str, List<AccessibilityNodeInfo> nodeInfoOutList) {
        // 获取当前节点的文本内容
        String text = String.valueOf(accNodeInfo.getText());
        String desc = String.valueOf(accNodeInfo.getContentDescription());

        // 检查是否匹配，若匹配则添加到匹配元素列表中
        if (text.contains(str) || desc.contains(str)) {
            nodeInfoOutList.add(accNodeInfo);
        }

        // 递归查找子节点
        for (int i = 0; i < accNodeInfo.getChildCount(); i++) {
            recursionByText(accNodeInfo.getChild(i), str, nodeInfoOutList);
        }
    }

    /**
     * selector UiSelector
     * 返回 UiOobject
     * 根据选择器 selector 在该控件的子控件、孙控件...中搜索符合该选择器条件的控件，并返回找到的第一个控件；如果没有找到符合条件的控件则返回null。
     * 例如，对于酷安动态列表，我们可以遍历他的子控件(每个动态列表项)，并在每个子控件中依次寻找点赞数量和图标，对于点赞数量小于 10 的点赞：
     * //找出动态列表
     * var list = id("recycler_view").findOne();
     * //遍历动态
     * list.children().forEach(function(child){
     *     //找出点赞图标
     *     var like = child.findOne(id("feed_action_view_like"));
     *     //找出点赞数量
     *     var likeCount = child.findOne(id("text_view"));
     *     //如果这两个控件没有找到就不继续了
     *     if(like == null || likeCount == null){
     *         return;
     *     }
     *     //判断点赞数量是否小于10
     *     if(parseInt(likeCount.text()) < 10){
     *         //点赞
     *         like.click();
     *     }
     * });
     * @param selector
     * @return
     */
    public UiObject findOne(UiSelector selector) {

        return null;
    }

    /**
     * selector UiSelector
     * 返回 UiCollection
     * 根据选择器 selector 在该控件的子控件、孙控件...中搜索符合该选择器条件的控件，并返回它们组合的集合。
     * @param selector
     * @return
     */
    private UiCollection find(UiSelector selector) {

        return null;
    }

}
