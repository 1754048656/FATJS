package com.linsheng.FATJS.ui.home;

import static com.linsheng.FATJS.config.GlobalVariableHolder.PATH;
import static com.linsheng.FATJS.config.GlobalVariableHolder.context;
import static com.linsheng.FATJS.config.GlobalVariableHolder.isRunning;
import static com.linsheng.FATJS.config.GlobalVariableHolder.isStop;
import static com.linsheng.FATJS.config.GlobalVariableHolder.killThread;
import static com.linsheng.FATJS.node.AccUtils.moveFloatWindow;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linsheng.FATJS.R;
import com.linsheng.FATJS.aione_editor.EditorActivity;
import com.linsheng.FATJS.aione_editor.Scripts;

import com.linsheng.FATJS.config.GlobalVariableHolder;
import com.linsheng.FATJS.databinding.FragmentHomeBinding;
import com.linsheng.FATJS.node.TaskBase;
import com.linsheng.FATJS.utils.ExceptionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myRecyclerAdapter;
    private ArrayList<String> list = new ArrayList<>();
    private FragmentHomeBinding binding;

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
        getFileList(1);

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        printLogMsg("HomeFragment onStart", 0);
        getFileList(0);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
        private ArrayList<String> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView file_name;
            public ImageButton rename_script, delete_script, run_script;

            public MyViewHolder(View view) {
                super(view);
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
        public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, int position) {
            final String name = list.get(position);
            holder.file_name.setText(name);
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
                @RequiresApi(api = Build.VERSION_CODES.O)
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

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private void getFileList(int show) {
        list.clear();
        File f = new File(EditorActivity.scripts_path);
        if (!f.exists()) {
//            if (show == 1)
//                Toast.makeText(context, "there are no files created", Toast.LENGTH_SHORT).show();
//            finish();
        }

        final String files[] = f.list();
        if (files == null || files.length == 0) {
            if (show == 1)
                Toast.makeText(context, "there are no files", Toast.LENGTH_SHORT).show();
            //finish();
        } else {
            for (String name : files) {
                if (name.endsWith(".js")
                        || name.endsWith(".txt")
                        || name.endsWith(".py")
                        || name.endsWith(".java")
                        || name.endsWith(".class")
                        || name.endsWith(".ini")
                        || name.endsWith(".xml")
                        || name.endsWith(".cpp")
                        || name.endsWith(".c")
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
        Toast.makeText(context, "open " + name, Toast.LENGTH_SHORT).show();
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void runScript(String name) {
        String script_path = Environment.getExternalStorageDirectory() + PATH + name;
        if (!isAccessibilityServiceOn()){
            Toast.makeText(context, "请开启无障碍服务", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        // 判断是否有任务正在执行
        if (isRunning || threadList.size() > 0) {
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

    // 判断本程序的无障碍服务是否已经开启
    public Boolean isAccessibilityServiceOn() {
        try{
            String packageName = context.getPackageName();
            String service = packageName + "/" + packageName + ".MyAccessibilityService";
            int enabled = Settings.Secure.getInt(GlobalVariableHolder.context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
            if (enabled == 1) {
                String settingValue = Settings.Secure.getString(GlobalVariableHolder.context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (settingValue != null) {
                    splitter.setString(settingValue);
                    while (splitter.hasNext()) {
                        String accessibilityService = splitter.next();
                        if (accessibilityService.equals(service)) {
                            return true;
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private void shareScript(final String mpath) {
        if (mpath == "") return;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri uri = Uri.fromFile(new File(mpath));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
    }
}