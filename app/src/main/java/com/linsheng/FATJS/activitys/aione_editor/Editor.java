package com.linsheng.FATJS.activitys.aione_editor;

import static com.linsheng.FATJS.activitys.aione_editor.MainActivity.cronTaskManager;
import static com.linsheng.FATJS.config.GlobalVariableHolder.CRON_TASK;
import static com.linsheng.FATJS.config.GlobalVariableHolder.CRON_TASK_FILE;
import static com.linsheng.FATJS.config.GlobalVariableHolder.context;
import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linsheng.FATJS.R;

import java.io.File;

public class Editor {
    private EditText editor;
    private TextView title;
    private TextView lines;
    private Context context;
    private Activity activity;

    private boolean show_lines;
    private int font_size;
    private final int zoom_value = 2;
    private boolean isDark;

    public String path;
    public String name;
    public File script;

    public int AFTER_SAVE = -1;
    public String SCRIPT_PATH;

    public static final int SHR = 0;
    public static final int LIS = 1;
    public static final int NEW = 2;
    public static final int SAV = 3;
    public static final int END = 4;

    public Editor(EditText editor, TextView lines, TextView title, Context context, boolean isDark) {
        this.editor = editor;
        this.title = title;
        this.lines = lines;
        init_line_counter();
        this.context = context;
        this.activity = (Activity) context;
        this.isDark = isDark;

        getEditorSettings();

    }

    private void init_line_counter() {
        lines.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                editor.setScrollY(i1);
            }
        });

        editor.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                lines.setScrollY(i1);
            }
        });
        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int j, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                count_lines();
            }
        });
    }

    public void count_lines() {
        if (!show_lines) return;
        lines.postDelayed(new Runnable() {
            @Override
            public void run() {
                lines.setText("");
                int n = editor.getLineCount();
                String format = (n > 99) ? "%03d" : "%02d";
                for (int i = 1; i <= n; i++) {
                    String num = String.format(format, i);
                    lines.append(num + "\n");
                }
            }
        }, 150);
    }

    public void show_lines() {
        show_lines = !show_lines;
        if (show_lines) lines.setVisibility(View.VISIBLE);
        else lines.setVisibility(View.GONE);
        count_lines();
    }

    public void show_lines(boolean b) {
        show_lines = b;
        if (show_lines) lines.setVisibility(View.VISIBLE);
        else lines.setVisibility(View.GONE);
        count_lines();
    }


    public void zoomIn(boolean in) {
        if (in && font_size < 100) font_size += zoom_value;
        else if (!in && font_size > 8) font_size -= zoom_value;
        editor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, font_size);
        lines.setTextSize(TypedValue.COMPLEX_UNIT_DIP, font_size);
        count_lines();
    }

    public void check_save() {
        setEditorSettings();
        final String txt = editor.getText().toString();
        boolean empty_file = (txt.isEmpty() && script == null);
        boolean no_changes = (script != null && txt.equals(Scripts.read(path)));
        if (empty_file || no_changes) after_save();
        else save_dialog();
    }

    public void save() {
        Scripts.write(path, editor.getText().toString());
        if (name.equals(CRON_TASK_FILE)) {
            flushCronTask();
        }else {
            Toast.makeText(context, name + " saved", Toast.LENGTH_SHORT).show();
        }
        after_save();
        activity.finish();
    }

    private void flushCronTask() {
        if (cronTaskManager == null) {
            Log.i(tag, "cronTaskManager == null");
            return;
        }
        if (CRON_TASK) {
            cronTaskManager.clearAllTasks();
            cronTaskManager.loadTasksFromFile(CRON_TASK_FILE);
            cronTaskManager.start();
        }
        Toast.makeText(context, "定时任务已刷新", Toast.LENGTH_SHORT).show();
    }

    public void save_as_dialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, isDark ? R.style.AlertThemeDark : R.style.AlertThemeLight);
        final EditText fname = new EditText(context);
        fname.setText("untitled");
        fname.setInputType(InputType.TYPE_CLASS_TEXT);
        fname.setHint("title");
        fname.setSelectAllOnFocus(true);
        final EditText ext = new EditText(context);
        ext.setText(".js");
        ext.setHint("extension");
        ext.setInputType(InputType.TYPE_CLASS_TEXT);

        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(fname);
        layout.addView(ext);

        alert.setView(layout);
        alert.setMessage("write file name");
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                after_save();
            }
        });
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    name = fname.getText().toString().trim() + ext.getText().toString().trim();
                    title.setText(name);
                    path = SCRIPT_PATH + "/" + name;
                    script = new File(path);
                    save();
                    activity.finish();
                } catch (Exception ee) {
                    Toast.makeText(context, "invalid file name", Toast.LENGTH_LONG).show();
                    ee.printStackTrace();
                    save_as_dialog();
                }
            }
        });
        alert.show();
    }

    private void save_dialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, isDark ? R.style.AlertThemeDark : R.style.AlertThemeLight);
        alert.setTitle("Wait !");
        alert.setIcon(R.drawable.ic_baseline_error_outline_24);
        final boolean is_file_new = (script == null && path.equals(""));
        String msg = is_file_new ? "Do you want to save this file ?" : "Do you want to save changes ?";
        msg = isDark ? "<font style='bold' color='#FFF'>" + msg + "</font>" : "<font style='bold' color='#000'>" + msg + "</font>";
        alert.setMessage(Html.fromHtml(msg));
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (is_file_new) save_as_dialog();
                else save();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                after_save();
            }
        });
        alert.show();
    }

    private void after_save() {
        int after_save = AFTER_SAVE;
        AFTER_SAVE = -1;
        switch (after_save) {
            case SHR:
                shareScript();
                break;
            case LIS:
                break;
            case NEW:
                newScript();
                break;
            case SAV:
                break;
            case END:
                activity.finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }

    private void shareScript() {
        if (script == null) return;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.fromFile(script);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("text/*");
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.app_name)));
    }

    public void newScript() {
        editor.setText("");
        title.setText("untitled");
        script = null;
        name = "";
        path = "";
    }

    public void setEditorSettings() {
        SharedPreferences.Editor sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit();
        sharedPrefEditor.putInt("font_size", font_size);
        sharedPrefEditor.putBoolean("show_lines", show_lines);
        if (script != null && script.exists()) {
            sharedPrefEditor.putString("path", path);
            sharedPrefEditor.putString("name", name);
        } else {
            sharedPrefEditor.putString("path", "");
            sharedPrefEditor.putString("name", "");
        }
        sharedPrefEditor.commit();
    }

    private void getEditorSettings() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        font_size = sharedPref.getInt("font_size", 14);
        show_lines = sharedPref.getBoolean("show_lines", false);

        show_lines(show_lines);
        lines.setTextSize(TypedValue.COMPLEX_UNIT_DIP, font_size);
        editor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, font_size);

        path = sharedPref.getString("path", "");
        name = sharedPref.getString("name", "");
        if (!path.equals("")) script = new File(path);
    }
}
