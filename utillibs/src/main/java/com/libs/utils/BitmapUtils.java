package com.libs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Created by qlang on 2018/7/2.
 */

public class BitmapUtils {
    /**
     * 压缩图片
     *
     * @param image
     * @param maxSize 体积大小，单位(k)
     * @return
     */
    public static Bitmap compressImage(@NonNull Bitmap image, int maxSize) {
        ByteArrayInputStream isBm = new ByteArrayInputStream(compressImage(maxSize, image));
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(isBm, null, options1);
    }

    /**
     * 压缩图片
     *
     * @param image
     * @param maxSize 体积大小，单位(k)
     * @return
     */
    @Nullable
    public static byte[] compressImage(int maxSize, @NonNull Bitmap image) {
        if (image == null) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] bytes = baos.toByteArray();
        return compressImage(bytes, maxSize);
    }

    /**
     * 压缩图片
     *
     * @param bytes
     * @param maxSize 体积大小，单位(k)
     * @return
     */
    @Nullable
    public static byte[] compressImage(@NonNull byte[] bytes, int maxSize) {
        if (bytes == null) return null;

        int options = 90;
        Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        int size = bytes.length;
        while (size / 1024 > maxSize) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            temp.compress(Bitmap.CompressFormat.JPEG, options, bao);

            byte[] buf = bao.toByteArray();
            size = buf.length;
            Bitmap bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
            options -= 5;
            if (options <= 0) options = 90;
            temp = bitmap;
            bytes = buf.clone();
        }
        return bytes;
    }

    @Nullable
    public static byte[] resizeCutImage(@NonNull Bitmap bitmap, int maxSize, int maxWidth, int maxHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap _bitmap = null;

        if (w > maxWidth || h > maxHeight) {
            _bitmap = resizeBitmap(bytes, maxWidth, maxHeight);//压缩尺寸
        }
        if (null != _bitmap) {
            byte[] _bytes = bitmap2Bytes(_bitmap);
            if (null != _bytes && _bytes.length / 1024 > maxSize) {
                return compressImage(maxSize, _bitmap);//压缩大小
            } else return _bytes;
        } else if (bytes != null && bytes.length / 1024 > maxSize) {
            return compressImage(bytes, maxSize);
        }
        return bytes;
    }

    @Nullable
    public static Bitmap resizeCutImage(int maxSize, int maxWidth, int maxHeight, @NonNull Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap _bitmap = null;

        if (w > maxWidth || h > maxHeight) {
            _bitmap = resizeBitmap(bytes, maxWidth, maxHeight);//压缩尺寸
        }

        if (null != _bitmap) {
            byte[] _bytes = bitmap2Bytes(_bitmap);
            if (null != _bytes && _bytes.length / 1024 > maxSize) {
                return compressImage(_bitmap, maxSize);//压缩大小
            } else return _bitmap;
        } else if (null != bytes && bytes.length / 1024 > maxSize) {//压缩图片
            return compressImage(bitmap, maxSize);//压缩大小
        }
        return bitmap;
    }

    /**
     * <p>
     * 二进制数据写文件
     * </p>
     *
     * @param bytes    二进制数据
     * @param filePath 文件生成目录
     */
    public static void saveBytes2File(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[1024];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }

    /**
     * 压缩图片尺寸
     *
     * @param buffer
     * @param w      最大宽
     * @param h      最大高
     * @return
     */
    @Nullable
    public static Bitmap resizeBitmap(@NonNull byte[] buffer, int w, int h) {
        if (buffer == null || buffer.length == 0) return null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);

            int minSideLength = Math.min(w, h);
            options.inSampleSize = computeSampleSize(options, minSideLength, w * h);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 压缩尺寸
     *
     * @param path
     * @param w    最大宽
     * @param h    最大高
     * @return
     */
    @Nullable
    public static Bitmap resizeBitmap(String path, int w, int h) {
        if (TextUtils.isEmpty(path)) return null;
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int minSideLength = Math.min(w, h);
            options.inSampleSize = computeSampleSize(options, minSideLength, w * h);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static byte[] bitmap2Bgr(@NonNull Bitmap bitmap) {
        int size = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(size); // Create a new buffer
        bitmap.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[temp.length / 4 * 3]; // Allocate for BGR
        // Copy pixels into place
        for (int i = 0; i < temp.length / 4; i++) {
            pixels[i * 3] = temp[i * 4 + 2];      //B
            pixels[i * 3 + 1] = temp[i * 4 + 1];    //G
            pixels[i * 3 + 2] = temp[i * 4];     //R
        }
        return pixels;
    }

    /**
     * byte[] data保存的是纯RGB的数据，而非完整的图片文件数据
     */
    public static Bitmap rgb2Bitmap(@NonNull byte[] data, int width, int height) {
        int[] argb = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
//                argb[i * width + j] = 255;
                argb[i * width + j] = (argb[i * width + j] << 8) + data[(i * width + j) * 3 + 0]; //+r
                argb[i * width + j] = (argb[i * width + j] << 8) + data[(i * width + j) * 3 + 1]; //+g
                argb[i * width + j] = (argb[i * width + j] << 8) + data[(i * width + j) * 3 + 2]; //+b
            }
        }
        return Bitmap.createBitmap(argb, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap bgr2Bitmap(@NonNull byte[] data, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int row = height - 1, col = width - 1;
        for (int i = data.length - 1; i >= 3; i -= 3) {
            int color = data[i - 2] & 0xFF;
            color += (data[i - 1] << 8) & 0xFF00;
            color += ((data[i]) << 16) & 0xFF0000;
            bmp.setPixel(col--, row, color);
            if (col < 0) {
                col = width - 1;
                row--;
            }
        }
        return bmp;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(@NonNull String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static byte[] base64ToBitmapBytes(@NonNull String base64Data) {
        return Base64.decode(base64Data, Base64.DEFAULT);
    }

    public static String bitmap2Base64(@NonNull Bitmap bitmap) {
        return Base64.encodeToString(bitmap2Bytes(bitmap), Base64.DEFAULT);
    }

    public static String bitmapBytes2Base64(@NonNull byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 把Bitmap转Byte
     */
    @Nullable
    public static byte[] bitmap2Bytes(@NonNull Bitmap bm) {
        return bitmap2Bytes(bm, 100);
    }

    @Nullable
    public static byte[] bitmap2Bytes(@NonNull Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Nullable
    public static Bitmap bytes2Bitmap(@NonNull byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
