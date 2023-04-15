package com.linsheng.FATJS.rpa.TaskFactory;

import com.linsheng.FATJS.AccUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaskBasic {
    private static final String TAG = "FATJS";

    public TaskBasic(String NAME) {
        this.NAME = NAME;
    }

    // 任务名称
    public String NAME = "";
    // 任务时间
    public Date startTime;

    // 停顿时长
    public final int waitHrefOfSecond  =   500;
    public final int waitOneSecond     =   1000;
    public final int waitTwoSecond     =   2000;
    public final int waitThreeSecond   =   3000;
    public final int waitFourSecond    =   4000;
    public final int waitFiveSecond    =   5000;
    public final int waitSixSecond     =   6000;

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

}
