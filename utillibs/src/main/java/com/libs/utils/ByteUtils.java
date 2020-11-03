package com.libs.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Created by qlang on 2017/12/20.
 */
public class ByteUtils {
    public static int removeBitVal(int value, int index) {
        int bit = 1 << index;
        int mark = 0;
        mark = (~mark) ^ bit;
        value &= mark;
        return value;
    }

    public static int setBitVal(int value, int index) {
        int bit = 1 << index;
        value |= bit;
        return value;
    }

    public static int toggleBitVal(int value, int index) {
        int bit = 1 << index;
        if ((value & bit) == bit) {
            value = removeBitVal(value, index);
        } else {
            value = setBitVal(value, index);
        }
        return value;
    }

    @Nullable
    public static byte[] int2Bytes(@NonNull byte[] datas, int startPos, int value) {
        if (datas == null || datas.length == 0 || datas.length <= startPos || startPos < 0) {
            return datas;
        }
        datas[startPos++] = (byte) ((value >> 24) & 0xFF);
        datas[startPos++] = (byte) ((value >> 16) & 0xFF);
        datas[startPos++] = (byte) ((value >> 8) & 0xFF);
        datas[startPos] = (byte) (value & 0xFF);
        return datas;
    }

    @Nullable
    public static byte[] short2Bytes(@NonNull byte[] datas, int startPos, int value) {
        if (datas == null || datas.length == 0 || datas.length <= startPos || startPos < 0) {
            return datas;
        }
        datas[startPos++] = (byte) ((value >> 8) & 0xFF);
        datas[startPos] = (byte) (value & 0xFF);
        return datas;
    }

    public static int bytes2Short(@NonNull byte[] datas, int startPos) {
        int values = 0;
        if (datas == null || datas.length == 0 || datas.length <= startPos || startPos < 0) {
            return 0;
        }
        values = datas[startPos++] & 0xFF;
        values = (values << 8) & 0xFF00;
        values = values | (datas[startPos] & 0xFF);
        return values;
    }

    public static int bytes2Int(@NonNull byte[] datas, int startPos) {
        int values = 0;
        if (datas == null || datas.length == 0 || datas.length <= startPos || startPos < 0) {
            return 0;
        }
        values = datas[startPos] & 0xFF;
        values = (values << 24) & 0xFF000000;
        values = values | ((datas[startPos + 1] & 0xFF) << 16)
                | ((datas[startPos + 2] & 0xFF) << 8)
                | ((datas[startPos + 3] & 0xFF));
        return values;
    }

    public static void long2Bytes(@NonNull byte[] b, long x, int offset) {
        if (b.length - offset >= 8) {
            b[offset + 7] = (byte) (x >> 56);
            b[offset + 6] = (byte) (x >> 48);
            b[offset + 5] = (byte) (x >> 40);
            b[offset + 4] = (byte) (x >> 32);
            b[offset + 3] = (byte) (x >> 24);
            b[offset + 2] = (byte) (x >> 16);
            b[offset + 1] = (byte) (x >> 8);
            b[offset] = (byte) (x);
        }
    }

    /**
     * @param b
     * @param offset
     * @return
     */
    public static long bytes2Long(@NonNull byte[] b, int offset) {
        long x = 0;
        if (b.length - offset >= 8) {
            x = ((((long) b[offset + 7] & 0xff) << 56)
                    | (((long) b[offset + 6] & 0xff) << 48)
                    | (((long) b[offset + 5] & 0xff) << 40)
                    | (((long) b[offset + 4] & 0xff) << 32)
                    | (((long) b[offset + 3] & 0xff) << 24)
                    | (((long) b[offset + 2] & 0xff) << 16)
                    | (((long) b[offset + 1] & 0xff) << 8)
                    | (((long) b[offset] & 0xff)));
        }
        return x;
    }

    /**
     * 0x48 0x33 --> H3
     *
     * @param b
     * @return
     */
    public static String bytes2String(@NonNull byte[] b) {
        StringBuffer result = new StringBuffer("");
        for (byte aB : b) {
            char ch = (char) (aB & 0xff);
            if (ch == 0) break;
            result.append(ch);
        }
        return result.toString();
    }

    @Nullable
    public static String bytes2HexString(@NonNull byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    @Nullable
    public static byte[] hexString2bytes(@NonNull String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    @Nullable
    public static String bytes2String(@NonNull byte[] data, int start, int length, int targetLength) {
        long number = 0;
        if (data == null || data.length < start + length) {
            return null;
        }
        for (int i = 1; i <= length; i++) {
            number *= 0x100;
            number += (data[start + length - i] & 0xFF);
        }
        return String.format("%0" + targetLength + "d", number);
    }

    @NonNull
    public static byte[] getBytes(@NonNull String str) {
        return str.getBytes();
    }

    public static byte[] readStream(@NonNull InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

}
