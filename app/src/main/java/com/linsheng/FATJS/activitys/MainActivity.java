package com.linsheng.FATJS.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.linsheng.FATJS.AccUtils;
import com.linsheng.FATJS.R;
import com.linsheng.FATJS.bean.Variable;
import com.linsheng.FATJS.config.WindowPermissionCheck;
import com.linsheng.FATJS.rpa.dyService.DyTaskService;
import com.linsheng.FATJS.service.MyService;
import com.linsheng.FATJS.utils.TxTManager;
import com.cmcy.rxpermissions2.RxPermissions;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FATJS";
    File patch_signed_7zip = null;
    private String readFromTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()被调用了");

        Variable.context = this.getApplicationContext();

        // 存储权限
        storagePermissions();

        // 获取手机信息
        // getPhoneInfo();

        // 开启前台服务 未适配低版本安卓
         openForwardService();

        // 从 sdcard 读取版本号和抖音id号
        readIDFromSdcard();

        setContentView(R.layout.activity_main);

        // 在其他应用上层显示
        boolean permission = WindowPermissionCheck.checkPermission(this);
        if (permission) {
            Log.i(TAG, "onCreate: permission true => " + permission);
            // 打开悬浮窗
            startService(new Intent(Variable.context, FloatingButton.class));
            // 打开悬浮窗
            startService(new Intent(Variable.context, FloatingWindow.class));
        }

        buildAdapter();

        initDisplay();
    }

    // 任务开始入口
    private void start_run() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                Toast.makeText(this.getApplicationContext(), "悬浮窗授权失败", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this.getApplicationContext(), "悬浮窗授权成功", Toast.LENGTH_SHORT).show();
                // 打开悬浮窗
                startService(new Intent(Variable.context, FloatingButton.class));
                // 打开悬浮窗
                startService(new Intent(Variable.context, FloatingWindow.class));
            }
        }
    }

    private void buildAdapter() {
        //2、绑定控件
        ListView listView = (ListView) findViewById(R.id.list_view);
        //3、准备数据
        String[] data={
                "版本号 => " + readFromTxt,
                "开启无障碍",
                "开始任务",
                //"ANDROID_ID: " + Variable.ANDROID_ID,
                //"PHONE_NAME: " + Variable.PHONE_NAME,
                //"load fix patch",
                "跳转到设置无障碍页面",
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
        if (result.equals("to acc")) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        // 加载要修复的包
        if (result.equals("load fix patch")) {
            // loadBtn = (TextView)view;
            loadPatch();
            return;
        }

        //判断无障碍是否开启
        if (!isAccessibilityServiceOn()) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }else {
            // 初始化
            if (result.equals("init webSocket")) {
                // 移动悬浮窗
                Variable.btnTextView.setText("全屏");
                Intent intent = new Intent();
                intent.setAction("com.msg");
                intent.putExtra("msg", "show_max");
                Variable.context.sendBroadcast(intent);

            }

            if (result.equals("开始任务")) {
                start_run();
            }
        }
    }

    public void initDisplay() {
        DisplayMetrics dm = new DisplayMetrics();//屏幕度量
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Variable.mWidth = dm.widthPixels;//宽度
        Variable.mHeight = dm.heightPixels ;//高度
    }

    private void readIDFromSdcard() {
        // 判断有没有 FATJS_DIR 目录  没有则创建
        File dirFile = new File(Environment.getExternalStorageDirectory() + "/FATJS_DIR");
        if (!dirFile.exists()) {
            if (dirFile.mkdir()) {
                Log.i(TAG, "onCreate: FATJS_DIR 目录创建成功");
            }
        }

        // 读取version.txt的版本号
        File versionFile = new File(Environment.getExternalStorageDirectory() + "/FATJS_DIR/version.txt");
        readFromTxt = readOrCreateVersion(versionFile.getAbsolutePath());
        readFromTxt = readFromTxt.trim();
    }

    private void openForwardService() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    private void getPhoneInfo() {
        // 获取 ANDROID_ID
        Variable.ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        Log.i(TAG, "attachBaseContext: ANDROID_ID--->" + Variable.ANDROID_ID);

        // 获取手机名称
        String phoneName = getPhoneName();
        Variable.PHONE_NAME = phoneName;
        Log.i(TAG, "onCreate: phoneName => " + phoneName);
    }

    // 获取 sdcard 读写权限
    private void storagePermissions() {
        // 存储权限
        RxPermissions.request(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(granted -> {
                if (granted) {
                    //权限允许
                    readIDFromSdcard();
                } else {
                    //权限拒绝
                    Toast.makeText(this.getApplicationContext(), "存储授权失败", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private String readOrCreateVersion(String absolutePath) {
        String fromXML = TxTManager.readFromTxt(absolutePath);
        if (fromXML == null || fromXML.equals("")) {
            TxTManager.writeToTxt(absolutePath, "2.1.6");
            return "2.1.6";
        }
        return fromXML;
    }

    // 获取本机名称
    private String getPhoneName() {
        return Settings.Secure.getString(getContentResolver(), "bluetooth_name"); // 手机名称
    }

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FATJS_DIR/patch_signed_7zip.apk";
    /**
     * 加载热补丁插件
     */
    public void loadPatch() {
        // 查看 sdcard/FATJS_DIR 目录下是否有 patch 包
        patch_signed_7zip = new File(Environment.getExternalStorageDirectory() + "/FATJS_DIR/patch_signed_7zip.apk");
        if (patch_signed_7zip.exists()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // load patch 包
                    TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);
                }
            }).start();

        }else {
            Log.i(TAG, "loadPatch: no patch_signed_7zip");
            Toast.makeText(Variable.context, "no patch_signed_7zip", Toast.LENGTH_LONG).show();
        }
    }

    // 广播
    DataReceiver dataReceiver = null;
    private static final String ACTIONR = "com.jumpUid";
    @Override
    protected void onStart() {//重写onStart方法
        super.onStart();

        Log.i(TAG, "onStart()");

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
            Variable.broadcast_map.put("jumpUid", false);
            Log.i(TAG, "onReceive广播: " + intent.getAction());
            Log.i(TAG, "onReceive: param -> " + intent.getStringExtra("tem"));

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
            Log.i(TAG, "isAccessibilityServiceOn: packageName => " + packageName);
            String service = packageName + "/" + packageName + ".MyAccessibilityService";
            int enabled = Settings.Secure.getInt(Variable.context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
            if (enabled == 1) {
                String settingValue = Settings.Secure.getString(Variable.context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
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
        Log.i(TAG, "onDestroy: 销毁了");
        super.onDestroy();
    }
}