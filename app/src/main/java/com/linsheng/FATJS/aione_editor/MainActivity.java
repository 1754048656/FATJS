package com.linsheng.FATJS.aione_editor;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.linsheng.FATJS.R;
import com.linsheng.FATJS.activitys.FloatingButton;
import com.linsheng.FATJS.activitys.FloatingWindow;
import com.linsheng.FATJS.aione_editor.EditorActivity;
import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.config.WindowPermissionCheck;
import com.linsheng.FATJS.databinding.ActivityMainBinding;
import com.linsheng.FATJS.findColor.config.CaptureScreenService;
import com.linsheng.FATJS.findColor.config.ScreenCaptureManager;
import com.linsheng.FATJS.service.MyService;
import com.linsheng.FATJS.ui.dashboard.DashboardFragment;
import com.linsheng.FATJS.ui.home.HomeFragment;
import com.linsheng.FATJS.ui.notifications.NotificationsFragment;
import com.linsheng.FATJS.utils.FileUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        // 开启前台服务 未适配低版本安卓
        openForwardService();
        openFloatWindow();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // , R.id.navigation_notifications
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (__mHeight == -1) {
            initDisplay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void to_acc(View v) {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private void openFloatWindow() {
        // 在其他应用上层显示
        boolean permission = WindowPermissionCheck.checkPermission(this);
        if (permission) {
            printLogMsg("onCreate: permission true => " + permission);
            // 打开悬浮窗
            startService(new Intent(GlobalVariableHolder.context, FloatingButton.class));
            // 打开悬浮窗
            startService(new Intent(GlobalVariableHolder.context, FloatingWindow.class));
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

    private void openForwardService() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 开启捕获屏幕
        if(requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent service = new Intent(this, CaptureScreenService.class);
                    service.putExtra("code", resultCode);
                    service.putExtra("data", data);
                    context.startForegroundService(service);
                } else {
                    ScreenCaptureManager.getInstance().start(resultCode, data);
                }
                ScreenCaptureManager.getInstance().state = ScreenCaptureManager.State.RUNNING;
                DashboardFragment._screen = true;
                DashboardFragment.switch_screen.setChecked(true);

                // 打开悬浮窗
                context.startService(new Intent(GlobalVariableHolder.context, FloatingButton.class));
                // 打开悬浮窗
                context.startService(new Intent(GlobalVariableHolder.context, FloatingWindow.class));

                Toast.makeText(context, "屏幕录制权限，部分可开启临时悬浮窗", Toast.LENGTH_SHORT).show();
            }else {
                ScreenCaptureManager.getInstance().state = ScreenCaptureManager.State.IDLE;
                DashboardFragment._screen = false;
                DashboardFragment.switch_screen.setChecked(false);
            }
        }
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

    // 获取本机名称
    private String getPhoneName() {
        return Settings.Secure.getString(getContentResolver(), "bluetooth_name"); // 手机名称
    }

}