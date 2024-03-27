package com.linsheng.FATJS.script;

import static com.linsheng.FATJS.config.GlobalVariableHolder.hashMapBuffer;
import static com.linsheng.FATJS.config.GlobalVariableHolder.isRunning;
import static com.linsheng.FATJS.config.GlobalVariableHolder.isStop;
import static com.linsheng.FATJS.config.GlobalVariableHolder.killThread;
import static com.linsheng.FATJS.node.AccUtils.loadScriptFromAssets;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.linsheng.FATJS.R;
import com.linsheng.FATJS.node.TaskBase;
import com.linsheng.FATJS.utils.ExceptionUtil;
import com.linsheng.FATJS.utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;

public class JsTaskDemo extends AppCompatActivity {
    EditText run_num_in;
    Button uid_file_in;
    TextView uid_file_path;
    EditText index_0_in;
    EditText index_1_in;
    EditText view_0_in;
    EditText view_1_in;
    EditText jump_0_in;
    EditText jump_1_in;
    CheckBox gender_m_in;
    CheckBox gender_w_in;
    CheckBox gender_no_in;
    Button start_run_btn;
    private static final int FILE_SELECT_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jstaskdemo);
        run_num_in = findViewById(R.id.run_num_in);
        uid_file_in = findViewById(R.id.uid_file_in);
        uid_file_path = findViewById(R.id.uid_file_path);
        index_0_in = findViewById(R.id.index_0_in);
        index_1_in = findViewById(R.id.index_1_in);
        view_0_in = findViewById(R.id.view_0_in);
        view_1_in = findViewById(R.id.view_1_in);
        jump_0_in = findViewById(R.id.jump_0_in);
        jump_1_in = findViewById(R.id.jump_1_in);
        gender_m_in = findViewById(R.id.gender_m_in);
        gender_w_in = findViewById(R.id.gender_w_in);
        gender_no_in = findViewById(R.id.gender_no_in);
        start_run_btn = findViewById(R.id.start_run_btn);

        uid_file_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        start_run_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            test();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });
    }

    private void test() throws IOException {
        hashMapBuffer = new HashMap<>();
        hashMapBuffer.put("run_num", Integer.parseInt(run_num_in.getText().toString()));
        hashMapBuffer.put("uid_file", uid_file_path.getText().toString());
        hashMapBuffer.put("index_0", Integer.parseInt(index_0_in.getText().toString()));
        hashMapBuffer.put("index_1", Integer.parseInt(index_1_in.getText().toString()));
        hashMapBuffer.put("view_0", Integer.parseInt(view_0_in.getText().toString()));
        hashMapBuffer.put("view_1", Integer.parseInt(view_1_in.getText().toString()));
        hashMapBuffer.put("jump_0", Integer.parseInt(jump_0_in.getText().toString()));
        hashMapBuffer.put("jump_1", Integer.parseInt(jump_1_in.getText().toString()));
        hashMapBuffer.put("gender_m", gender_m_in.isChecked());
        hashMapBuffer.put("gender_w", gender_w_in.isChecked());
        hashMapBuffer.put("gender_no", gender_no_in.isChecked());
        testMethod();
    }

    /**
     * 测试方法
     */
    private void testMethod() throws IOException {
        String checkedFileName = "uid_action.js";
        String uid_action = loadScriptFromAssets(checkedFileName);
        // 将测试的动作写到这里
        printLogMsg("run script " + checkedFileName, 0);
        try {
            isRunning = true;
            isStop = false;
            killThread = false;
            TaskBase.execScript(uid_action);
        }catch (Exception e) {
            printLogMsg(ExceptionUtil.toString(e), 0);
        }finally {
            isRunning = false;
            isStop = false;
            killThread = false;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Set MIME type to all files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            // 启动Activity，并期望获取结果
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Handle exception here
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                // 获取选择的文件的Uri
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    // 显示文件路径
                    String fileUriPath = selectedFileUri.getPath();
                    if (fileUriPath.contains("primary:")) {
                        String path = "/sdcard/" + fileUriPath.split("primary:")[1];
                        uid_file_path.setText(path);
                    }
                }
            }
        }
    }
}