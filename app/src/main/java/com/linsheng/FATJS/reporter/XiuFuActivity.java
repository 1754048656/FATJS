package com.linsheng.FATJS.reporter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.linsheng.FATJS.R;
import com.linsheng.FATJS.BuildConfig;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

public class XiuFuActivity extends AppCompatActivity{

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FATJS_DIR/patch_signed_7zip.apk";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textView = findViewById(R.id.tv_text);
//        textView.setText("补丁信息2");
//
//        //请求文件权限
//        RxPermissions.request(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .subscribe(granted -> {
//                    if (granted) {
//                        //权限允许
//                    } else {
//                        //权限拒绝
//                    }
//                });
    }

    /**
     * 加载热补丁插件
     */
    public void loadPatch() {
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);
    }

    /**
     * 查看补丁信息
     */
    public void showInfo(View v) {
        // add more Build Info
        final StringBuilder sb = new StringBuilder();
        Tinker tinker = Tinker.with(getApplicationContext());
        if (tinker.isTinkerLoaded()) {
            sb.append(String.format("[补丁已加载] \n"));
            sb.append(String.format("[基准包版本号] %s \n", BuildConfig.TINKER_ID));

            sb.append(String.format("[补丁号] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName(ShareConstants.TINKER_ID)));
            sb.append(String.format("[补丁版本] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName("patchVersion")));
            sb.append(String.format("[补丁占用空间] %s k \n", tinker.getTinkerRomSpace()));

        } else {
            sb.append(String.format("[补丁未加载] \n"));
            sb.append(String.format("[基准包版本号] %s \n", BuildConfig.TINKER_ID));

            sb.append(String.format("[TINKER_ID] %s \n", ShareTinkerInternals.getManifestTinkerID(getApplicationContext())));
        }

        textView.setText(sb);
    }

    /**
     * 清除补包
     */
    public void cleanPatch(View v){
        Tinker.with(getApplicationContext()).cleanPatch();
    }
}