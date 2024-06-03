package com.linsheng.FATJS.ui.dashboard;

import static com.linsheng.FATJS.config.GlobalVariableHolder.ABSOLUTE_PATH;
import static com.linsheng.FATJS.config.GlobalVariableHolder.CRON_TASK;
import static com.linsheng.FATJS.config.GlobalVariableHolder.CRON_TASK_FILE;
import static com.linsheng.FATJS.config.GlobalVariableHolder.CRON_TASK_FILE_TEST;
import static com.linsheng.FATJS.config.GlobalVariableHolder.DEV_MODE;
import static com.linsheng.FATJS.config.GlobalVariableHolder.PATH;
import static com.linsheng.FATJS.config.GlobalVariableHolder.context;
import static com.linsheng.FATJS.config.GlobalVariableHolder.reviewConfig;
import static com.linsheng.FATJS.config.GlobalVariableHolder.saveConfig;
import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.linsheng.FATJS.R;
import com.linsheng.FATJS.activitys.FloatingButton;
import com.linsheng.FATJS.activitys.FloatingWindow;
import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.config.WindowPermission;
import com.linsheng.FATJS.databinding.FragmentDashboardBinding;
import com.linsheng.FATJS.findColor.config.ScreenCaptureManager;
import com.linsheng.FATJS.utils.FileUtils;
import com.linsheng.FATJS.utils.StringUtils;

