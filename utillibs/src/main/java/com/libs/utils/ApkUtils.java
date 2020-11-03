package com.libs.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Created by qlang on 2017/6/27.
 */
public class ApkUtils {
    private static final String PATH_64_LIB_FILE = "/system/lib64/libc.so";

    private static boolean is64LibSystem() {
        File file = new File(PATH_64_LIB_FILE);
        return file.isFile() && file.exists();
    }

    /**
     * 安装
     *
     * @param apkPath
     * @param context
     * @return
     */
    public static boolean install(@NonNull String apkPath, @NonNull Context context) {
        // 先判断手机是否有root权限
        if (hasRootPerssion()) {
            // 有root权限，利用静默安装实现
            return silentInstall(apkPath);
        } else {
            // 没有root权限，利用意图进行安装
            File file = new File(apkPath);
            if (!file.exists())
                return false;
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            context.startActivity(intent);
            return true;
        }
    }

    public static boolean installByAction(@NonNull String apkPath, @NonNull Context context) {
        File file = new File(apkPath);
        if (!file.exists())
            return false;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }

        context.startActivity(intent);
        return true;
    }

    /**
     * 卸载
     *
     * @param packageName
     * @param context
     * @return
     */
    public static boolean uninstall(@NonNull String packageName, @NonNull Context context) {
        if (hasRootPerssion()) {
            // 有root权限，利用静默卸载实现
            return silentUninstall(packageName);
        } else {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(uninstallIntent);
            return true;
        }
    }

    /**
     * 判断手机是否有root权限
     *
     * @return
     */
    private static boolean hasRootPerssion() {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 静默安装
     *
     * @param apkPath
     * @return
     */
    private static boolean silentInstall(@NonNull String apkPath) {
        PrintWriter writer = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            writer = new PrintWriter(process.getOutputStream());
            writer.println("chmod 777 " + apkPath);
            if (!is64LibSystem())
                writer.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            writer.println("pm install -r " + apkPath);
            writer.println("exit");
            writer.flush();
            writer.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 静默卸载
     *
     * @param packageName
     * @return
     */
    private static boolean silentUninstall(@NonNull String packageName) {
        PrintWriter writer = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            writer = new PrintWriter(process.getOutputStream());
            if (!is64LibSystem())
                writer.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            writer.println("pm uninstall " + packageName);
            writer.flush();
            writer.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 启动app
     * com.exmaple.client/.MainActivity
     * com.exmaple.client/com.exmaple.client.MainActivity
     *
     * @param packageName
     * @param activityName
     * @return
     */
    public static boolean startApp(@NonNull String packageName, @NonNull String activityName) {
        boolean isSuccess = false;
        String cmd = "am start -n " + packageName + "/" + activityName + " \n";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return isSuccess;
    }


    private static boolean returnResult(int value) {
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }

    public static PackageInfo getPackageInfo(Context context, String applicationId) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            if (packageInfos != null)
                for (PackageInfo info : packageInfos) {
                    if (info != null) {
                        if (applicationId.equals(info.packageName)) {
                            return info;
                        }
                    }
                }
        }
        return null;
    }
}
