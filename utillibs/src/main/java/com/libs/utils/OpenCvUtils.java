package com.libs.utils;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * @author Created by qlang on 2018/10/8.
 */

public class OpenCvUtils {
    static {
        System.loadLibrary("opencv_java");
    }

    @Nullable
    public static boolean isBlur(@NonNull Bitmap image, int offset) {
        if (image == null) return true;

        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY); // 图像灰度化

        Bitmap destImage;
        destImage = Bitmap.createBitmap(image);
        Mat dst2 = new Mat();
        Utils.bitmapToMat(destImage, dst2);
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, CvType.CV_8UC1);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U); // 拉普拉斯变换
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, CvType.CV_8UC1);

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(laplacianImage8bit, bmp);
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight()); // bmp为轮廓图

        int maxLap = -16777216; // 16m
        for (int pixel : pixels) {
            if (pixel > maxLap) maxLap = pixel;
        }
//        int userOffset = -3881250; // 界线（严格性）降低一点
        int soglia = -6118750 + offset; // -6118750为广泛使用的经验值
        soglia += 6118750 + offset;
        maxLap += 6118750 + offset;

//        Log.e("OpenCvUtils", "maxLap= " + maxLap + "(清晰范围:" + soglia + "~"
//                + (6118750 + offset) + ")" + (maxLap <= soglia ? "模糊" : "清晰"));
        return maxLap <= soglia;
    }
}
