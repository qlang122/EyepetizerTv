package com.libs.utils

import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import java.io.ByteArrayOutputStream
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import java.lang.Exception

/**
 * @author Created by qlang on 2017/12/11.
 */
object ImageUtils {
    /**
     * 把Bitmap转Byte
     */
    @JvmStatic
    fun bitmap2Bytes(bm: Bitmap): ByteArray {
        return bitmap2Bytes(bm, 100)
    }

    @JvmStatic
    fun bitmap2Bytes(bm: Bitmap, quality: Int): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(CompressFormat.PNG, quality, baos)
        val bytes = baos.toByteArray()
        try {
            baos.close()
        } catch (e: Exception) {
        }
        return bytes
    }

    /**
     *  byte转bitmap
     */
    @JvmStatic
    fun bytes2Bitmap(bytes: ByteArray): Bitmap? {
        if (bytes.isNotEmpty()) return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return null
    }

    /**
     * Bitmap---->Drawable
     */
    @JvmStatic
    fun bitmap2Drawable(bitmap: Bitmap): Drawable {
        return BitmapDrawable(bitmap)
    }

    /**
     * Drawable → Bitmap
     */
    @JvmStatic
    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 将bitmap转换成base64字符串

     * @param bitmap
     * *
     * @return base64 字符串
     */
    @JvmStatic
    fun bitmap2String(bitmap: Bitmap, bitmapQuality: Int): String {
        // 将Bitmap转换成字符串
        var string: String? = null
        val stream = ByteArrayOutputStream()
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, bitmapQuality, stream)
            string = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            stream.close()
        }
        return string ?: ""
    }

    /**
     * 选择变换
     *
     * @param bitmap 原图
     * @param degree 旋转角度，可正可负
     * @return 旋转后的图片
     */
    @JvmStatic
    fun rotateBitmap(bitmap: Bitmap?, degree: Float): Bitmap? {
        if (bitmap == null) return null

        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        matrix.setRotate(degree)
        // 围绕原地进行旋转
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
    }
}