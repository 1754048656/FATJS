package com.linsheng.FATJS.enums;

//调度任务类型枚举
public enum TaskTypeEnum {
    SEND_MESSAGE(1,"私信","私信"),
    WECHAT_SEND_MESSAGE(2,"微信群发","微信群发");

    private int code;
    private String name;
    private String descTxt;

    TaskTypeEnum(int code, String name, String descTxt) {
        this.code = code;
        this.name = name;
        this.descTxt = descTxt;
    }

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }


    public String getDescTxt() {
        return descTxt;
    }

}
