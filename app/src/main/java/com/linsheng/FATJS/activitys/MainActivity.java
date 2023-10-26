package com.linsheng.FATJS.activitys;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.linsheng.FATJS.R;
import com.linsheng.FATJS.aione_editor.EditorActivity;
import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.script.dyService.DyTaskService;
import com.linsheng.FATJS.service.MyService;
import com.linsheng.FATJS.utils.FileUtils;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    File patch_signed_7zip = null;
    private String readFromTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        GlobalVariableHolder.context = this.getApplicationContext();

        printLogMsg("onCreate() is run");

        setContentView(R.layout.activity_four);

        buildAdapter();

        initDisplay();
    }

    // 任务开始入口
    private void start_run_dy() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {

                    DyTaskService dyTaskService = new DyTaskService();
                    dyTaskService.main();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
//                Toast.makeText(this.getApplicationContext(), "悬浮窗授权失败", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(this.getApplicationContext(), "悬浮窗授权成功", Toast.LENGTH_SHORT).show();
//                // 打开悬浮窗
//                startService(new Intent(GlobalVariableHolder.context, FloatingButton.class));
//                // 打开悬浮窗
//                startService(new Intent(GlobalVariableHolder.context, FloatingWindow.class));
//            }
//        }
//    }

    private void buildAdapter() {
        //2、绑定控件
        ListView listView = findViewById(R.id.list_view);
        //3、准备数据
        String[] data={
                //"版本号 => 2.1.6",
                "测试",
                "开启无障碍",
                //"ANDROID_ID: " + Variable.ANDROID_ID,
                //"PHONE_NAME: " + Variable.PHONE_NAME,
                "跳转到设置无障碍页面",
                "代码开源 GitHub 搜索 FATJS",
                "仅用于学习交流，切勿用于非法途径，否则与作者无关",
        };
        //4、创建适配器 连接数据源和控件的桥梁
        //参数 1：当前的上下文环境
        //参数 2：当前列表项所加载的布局文件
        //(android.R.layout.simple_list_item_1)这里的布局文件是Android内置的，里面只有一个textview控件用来显示简单的文本内容
        //参数 3：数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,data);
        //5、将适配器加载到控件中
        listView.setAdapter(adapter);
        //6、为列表中选中的项添加单击响应事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String result=((TextView)view).getText().toString();

                switchResult(result, view);
            }
        });
    }

    private void switchResult(String result, View view) {
        // 跳转无障碍页面
        if (result.equals("跳转到设置无障碍页面")) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        // 仅用于学习交流，切勿用于非法途径
        if (result.equals("仅用于学习交流，切勿用于非法途径，否则与作者无关")) {
            return;
        }

        // 代码开源 GitHub 搜索 FATJS
        if (result.equals("代码开源 GitHub 搜索 FATJS")) {
            startActivity(new Intent(this, EditorActivity.class));
            return;
        }

        //判断无障碍是否开启
        if (!isAccessibilityServiceOn()) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }else {
            // 初始化
            if (result.equals("init webSocket")) {
                // 移动悬浮窗
                btnTextView.setText("全屏");
                Intent intent = new Intent();
                intent.setAction("com.msg");
                intent.putExtra("msg", "show_max");
                context.sendBroadcast(intent);
            }

            if (result.equals("测试")) {
                start_run_dy();
            }
        }
    }

    public void initDisplay() {
        DisplayMetrics dm = new DisplayMetrics();//屏幕度量
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        defaultDisplay.getRealMetrics(dm);
        mWidth = dm.widthPixels;//宽度
        mHeight = dm.heightPixels;//高度
        DisplayMetrics __dm = new DisplayMetrics();//屏幕度量
        getWindowManager().getDefaultDisplay().getMetrics(__dm);
        __mHeight = __dm.heightPixels;//去掉导航栏和状态栏的高度
    }

    private void getPhoneInfo() {
        // 获取 ANDROID_ID
        GlobalVariableHolder.ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        printLogMsg( "ANDROID_ID =>" + GlobalVariableHolder.ANDROID_ID);

        // 获取手机名称
        String phoneName = getPhoneName();
        GlobalVariableHolder.PHONE_NAME = phoneName;
        printLogMsg( "phoneName => " + phoneName);
    }

    private String readOrCreateVersion(String absolutePath) {
        String fromXML = FileUtils.readFromTxt(absolutePath);
        if (fromXML == null || fromXML.equals("")) {
            FileUtils.writeToTxt(absolutePath, "2.1.6");
            return "2.1.6";
        }
        return fromXML;
    }

    // 获取本机名称
    private String getPhoneName() {
        return Settings.Secure.getString(getContentResolver(), "bluetooth_name"); // 手机名称
    }

    // 广播
    DataReceiver dataReceiver = null;
    private static final String ACTIONR = "com.jumpUid";
    @Override
    protected void onStart() {//重写onStart方法
        super.onStart();

        if (dataReceiver == null) {
            dataReceiver = new DataReceiver();
            IntentFilter filter = new IntentFilter();//创建IntentFilter对象
            filter.addAction(ACTIONR);
            registerReceiver(dataReceiver, filter);//注册Broadcast Receiver
        }

    }
    // 广播内部类
    public class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            GlobalVariableHolder.broadcast_map.put("jumpUid", false);
            printLogMsg( "onReceive广播: " + intent.getAction());
            printLogMsg( "onReceive: param -> " + intent.getStringExtra("tem"));

            // UID跳转
            Intent intentToUri = new Intent();
            intentToUri.setData(Uri.parse("snssdk1128://user/profile/" + intent.getStringExtra("tem")));
            startActivity(intentToUri);
        }
    }

    // 判断本程序的无障碍服务是否已经开启
    public Boolean isAccessibilityServiceOn() {
        try{
            String packageName = this.getPackageName();
            String service = packageName + "/" + packageName + ".MyAccessibilityService";
            int enabled = Settings.Secure.getInt(GlobalVariableHolder.context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
            if (enabled == 1) {
                String settingValue = Settings.Secure.getString(GlobalVariableHolder.context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (settingValue != null) {
                    splitter.setString(settingValue);
                    while (splitter.hasNext()) {
                        String accessibilityService = splitter.next();
                        if (accessibilityService.equals(service)) {
                            return true;
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}