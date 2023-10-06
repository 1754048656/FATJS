package com.linsheng.FATJS.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
    public ExceptionUtil() {
    }

    public static String toString(Throwable var0) {
        StringWriter var1 = new StringWriter();
        PrintWriter var2 = new PrintWriter(var1);
        var0.printStackTrace(var2);
        return var1.toString();
    }
}