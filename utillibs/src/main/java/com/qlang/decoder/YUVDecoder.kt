package com.qlang.decoder

/**
 * @author Created by qlang on 2017/12/14.
 */
object YUVDecoder {
    /**
     * RGB图片转YUV420数据
     * 宽、高不能为奇数
     * @param pixels 图片像素集合
     * @param width 宽
     * @param height 高
     * @return
     */
    fun rgb2YCbCr420(pixels: IntArray, width: Int, height: Int): ByteArray {
        val len = width * height
        //yuv格式数组大小，y亮度占len长度，u,v各占len/4长度。
        val yuv = ByteArray(len * 3 / 2)
        var y: Int
        var u: Int
        var v: Int
        for (i in 0 until height) {
            for (j in 0 until width) {
                //屏蔽ARGB的透明度值
                val rgb = pixels[i * width + j] and 0x00FFFFFF
                //像素的颜色顺序为bgr，移位运算。
                val r = rgb and 0xFF
                val g = rgb shr 8 and 0xFF
                val b = rgb shr 16 and 0xFF
                //套用公式
                y = (66 * r + 129 * g + 25 * b + 128 shr 8) + 16
                u = (-38 * r - 74 * g + 112 * b + 128 shr 8) + 128
                v = (112 * r - 94 * g - 18 * b + 128 shr 8) + 128
                //调整
                y = if (y < 16) 16 else if (y > 255) 255 else y
                u = if (u < 0) 0 else if (u > 255) 255 else u
                v = if (v < 0) 0 else if (v > 255) 255 else v
                //赋值
                yuv[i * width + j] = y.toByte()
                yuv[len + (i shr 1) * width + (j and 1.inv()) + 0] = u.toByte()
                yuv[len + +(i shr 1) * width + (j and 1.inv()) + 1] = v.toByte()
            }
        }
        return yuv
    }
}