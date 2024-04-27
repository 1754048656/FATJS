package com.linsheng.FATJS.node;

import android.content.Intent;
import android.net.Uri;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class App {
    public Intent _intent(String jsonText) {
        JsonObject jsonObject = JsonParser.parseString(jsonText).getAsJsonObject();
        String data = jsonObject.get("data").getAsString();
        Intent intent = new Intent();
        intent.setData(Uri.parse(data));
        return intent;
    }
}
