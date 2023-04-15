package com.linsheng.FATJS.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
    public ExceptionUtil() {
    }

    public static String toString(Throwable var0) throws ExitException {
        StringWriter var1 = new StringWriter();
        PrintWriter var2 = new PrintWriter(var1);
        var0.printStackTrace(var2);
        if (var1.toString().contains("任务销毁")) {
            throw new ExitException("0");
        }
        return var1.toString();
    }
}