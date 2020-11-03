package com.libs.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lg {
    private static String customTagPrefix = "";
    static final boolean ENABLE = true;
    static final String TAG = "Lg";
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final boolean DEBUG_SAVE_LOG = false;

    private static String generateTag() {
        String tag = "%s.%s(line: %d)";
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return "Lg: " + tag;
    }

    private static String generateTag(String _tag) {
        String tag = "%s.%s(line: %d)";
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ": " + tag;
        return _tag + ": " + tag;
    }

    public static void d(String tag, String msg) {
        if (!ENABLE) return;
        if (msg == null) return;
        Log.d(generateTag(tag), msg);
    }

    public static void d(String tag, boolean logFlag, String msg) {
        if (!ENABLE) return;
        if (!logFlag) return;
        if (msg == null) return;
        Log.d(generateTag(tag), msg);
    }

    public static void v(String tag, String msg) {
        if (!ENABLE) return;
        if (msg == null) return;
        Log.v(generateTag(tag), msg);
    }

    public static void v(String tag, boolean logFlag, String msg) {
        if (!ENABLE) return;
        if (!logFlag) return;
        if (msg == null) return;
        Log.v(generateTag(tag), msg);
    }

    public static void w(String tag, String msg) {
        if (!ENABLE) return;
        Log.w(generateTag(tag), msg);
    }

    public static void w(String tag, boolean logFlag, String msg) {
        if (!ENABLE) return;
        if (!logFlag) return;
        if (msg == null) return;
        Log.w(generateTag(tag), msg);
    }

    public static void i(String tag, String msg) {
        if (!ENABLE) return;
        Log.i(generateTag(tag), msg);
        if (DEBUG_SAVE_LOG)
            save(tag, "", "", msg);
    }

    public static void i(String tag, int code, String msg) {
        if (!ENABLE) return;
        i(generateTag(tag), "Code: " + code + " " + msg);
        if (DEBUG_SAVE_LOG)
            save(tag, "", "", msg);
    }

    public static void e(String tag, int code, String msg) {
        if (!ENABLE) return;
        e(generateTag(tag), "Code: " + code + " " + msg);
        if (DEBUG_SAVE_LOG)
            save(tag, "", "", msg);
    }

    public static void e(String tag, String msg) {
        if (!ENABLE) return;
        Log.e(generateTag(tag), msg);
        if (DEBUG_SAVE_LOG)
            save(tag, "", "", msg);
    }

    public static void i(String tag, String msg, byte[] b) {
        if (!ENABLE) return;
        if (b == null) {
            return;
        }
        Log.i(generateTag(tag), merge(msg, b));
        if (DEBUG_SAVE_LOG)
            save(tag, "", "", msg);
    }

    public static void e(String tag, String msg, byte[] b) {
        if (!ENABLE) return;
        if (b == null) {
            return;
        }
        Log.e(generateTag(tag), merge(msg, b));
        if (DEBUG_SAVE_LOG)
            save(tag, "", "", msg);
    }

    private static String merge(String msg, byte[] b) {
        if (TextUtils.isEmpty(msg)) {
            msg = "";
        }
        int length = b.length;
        byte[] d = null;
        if (length > 200) {
            d = new byte[200];
            msg += "\"byte is too large, only show 200 lenght.\"";
            System.arraycopy(b, 0, d, 0, d.length);
            b = d;
        }

        msg += String.format(" len: %d, bytes: ", length);
        StringBuilder msgBuilder = new StringBuilder(msg);
        for (byte aB : b) {
            msgBuilder.append(String.format("%02X ", aB));
        }
        return msgBuilder.toString();
    }

    public static void e(String tag, boolean logFlag, String msg) {
        if (!ENABLE) return;
        if (!logFlag) return;
        if (msg == null) return;
        Log.e(generateTag(tag), msg);
    }

    public static void l(String tag, String msg) {
        if (!ENABLE) return;
        System.out.println("TAG:" + tag + "/n" + msg);
    }

    public static void l(String tag, boolean logFlag, String msg) {
        if (!ENABLE) return;
        if (!logFlag) return;
        if (msg == null) return;
        System.out.println("TAG:" + tag + "/n" + msg);
    }

    public static void save(final String classname, final String page, final String actioname, final String notes) {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File file = null;
                File logFile = null;
                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "logs");
                    } else {
                        file = new File("mnt" + File.separator + "logs");
                    }
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String time = new SimpleDateFormat("yyyyMMdd").format(new Date());
                    logFile = new File(file.getAbsolutePath() + File.separator + "Log-" + time + ".txt");
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (logFile == null) {
                    return;
                }

                String timer = format.format(new Date(System.currentTimeMillis()));
                StringBuilder builder = new StringBuilder();
                builder.append(timer)
                        .append(" ")
                        .append("")
                        .append(" ")
                        .append("")
                        .append(" ")
                        .append(classname)
                        .append(" ")
                        .append(page)
                        .append(" ")
                        .append(actioname)
                        .append(" ")
                        .append(notes);
                FileOutputStream fos = null;
                OutputStreamWriter fileWriter = null;
                BufferedWriter bufferedWriter = null;

                try {
                    fos = new FileOutputStream(logFile.getAbsolutePath(), true);
                    fileWriter = new OutputStreamWriter(fos);
                    bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(builder.toString());
                    bufferedWriter.write("\r\n");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
