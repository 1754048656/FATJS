package com.linsheng.FATJS.utils;

import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.linsheng.FATJS.config.GlobalVariableHolder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = GlobalVariableHolder.tag;

    /**
     * 保存内容到TXT文件中
     */
    public static boolean writeToTxt(String fileName, String content) {
        FileOutputStream fileOutputStream;
        BufferedWriter bufferedWriter;
        File file = new File(fileName);
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            printLogMsg("writeToTxt: java.io.FileNotFoundException: version.txt: open failed: EACCES (Permission denied)");
            return false;
        }
        return true;
    }
    /**
     * 读取内容
     * @param filePath
     * @return
     */
    public static String readFromTxt(String filePath) {
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
                printLogMsg("readFromTxt: FileNotFoundException");
                return null;
            } catch (IOException e) {
                printLogMsg("readFromTxt: IOException");
                return null;
            }
        }
        return stringBuilder.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {}
        return content.toString();
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

    /**
     * 移动文件
     * @param sourceFilePath
     * @param targetFolderPath
     * @return
     */
    public static boolean moveFile(String sourceFilePath, String targetFolderPath) {
        File sourceFile = new File(sourceFilePath);
        File targetFolder = new File(targetFolderPath);

        if (sourceFile.exists() && sourceFile.isFile() && targetFolder.exists() && targetFolder.isDirectory()) {
            boolean isMoved = sourceFile.renameTo(new File(targetFolder, sourceFile.getName()));
            if (isMoved) {
                // 移动成功
                printLogMsg("移动成功");
                return true;
            }
        }
        // 移动失败
        printLogMsg("移动失败");
        return false;
    }

    /**
     * 查看某一目录所有文件
     * @param folderPath
     * @return
     */
    public static List<String> pathFileList(String folderPath) {
        List<String> list = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    String filePath = file.getPath();
                    // 其他操作
                    printLogMsg(filePath);
                    list.add(filePath);
                }
            }
        }
        return list;
    }

    /**
     * 重命名文件
     * @param sourceFilePath
     * @param targetFilePath
     * @return
     */
    public static boolean renameFile(String sourceFilePath, String targetFilePath) {
        // 创建源文件对象和目标文件对象
        File sourceFile = new File(sourceFilePath);
        File targetFile = new File(targetFilePath);

        // 使用renameTo()方法重命名文件
        if (sourceFile.renameTo(targetFile)) {
            printLogMsg("文件重命名成功");
            return true;
        }
        printLogMsg("文件重命名失败");
        return false;
    }

    /**
     * 删除对应文件
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 从文本中逐行读取
     * @param filePath
     * @return
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}