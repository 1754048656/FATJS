package com.linsheng.FATJS.node;

import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson2.JSONObject;

public class App {

    public Intent _intent(String jsonText) {
        JSONObject parseObject = JSONObject.parseObject(jsonText);
        String data = parseObject.getString("data");
        Intent intent = new Intent();
        intent.setData(Uri.parse(data));
        return intent;
    }

}
