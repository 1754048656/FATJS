package com.linsheng.FATJS.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellUtils {

    /**
     * 执行Shell命令，并返回执行结果。
     *
     * @param command 待执行的命令，例如 "ls -l" 或 "su -c ls" 等
     * @return ShellResult 对象，包含退出码、标准输出和错误输出
     */
    public static ShellResult execCommand(String command) {
        ShellResult result = new ShellResult();
        Process process = null;
        BufferedReader reader = null;
        BufferedReader errorReader = null;
        try {
            process = Runtime.getRuntime().exec(command);
            // 分别获取标准输出和错误输出流
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder successOutput = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                successOutput.append(line).append("\n");
            }

            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            result.exitCode = process.waitFor();
            result.successMsg = successOutput.toString();
            result.errorMsg = errorOutput.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception e) { }
            }
            if (errorReader != null) {
                try { errorReader.close(); } catch (Exception e) { }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
    
    /**
     * 执行Shell命令，并直接返回标准输出字符串。
     *
     * @param command 待执行的命令
     * @return 标准输出字符串，如果有错误输出则可以根据需要进行处理
     */
    public static String execCommandAndGetOutput(String command) {
        ShellResult result = execCommand(command);
        return result.successMsg;
    }
    
    /**
     * 用于封装Shell命令执行结果的内部类
     */
    public static class ShellResult {
        public int exitCode;
        public String successMsg;
        public String errorMsg;
        
        @Override
        public String toString() {
            return "Exit Code: " + exitCode + "\nSuccess Output:\n" + successMsg + "\nError Output:\n" + errorMsg;
        }
    }
}
