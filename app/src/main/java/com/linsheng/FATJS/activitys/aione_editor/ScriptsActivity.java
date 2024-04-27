package com.linsheng.FATJS.activitys.aione_editor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linsheng.FATJS.R;

import java.io.File;
import java.util.ArrayList;

public class ScriptsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myRecyclerAdapter;
    private Spinner script_type;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scripts);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerAdapter = new MyRecyclerAdapter(list);
        recyclerView.setAdapter(myRecyclerAdapter);
        getFileList();
    }

    private void getFileList() {
        list.clear();
        File f = new File(EditorActivity.scripts_path);
        if (!f.exists()) {
            Toast.makeText(this, "there are no files created", Toast.LENGTH_LONG).show();
            finish();
        }

        final String files[] = f.list();
        if (files == null || files.length == 0) {
            Toast.makeText(this, "there are no files", Toast.LENGTH_LONG).show();
            finish();
        } else {
            for (int i = 0; i < files.length; i++) {
                String name = files[i];
                list.add(name);
            }
        }
        myRecyclerAdapter.notifyDataSetChanged();
    }

    private void openFile(String name) {
        Intent editor_intent = new Intent(this, EditorActivity.class);
        editor_intent.putExtra("name", name);
        editor_intent.putExtra("path", EditorActivity.scripts_path + "/" + name);
        startActivity(editor_intent);
    }

    private void renameScript(final String name) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
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
        Toast.makeText(getApplicationContext(), name + " deleted", Toast.LENGTH_SHORT).show();
        getFileList();
    }

    private void runScript(String name) {
        shareScript(EditorActivity.scripts_path + "/" + name);
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


    public void about(View v) {
        //startActivity(new Intent(this, AboutActivity.class));
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
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
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
                @Override
                public void onClick(View view) {
                    //runScript(name);
                    //AccUtils.printLogMsg("run script");
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