import java.util.List;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        permissions(root); // 赋予权限
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onStart() {
        super.onStart();
        checkPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkPermissions() {
        storagePermission();
        floatPermission();
        accessibilityPermission();
        screenPermission();
        devPermission();
        cronTaskPermission();
    }

    private void cronTaskPermission() {
        switch_cron.setChecked(CRON_TASK);
    }

    private void devPermission() {
        switch_dev.setChecked(DEV_MODE);
    }

    private void getDevPermission() {
        DEV_MODE = true;
        switch_dev.setChecked(DEV_MODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void storagePermission() {
        boolean permission = false;
        try {
            String directoryPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + PATH;
            String filePath = directoryPath + "version.txt";
            printLogMsg(directoryPath, 0);
            FileUtils.createDirectory(directoryPath);
            permission = FileUtils.writeToTxt(filePath, "test");
        }catch (Exception e) {}

        _storage = permission;
        switch_storage.setChecked(permission);
        if (permission) {
            // 权限已经被授予，可以进行SD卡读写操作
            //printLogMsg("SD卡读写权限已授予", 0);
            cronTaskDemo();
        } else {
            // 权限尚未被授予，需要进行相应处理
            printLogMsg("SD卡读写权限未授予", 0);
        }
    }

    @SuppressLint("SdCardPath")
    private void cronTaskDemo() {
        if (!FileUtils.fileExists(ABSOLUTE_PATH + CRON_TASK_FILE)) {
            FileUtils.writeFile(ABSOLUTE_PATH + CRON_TASK_FILE_TEST, "showLog();print('cron task is running')");
            FileUtils.writeFile(ABSOLUTE_PATH + CRON_TASK_FILE, "* * * * *  " + CRON_TASK_FILE_TEST);
        }
    }

    public void getStoragePermission() {
        // 获取权限
        XXPermissions.with(this)
        //申请单个权限
        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
        //申请多个权限
        //.permission(Permission.READ_MEDIA_IMAGES)
        //.permission(Permission.READ_MEDIA_VIDEO)
        //.permission(Permission.READ_MEDIA_AUDIO)
        //.permission(Permission.SYSTEM_ALERT_WINDOW)
        // 设置权限请求拦截器（局部设置）
        //.interceptor(new PermissionInterceptor())
        // 设置不触发错误检测机制（局部设置）
        //.unchecked()
        .request(new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                if (!allGranted) {
                    switch_storage.setChecked(false);
                    return;
                }
                _storage = true;
                switch_storage.setChecked(true);
                FileUtils.createDirectory(Environment.getExternalStorageDirectory() + PATH);
                FileUtils.writeToTxt(
                        Environment.getExternalStorageDirectory() + PATH + "version.txt",
                        "test");
                String config = FileUtils.readFile(Environment.getExternalStorageDirectory() + PATH + "config.json");
                if (StringUtils.isEmpty(config)) {
                    printLogMsg("config is empty", 0);
                    saveConfig();
                }else {
                    printLogMsg("config is not empty, review config", 0);
                    reviewConfig();
                }
            }
            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                switch_storage.setChecked(false);
                if (doNotAskAgain) {
                    printLogMsg("被永久拒绝授权，请手动授予权限", 0);
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    //XXPermissions.startPermissionActivity(context, permissions);
                } else {
                    printLogMsg("获取权限失败", 0);
                }
            }
        });
    }
    private void floatPermission() {
        // 在其他应用上层显示
        boolean permission = WindowPermission.checkPermission(getActivity());
        _float = permission;
        switch_float.setChecked(permission);
        if (permission) {
            //printLogMsg("悬浮窗权限已授予", 0);
        }else {
            // 权限尚未被授予，需要进行相应处理
            printLogMsg("悬浮窗权限未授予", 0);
        }
    }
    public void getFloatPermission() {
        // 获取权限
        XXPermissions.with(this)
        //申请单个权限
        //.permission(Permission.MANAGE_EXTERNAL_STORAGE)
        //申请多个权限
        //.permission(Permission.READ_MEDIA_IMAGES)
        //.permission(Permission.READ_MEDIA_VIDEO)
        //.permission(Permission.READ_MEDIA_AUDIO)
        .permission(Permission.SYSTEM_ALERT_WINDOW)
        // 设置权限请求拦截器（局部设置）
        //.interceptor(new PermissionInterceptor())
        // 设置不触发错误检测机制（局部设置）
        //.unchecked()
        .request(new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                if (!allGranted) {
                    switch_float.setChecked(false);
                    return;
                }
                _float = true;
                switch_float.setChecked(true);
                // 打开悬浮窗
                context.startService(new Intent(GlobalVariableHolder.context, FloatingWindow.class));
                // 打开悬浮窗
                context.startService(new Intent(GlobalVariableHolder.context, FloatingButton.class));
            }
            @Override
            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                switch_float.setChecked(false);
                if (doNotAskAgain) {
                    printLogMsg("被永久拒绝授权，请手动授予权限", 0);
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(context, permissions);
                } else {
                    printLogMsg("获取权限失败", 0);
                }
            }
        });
    }
    private void screenPermission() {
        if (ScreenCaptureManager.getInstance().isOpen()) {
            _screen = true;
            switch_screen.setChecked(true);
            //printLogMsg("屏幕录制权限已授予", 0);
            return;
        }
        _screen = false;
        switch_screen.setChecked(false);
        //printLogMsg("屏幕录制权限未授予", 0);
    }
    // 开启捕获屏幕
    public void getMediaProjectionManger() {
        ScreenCaptureManager.getInstance().init(getActivity());
    }
    private boolean accessibilityPermission() {
        try{
            String packageName = context.getPackageName();
            String service = packageName + "/" + packageName + ".MyAccessibilityService";
            int enabled = Settings.Secure.getInt(GlobalVariableHolder.context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
            if (enabled == 1) {
                String settingValue = Settings.Secure.getString(GlobalVariableHolder.context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (StringUtils.isNotEmpty(settingValue)) {
                    splitter.setString(settingValue);
                    while (splitter.hasNext()) {
                        String accessibilityService = splitter.next();
                        if (accessibilityService.equals(service)) {
                            _accessibility = true;
                            switch_accessibility.setChecked(true);
                            //printLogMsg("无障碍权限已授予", 0);
                            return true;
                        }
                    }
                }else {
                    // 系统隐藏了无障碍应用列表
                    _accessibility = true;
                    switch_accessibility.setChecked(true);
                    return true;
                }
            }
        }catch (Exception e){
            _accessibility = false;
            switch_accessibility.setChecked(false);
            printLogMsg("无障碍权限未授予", 0);
            return false;
        }
        _accessibility = false;
        switch_accessibility.setChecked(false);
        printLogMsg("无障碍权限未授予", 0);
        return false;
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch_storage;
    boolean _storage = false;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public static Switch switch_float;
    public static boolean _float = false;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch_accessibility;
    boolean _accessibility = false;
    @SuppressLint({"UseSwitchCompatOrMaterialCode", "StaticFieldLeak"})
    public static Switch switch_screen;
    public static boolean _screen = false;
    @SuppressLint({"UseSwitchCompatOrMaterialCode"})
    Switch switch_dev;
    @SuppressLint({"UseSwitchCompatOrMaterialCode"})
    Switch switch_cron;
    private void permissions(View root) {
        switch_storage = root.findViewById(R.id.switch_storage);
        switch_float = root.findViewById(R.id.switch_float);
        switch_screen = root.findViewById(R.id.switch_screen);
        switch_accessibility = root.findViewById(R.id.switch_accessibility);
        switch_dev = root.findViewById(R.id.switch_dev);
        switch_cron = root.findViewById(R.id.switch_cron);
        switch_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_storage) {
                    printLogMsg("switch_storage already True", 0);
                    switch_storage.setChecked(_storage);
                    Toast.makeText(context, "switch_storage already True", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(tag, "switch_storage => " + switch_storage.isChecked());
                getStoragePermission();
            }
        });
        switch_float.setOnClickListener(v -> {
            if (_float) {
                printLogMsg("switch_float already True", 0);
                switch_float.setChecked(_float);
                Toast.makeText(context, "switch_float already True", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(tag, "switch_float => " + switch_float.isChecked());
            getFloatPermission();
        });
        switch_accessibility.setOnClickListener(v -> {
            if (_accessibility) {
                printLogMsg("switch_accessibility already True", 0);
                switch_accessibility.setChecked(_accessibility);
                Toast.makeText(context, "switch_accessibility already True", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(tag, "switch_accessibility => " + switch_accessibility.isChecked());
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });
        switch_screen.setOnClickListener(v -> {
            if (_screen) {
                printLogMsg("switch_screen already True", 0);
                _screen = false;
                ScreenCaptureManager.getInstance().stop();
                return;
            }
            Log.i(tag, "switch_screen => " + switch_screen.isChecked());
            getMediaProjectionManger();
        });
        switch_dev.setOnClickListener(v -> {
            DEV_MODE = !DEV_MODE;
            switch_dev.setChecked(DEV_MODE);
            saveConfig();
            Log.i(tag, "DEV_MODE => " + DEV_MODE);
            Toast.makeText(context, "DEV_MODE => " + DEV_MODE, Toast.LENGTH_SHORT).show();
        });
        switch_cron.setOnClickListener(v -> {
            CRON_TASK = !CRON_TASK;
            switch_cron.setChecked(CRON_TASK);
            saveConfig();
            Log.i(tag, "CRON_TASK => " + CRON_TASK);
            Toast.makeText(context, "CRON_TASK => " + CRON_TASK, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}