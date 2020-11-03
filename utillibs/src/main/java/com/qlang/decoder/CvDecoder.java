package com.qlang.decoder;

import android.graphics.Rect;
import androidx.annotation.Nullable;

/**
 * @author Created by qlang on 2018/12/4.
 */

public class CvDecoder {
    public static final int NV21_TO_RGBA = 0x10;
    public static final int NV21_TO_RGB = 0x11;
    public static final int NV21_TO_BGRA = 0x12;
    public static final int NV21_TO_BGR = 0x13;

    public static final int RGBA_TO_I420 = 0x14;
    public static final int RGB_TO_I420 = 0x15;
    public static final int RGBA_TO_YV12 = 0x16;
    public static final int RGB_TO_YV12 = 0x17;
    public static final int BGR_TO_I420 = 0x18;
    public static final int BGRA_TO_I420 = 0x19;
    public static final int BGRA_TO_YV12 = 0x20;
    public static final int BGR_TO_YV12 = 0x21;

    static {
        try {
            System.loadLibrary("cv_util");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private CvDecoder() {
    }

    @Nullable
    public static byte[] convertYUV(byte[] data, @Nullable Rect rect, int width, int height, int format) {
        if (null == data || width <= 0 || height <= 0) return null;
        if (rect != null && (rect.left < 0 || rect.right <= 0 || rect.right
                <= rect.left || rect.top < 0 || rect.bottom <= 0 || rect.bottom <= rect.top))
            return null;
        switch (format) {
            case NV21_TO_RGBA:
            case NV21_TO_RGB:
            case NV21_TO_BGRA:
            case NV21_TO_BGR:
                break;
            default:
                return null;
        }
        return n_convert_yuv(data, rect, width, height, format);
    }

    @Nullable
    public static byte[] convertRGB(byte[] data, int width, int height, int format) {
        if (null == data || width <= 0 || height <= 0) return null;
        switch (format) {
            case RGBA_TO_I420:
            case RGB_TO_I420:
            case RGBA_TO_YV12:
            case RGB_TO_YV12:
            case BGR_TO_I420:
            case BGRA_TO_I420:
            case BGRA_TO_YV12:
            case BGR_TO_YV12:
                break;
            default:
                return null;
        }
        return n_convert_rgb(data, width, height, format);
    }

    public static void yv12ToNV21(byte[] inData, byte[] outData, int width, int height) {
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;
        final int tempFrameSize = frameSize * 5 / 4;

        System.arraycopy(inData, 0, outData, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            outData[frameSize + i * 2] = inData[frameSize + i]; // Cb (U)
            outData[frameSize + i * 2 + 1] = inData[tempFrameSize + i]; // Cr (V)
        }
    }

    public static void i420ToNV21(byte[] inData, byte[] outData, int width, int height) {
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;
        final int tempFrameSize = frameSize * 5 / 4;

        System.arraycopy(inData, 0, outData, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            outData[frameSize + i * 2] = inData[tempFrameSize + i]; // Cb (U)
            outData[frameSize + i * 2 + 1] = inData[frameSize + i]; // Cr (V)
        }
    }

    private static native byte[] n_convert_yuv(byte[] data, Rect rect, int width, int height, int format);

    private static native byte[] n_convert_rgb(byte[] data, int width, int height, int format);
}
