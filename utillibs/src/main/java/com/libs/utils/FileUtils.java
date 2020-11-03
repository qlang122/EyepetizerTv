package com.libs.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @author Created by qlang on 2017/5/15.
 */

public class FileUtils {
    /**
     * 复制asset文件到指定目录
     *
     * @param oldPath asset下的路径
     * @param newPath SD卡下保存路径
     */
    public static void copyAssets(@NonNull Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                if (!file.exists()) file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getClass().getClassLoader().getResourceAsStream(oldPath);
                File file = new File(newPath);
                if (file.exists()) file.delete();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while (null != is && (byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                if (null != is) is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建指定的文件路径
     *
     * @param path
     */
    public static void createFolder(String path) {
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 文件路径转换成Uri
     *
     * @param filePath
     * @return 输出格式以“file:///”开头
     */
    public static Uri path2Uri(String filePath) {
        return Uri.fromFile(new File(filePath));
    }

    /**
     * 获取字符串
     *
     * @param inputStream
     * @return
     */
    public static String getString(@NonNull InputStream inputStream) {
        if (null == inputStream) return "";
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        if (null == inputStreamReader) return "";
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取字符串(按行读取)
     *
     * @param inputStream
     * @return 返回读取后的一行一行文本集合
     */
    public static ArrayList<String> getStrings(@NonNull InputStream inputStream) {
        if (null == inputStream) return null;
        InputStreamReader inputStreamReader = null;
        ArrayList<String> str = new ArrayList<>();
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                str.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param path
     * @param fileName
     * @return
     */
    public static File createFile(String path, String fileName) {
        File file = new File(path + File.separator + fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File writeFile(String path, String fileName, @NonNull InputStream is) {
        return writeFile(path, fileName, is, true);
    }

    /**
     * 将一个inputStream里面的数据写到文件中
     *
     * @param path
     * @param fileName
     * @param is
     * @return
     */
    public static File writeFile(String path, String fileName, @NonNull InputStream is, boolean isAppend) {
        if (is == null) return null;

        File file = createFile(path, fileName);
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            fos = new FileOutputStream(file, isAppend);
            bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (bis != null) bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 将一个inputStream里面的数据写到文件中
     *
     * @param fos
     * @param is
     * @return
     */
    public static boolean writeFile(@NonNull OutputStream fos, @NonNull InputStream is) {
        if (is == null || fos == null)
            return false;

        boolean flag = false;

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (fos != null) fos.close();
                if (bis != null) bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 获取Uri对应的文件的路径,兼容4.4。  4.4之后需增加权限  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     */
    public static String getPath(@NonNull Context context, @NonNull Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) { // 【DocumentProvider】
            if (isExternalStorageDocument(uri)) { // 【ExternalStorageProvider】
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
//                if ("primary".equalsIgnoreCase(type))
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) { // 【DownloadsProvider】
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) { // 【MediaProvider】
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                else if ("video".equals(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                else if ("audio".equals(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // 【MediaStore (and general)】
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // 【File】
            return uri.getPath();
        }
        return "";
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(@NonNull Context context, @NonNull Uri uri, String selection, @NonNull String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(@NonNull Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(@NonNull Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(@NonNull Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 拷贝文件（包含所有子目录及文件）
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);

        if (!root.exists()) return -1;

        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        if (!targetDir.exists()) targetDir.mkdirs();

        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory()) {//如果当前项为子目录 进行递归
                copy(currentFiles[i].getPath() + "/", toFile + "/" + currentFiles[i].getName() + "/");
            } else {//如果当前项为文件则进行文件拷贝
                copyFile(currentFiles[i].getPath() + "/", toFile + "/" + currentFiles[i].getName());
            }
        }
        return 0;
    }

    /**
     * 要复制的目录下的所有非子目录(文件夹)文件拷贝
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copyFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void deleteFiles(@NonNull File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        for (File file : dir.listFiles()) {
            if (file.isFile()) file.delete(); // 删除所有文件
            else if (file.isDirectory()) deleteFiles(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
}
