package com.linsheng.FATJS.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TxTManager {
    public static String rootXMLPath = Environment.getExternalStorageDirectory().getPath() + "/testTXT";

    private static final String TAG = "FATJS";

    /**
     * 保存内容到TXT文件中
     */
    public static boolean writeToXML(String fileName, String content) {
//        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        if (hasSDCard) {
//            fileName = Environment.getExternalStorageDirectory().toString() + File.separator+"/test/" + fileName +".txt";
//        } else{
//            fileName = Environment.getDownloadCacheDirectory().toString() + File.separator +"/test/" +fileName + ".txt";
//        }
        FileOutputStream fileOutputStream;
        BufferedWriter bufferedWriter;
        createDirectory(rootXMLPath);
        File file = new File(fileName);
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToXML: java.io.FileNotFoundException: version.txt: open failed: EACCES (Permission denied)");
            return false;
        }
        return true;
    }
    /**
     * 读取内容
     *
     * @param filePath
     * @return
     */
    public static String readFromXML(String filePath) {
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(filePath);
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "readFromXML: FileNotFoundException");
                return null;
            } catch (IOException e) {
                Log.e(TAG, "readFromXML: IOException");
                return null;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 创建文件夹
     * @param fileDirectory
     */
    public static void createDirectory(String fileDirectory) {
        File file = new File(fileDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}