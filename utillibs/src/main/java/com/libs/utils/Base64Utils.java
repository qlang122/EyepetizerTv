package com.libs.utils;

import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Created by qlang on 2017/5/12.
 */

public class Base64Utils {
    private static final int CACHE_SIZE = 1024;

    /**
     * BASE64字符串解码为二进制数据
     *
     * @param base64
     * @return
     */
    @Nullable
    public static byte[] decode(@NonNull String base64) {
        return Base64.decode(base64.getBytes(), Base64.DEFAULT);
    }

    /**
     * 二进制数据编码为BASE64字符串
     *
     * @param bytes
     * @return
     */
    @Nullable
    public static String encode(@NonNull byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 将文件编码为BASE64字符串
     * 大文件慎用，可能会导致内存溢出
     *
     * @param filePath 文件绝对路径
     * @return
     */
    @Nullable
    public static String encodeFile(@NonNull String filePath) {
        return encode(ImageUtils.bitmap2Bytes(BitmapFactory.decodeFile(filePath), 80));
    }

    /**
     * BASE64字符串转回文件
     *
     * @param filePath 文件绝对路径
     * @param base64   编码字符串
     * @throws Exception
     */
    @Nullable
    public static void decodeToFile(@NonNull String filePath, @NonNull String base64) throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     *
     * @param filePath 文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /**
     * <p>
     * 二进制数据写文件
     * </p>
     *
     * @param bytes    二进制数据
     * @param filePath 文件生成目录
     */
    @Nullable
    public static void byteArrayToFile(@NonNull byte[] bytes, @NonNull String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }

}
