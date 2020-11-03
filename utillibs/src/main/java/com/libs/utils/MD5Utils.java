package com.libs.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String md5(String str) {
        if (TextUtils.isEmpty(str)) return "";

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] m = md5.digest(byteArray);

        return getString(m);
    }

    private static String getString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            int val = ((int) aB) & 0xFF;
            if (val < 16) sb.append("0");
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }
}
