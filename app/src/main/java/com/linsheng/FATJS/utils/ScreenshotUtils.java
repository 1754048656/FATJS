package com.linsheng.FATJS.utils;

import static com.linsheng.FATJS.config.GlobalVariableHolder.context;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.linsheng.FATJS.findColor.GBData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenshotUtils {

    public static boolean capture(String filePath) {
        Bitmap bmp = GBData.getImageBitmap();
        File filePic = new File(filePath);
        try {
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            printLogMsg("保存成功,位置:" + filePic.getAbsolutePath());
        } catch (IOException e) {
            printLogMsg(ExceptionUtil.toString(e));
            printLogMsg("保存失败");
            return false;
        }
        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), filePic.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
        return true;
    }

    public static void save(Bitmap bitmap, String filename) {
        String filePath = Environment.getExternalStorageDirectory().toString() + "/" + filename + ".png";
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
