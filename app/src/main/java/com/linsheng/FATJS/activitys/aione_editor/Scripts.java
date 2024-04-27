package com.linsheng.FATJS.activitys.aione_editor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Scripts {

    public static boolean delete(String name) {
        File file = new File(name);
        if (!file.exists()) return false;
        try {
            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void rename(String name, String new_name) {
        File file = new File(name);
        File new_file = new File(new_name);
        if (!file.exists()) return;
        try {
            file.renameTo(new_file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(String name) {
        File file = new File(name);
        if (!file.exists())
            return "";
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            byte[] byt = new byte[dis.available()];
            dis.readFully(byt);
            dis.close();
            String content = new String(byt, 0, byt.length);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getStackTrace().toString();
        }
    }

    public static boolean write(String name, String data) {
        File file = new File(name);
        try {
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.append(data);
            writer.flush();
            writer.close();
            out.close();
            return true;
        } catch (Exception e) {
            file.delete();
            e.printStackTrace();
            return false;
        }
    }

}
