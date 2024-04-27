package com.linsheng.FATJS.activitys.aione_editor;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.linsheng.FATJS.R;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ThreadLocalRandom;


public class AboutActivity extends AppCompatActivity {
    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        version = findViewById(R.id.app_version);
        version.setText(version());
        final int[] colors = {R.color.about_bg1, R.color.about_bg2, R.color.about_bg3, R.color.about_bg4};
        final View layout = findViewById(R.id.about_layout);
        int i = 0;
        i = ThreadLocalRandom.current().nextInt(0, 4);
        layout.setBackgroundColor(getResources().getColor(colors[i]));
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        Thread.sleep(3000);
                        final int color = colors[i];
                        layout.post(new Runnable() {
                            @Override
                            public void run() {
                                layout.setBackgroundColor(getResources().getColor(color));
                            }
                        });
                        i++;
                        if (i == 4) i = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void share(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=avd.ma7moud3ly.com");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.app_name)));
    }

    public void rate(View v) {

        Uri uri = Uri.parse("FBD://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }

    }

    public void contact(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri d = Uri.parse("http://www.facebook.com/engma7moud3ly");
        i.setData(d);
        startActivity(i);
    }

    private String version() {
        String v = "1";
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
            v = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return v;
    }

}