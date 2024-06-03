package com.linsheng.FATJS.cron4j;

import static com.linsheng.FATJS.config.GlobalVariableHolder.ABSOLUTE_PATH;
import static com.linsheng.FATJS.config.GlobalVariableHolder.CRON_TASK_FILE;
import static com.linsheng.FATJS.config.GlobalVariableHolder.isRunning;
import static com.linsheng.FATJS.config.GlobalVariableHolder.killThread;
import static com.linsheng.FATJS.config.GlobalVariableHolder.mHeight;
import static com.linsheng.FATJS.config.GlobalVariableHolder.mWidth;
import static com.linsheng.FATJS.config.GlobalVariableHolder.mainActivity;
import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;
import static com.linsheng.FATJS.node.AccUtils.isAccessibilityServiceOn;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.linsheng.FATJS.activitys.aione_editor.EditorActivity;
import com.linsheng.FATJS.node.TaskBase;
import com.linsheng.FATJS.utils.ExceptionUtil;
import com.linsheng.FATJS.utils.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CronTaskManager {

    private Context context;
    private CronTask cronTask;

    public CronTaskManager(Context context) {
        this.context = context;
        cronTask = new CronTask();
    }

    public void initCronTask() {
        // 定义一个Cron表达式
        String cronPattern = "*/5 * * * *"; // 每5分钟触发一次
        // 这里可以根据fileName执行相应的任务
        Runnable task = this::execInitTask;
        cronTask.scheduleTask(cronPattern, task);
    }

    private void execInitTask() {
        Log.i(tag, "execInitTask");
    }

    public void loadTasksFromFile(String filename) {
        Log.i(tag, "定时任务启动: " + filename);
        try {
            File file = new File(ABSOLUTE_PATH + filename); // 替换为你的文件路径
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                parseAndScheduleTask(line);
            }
            reader.close();
        } catch (Exception e) {
            Log.i(tag, CRON_TASK_FILE + "读取失败");
        }
    }

    private void parseAndScheduleTask(String line) {
        // 正则表达式匹配 cron 表达式的前五个或六个字段
        String cronRegex = "^((\\S+\\s+){5,6})";
        Pattern pattern = Pattern.compile(cronRegex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String cronExpression = Objects.requireNonNull(matcher.group(1)).trim();
            String fileName = line.substring(matcher.end()).trim();
            scheduleTask(cronExpression, fileName);
        } else {
            Log.i(tag, "Invalid line format: " + line);
        }
    }

    private void scheduleTask(String cronExpression, String fileName) {
        Runnable task = () -> {
            // 这里可以根据fileName执行相应的任务
            execByTaskName(fileName);
        };
        cronTask.scheduleTask(cronExpression, task);
    }

    private void execByTaskName(String fileName) {
        File f = new File(EditorActivity.scripts_path);

        final String[] files = f.list();
        if (files != null) {
            List<String> list = Arrays.asList(files);
            if (list.contains(fileName)) {

                printLogMsg("\n************\n************\n\n定时任务开始执行: " + fileName);
                //Log.i(tag, "开始执行: " + fileName);
                if (!isAccessibilityServiceOn()){
                    //Log.i(tag, "请开启无障碍服务");
                    printLogMsg("请开启无障碍服务");
                    ThreadUtils.runOnMainThread(() -> {
                        Toast.makeText(context, "请开启无障碍服务", Toast.LENGTH_SHORT).show();
                        mainActivity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    });
                    return;
                }
                // 判断是否有任务正在执行
                if (isRunning) {
                    killThread = true;
                    Log.i(tag, "有任务正在执行");
                    Toast.makeText(context, "有任务正在执行", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(tag, "开始运行...");
                //if (smallCircle2 != null)
                //    smallCircle2.setText("暂停");

                try {
                    Log.i(tag, "w => " + mWidth + ", h => " + mHeight);
                    // 测试方法
                    testMethod(fileName);
                } catch (Exception e) {
                    Log.i(tag, ExceptionUtil.toString(e));
                }
            }
        }
    }

    /**
     * 开始执行
     */
    private void testMethod(String checkedFileName) {
        // Test 1
        TaskBase taskDemo = new TaskBase();
        Log.i(tag,"run script " + checkedFileName);
        String script_path = ABSOLUTE_PATH + checkedFileName;
        // Log.i(tag, "script_path => " + script_path, 0);
        taskDemo.initJavet(script_path);
    }

    public void start() {
        cronTask.start();
    }

    public void stop() {
        cronTask.stop();
    }

    public void clearAllTasks() {
        cronTask.clearAllTasks();
    }

}