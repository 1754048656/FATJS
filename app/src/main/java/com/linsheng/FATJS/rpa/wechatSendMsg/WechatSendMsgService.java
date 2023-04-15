package com.linsheng.FATJS.rpa.wechatSendMsg;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.AccUtils;
import com.linsheng.FATJS.enums.TaskTypeEnum;
import com.linsheng.FATJS.rpa.TaskFactory.TaskBasic;
import com.linsheng.FATJS.utils.ExceptionUtil;
import com.linsheng.FATJS.utils.ExitException;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信群发
 */
public class WechatSendMsgService extends TaskBasic {

    private static final String TAG = "FATJS";
    public WechatSendMsgService() {
        super(TaskTypeEnum.WECHAT_SEND_MESSAGE.getName());
    }

    @RequiresApi(24)
    public void main() throws Exception {

        try {
            runTask();
        }catch (Exception e) {
            AccUtils.printLogMsg(ExceptionUtil.toString(e));
            e.printStackTrace();
        }
    }

    @RequiresApi(24)
    private void runTask() throws ExitException {
        AccUtils.moveFloatWindow("打开");
        AccUtils.printLogMsg("任务开启 => 读取通讯录列表");
        AccUtils.printLogMsg("open Wechat App");
        AccUtils.openApp("微信");
        AccUtils.timeSleep(waitSixSecond);
        AccUtils.printLogMsg("点击通讯录");
        AccUtils.clickNodeByPoint(AccUtils.findElementByText("通讯录"));
//        AccUtils.timeSleep(waitThreeSecond);
//        AccUtils.printLogMsg("click A by FindColor");
//        int[] x = ScreenLib.findColor( 0x191919, "1|-4|0x191919,4|1|0x191919,6|8|0x191919,2|8|0x191919,-4|9|0x191919,2|5|0xffffff", 90, 1017, 405, 1079, 858);
//        if (x != null) {
//            AccUtils.printLogMsg("color find point => " + x[0] + ", " + x[1]);
//            AccUtils.clickPoint(x[0], x[1], new Random().nextInt(54) + 150);
//        }else {
//            AccUtils.printLogMsg("color not found");
//        }
//        AccUtils.timeSleep(waitOneSecond);
//        AccUtils.printLogMsg("start read address book");
//        for (int i = 0; i < 20; i++) {
//            readAddressBooks();
//        }

    }

    private static List<String> nameItemList = new ArrayList<>();

    private void readAddressBooks() throws ExitException {
        AccessibilityNodeInfo targetClassNameNode = findTargetClassNameNode();
        if (targetClassNameNode != null) {
            int childCount = targetClassNameNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo nodeChild = targetClassNameNode.getChild(i);
                List<AccessibilityNodeInfo> listByClassName = AccUtils.findElementListByContainClassName(nodeChild, "android.widget.TextView");
                if (listByClassName != null && listByClassName.size() > 0) {
                    AccessibilityNodeInfo nodeInfo = listByClassName.get(0);
                    String nameItem = String.valueOf(nodeInfo.getText());
                    if (nameItem.length() > 1) {
                        AccUtils.printLogMsg("=> " + nameItem);
                        nameItemList.add(nameItem);
                    }
                }
            }
            targetClassNameNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            AccUtils.timeSleep(waitThreeSecond);
        }

    }

    public static AccessibilityNodeInfo findTargetClassNameNode() throws ExitException {
        List<AccessibilityNodeInfo> listByClassName = AccUtils.findElementListByContainClassName("androidx.recyclerview.widget.RecyclerView");
        AccessibilityNodeInfo targetClassNameNode = null;
        if (listByClassName != null && listByClassName.size() > 0) {
            for (AccessibilityNodeInfo classNameNode : listByClassName) {
                if (classNameNode.isScrollable()) {
                    AccUtils.printLogMsg("found classNameNode => " + classNameNode.getClassName() + " isScrollable => " + classNameNode.isScrollable());
                    targetClassNameNode = classNameNode;
                    return targetClassNameNode;
                }
            }
        }else {
            AccUtils.printLogMsg("not found tableRow");
        }
        return null;
    }
}
