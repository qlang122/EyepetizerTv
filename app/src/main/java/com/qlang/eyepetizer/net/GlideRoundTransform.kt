package com.qlang.eyepetizer.net

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation


class GlideRoundTransform(context: Context, var radius: Double = 10.0) : BitmapTransformation(context) {
    init {
        radius *= Resources.getSystem().displayMetrics.density
    }

    override fun getId(): String = javaClass.name + Math.round(radius)

    override fun transform(pool: BitmapPool?, toTransform: Bitmap?, outWidth: Int, outHeight: Int): Bitmap? {
        return roundCrop(pool, toTransform)
    }

    private fun roundCrop(pool: BitmapPool?, source: Bitmap?): Bitmap? {
        if (source == null) return null

        var result: Bitmap? = pool?.get(source.width, source.height, Bitmap.Config.ARGB_8888)
        if (result == null) {
            result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(result!!)
        val paint = Paint()
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
        canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
        return result
    }
}