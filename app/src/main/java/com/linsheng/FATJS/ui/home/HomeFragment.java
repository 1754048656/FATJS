package com.linsheng.FATJS.ui.home;

import static com.linsheng.FATJS.config.GlobalVariableHolder.*;
import static com.linsheng.FATJS.node.AccUtils.isAccessibilityServiceOn;
import static com.linsheng.FATJS.node.AccUtils.moveFloatWindow;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linsheng.FATJS.R;
import com.linsheng.FATJS.activitys.aione_editor.EditorActivity;
import com.linsheng.FATJS.activitys.aione_editor.Scripts;

import com.linsheng.FATJS.databinding.FragmentHomeBinding;
import com.linsheng.FATJS.node.TaskBase;
import com.linsheng.FATJS.utils.ExceptionUtil;
import com.linsheng.FATJS.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;
    private ArrayList<String> list = new ArrayList<>();
    private FragmentHomeBinding binding;
    private int checkedPosition = -1;
    private boolean __isOpenFloatWin = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerAdapter = new MyRecyclerAdapter(list);
        recyclerView.setAdapter(myRecyclerAdapter);

        printLogMsg("HomeFragment onCreateView", 0);
        getFileList(1);
        // 恢复选中状态
        int checkedPosition = myRecyclerAdapter.getCheckedPosition();
        if (checkedPosition != -1) {
            myRecyclerAdapter.notifyItemChanged(checkedPosition);
        }

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        getFileList(0);
        // 备份记录悬浮窗是否打开
        __isOpenFloatWin = isOpenFloatWin;
        moveFloatWindow("隐藏");
    }

    public View aboutBtn;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        aboutBtn = getActivity().findViewById(R.id.about_btn);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClick();
            }
        });
    }

    public void btnClick() {
        printLogMsg("btnClick", 0);
        startActivity(new Intent(getActivity(), EditorActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        printLogMsg("HomeFragment onResume", 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        printLogMsg("HomeFragment onPause", 0);
        // 保存选中状态
        int checkedPosition = myRecyclerAdapter.getCheckedPosition();
        myRecyclerAdapter.saveCheckedPosition(checkedPosition);
        /*if (DEV_MODE && __isOpenFloatWin) {
            moveFloatWindow("打开");
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
        private ArrayList<String> list;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public RadioButton radio_button;
            public TextView file_name;
            public ImageButton rename_script, delete_script, run_script;

            public MyViewHolder(View view) {
                super(view);
                radio_button = view.findViewById(R.id.radio_button);
                file_name = view.findViewById(R.id.file_name);
                run_script = view.findViewById(R.id.run_script);
                delete_script = view.findViewById(R.id.delete_script);
                rename_script = view.findViewById(R.id.rename_script);
            }
        }

        public MyRecyclerAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item, parent, false);
            return new MyRecyclerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final String name = list.get(position);
            holder.file_name.setText(name);
            holder.radio_button.setChecked(position == checkedPosition);
            // 根据 file_name 判断是否选中
            if (name.equals(checkedFileName)) {
                holder.radio_button.setChecked(true);
                checkedPosition = position;
            }
            holder.radio_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkedPosition != position) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = position;
                        checkedFileName = name;
                        notifyItemChanged(checkedPosition);
                        saveConfig();
                        printLogMsg("已选中 " + checkedFileName, 0);
                        Toast.makeText(context, "已选中 " + checkedFileName, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.file_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFile(name);
                }
            });
            holder.rename_script.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    renameScript(name);
                }
            });
            holder.delete_script.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteScript(name);
                }
            });
            holder.run_script.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!name.endsWith(".js")) {
                        Toast.makeText(context, name + " is not a js file", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    printLogMsg("run script " + name, 0);
                    runScript(name);
                }
            });
        }

        // 保存选中状态
        public void saveCheckedPosition(int position) {
            checkedPosition = position;
        }

        // 恢复选中状态
        public int getCheckedPosition() {
            return checkedPosition;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getFileList(int show) { // 获取并刷新脚本文件列表
        list.clear();
        File f = new File(EditorActivity.scripts_path);
        if (!f.exists()) {
//            if (show == 1)
//                Toast.makeText(context, "there are no files created", Toast.LENGTH_SHORT).show();
//            finish();
        }

        final String[] files = f.list();
        if (files == null || files.length == 0) {
            if (show == 1)
                Toast.makeText(context, "there are no files", Toast.LENGTH_SHORT).show();
            //finish();
        } else {
            for (String name : files) {
                if (name.endsWith(".js")
                        || name.endsWith(".txt")
                        //|| name.endsWith(".json")
                        || name.endsWith(".py")
                        || name.endsWith(".java")
                        || name.endsWith(".class")
                        || name.endsWith(".ini")
                        || name.endsWith(".xml")
                        || name.endsWith(".cpp")
                        || name.endsWith(".c")
                        || name.endsWith(".conf")
                ) {
                    list.add(name);
                }
            }
        }
        myRecyclerAdapter.notifyDataSetChanged();
    }

    private void openFile(String name) {
        Intent editor_intent = new Intent(context, EditorActivity.class);
        editor_intent.putExtra("name", name);
        editor_intent.putExtra("path", EditorActivity.scripts_path + "/" + name);
        startActivity(editor_intent);
    }

    private void renameScript(final String name) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(name);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ad.setView(input);
        ad.setMessage("write new file name");
        ad.setNegativeButton("Save", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String scriptName = input.getText().toString().trim();
                if (scriptName.isEmpty()) renameScript(name);
                else {
                    Scripts.rename(EditorActivity.scripts_path + "/" + name, EditorActivity.scripts_path + "/" + scriptName);
                    list.set(list.indexOf(name), scriptName);
                    myRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
        ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        ad.show();
    }

    private void deleteScript(String name) {
        Scripts.delete(EditorActivity.scripts_path + "/" + name);
        Toast.makeText(context, name + " deleted", Toast.LENGTH_SHORT).show();
        getFileList(1);
    }

    private List<Thread> threadList = new ArrayList<>();
    private void runScript(String name) {
        String script_path = Environment.getExternalStorageDirectory() + PATH + name;
        if (!isAccessibilityServiceOn()){
            printLogMsg("请开启无障碍服务", 0);
            Toast.makeText(context, "请开启无障碍服务", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        // 判断是否有任务正在执行
        if (isRunning || !threadList.isEmpty()) {
            killThread = true;
            printLogMsg("有任务正在执行，已强制停止", 0);
            Toast.makeText(context, "有任务正在执行，已强制停止，请打开悬浮窗", Toast.LENGTH_SHORT).show();
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TaskBase taskBase = new TaskBase();
                    taskBase.initJavet(script_path);
                }catch (Exception e) {
                    printLogMsg(ExceptionUtil.toString(e));
                }finally {
                    threadList = new ArrayList<>();
                }
            }
        });
        threadList.add(thread);
        thread.start();
    }

    private void shareScript(final String mpath) {
        if (StringUtils.isEmpty(mpath)) return;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.fromFile(new File(mpath));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
    }
}