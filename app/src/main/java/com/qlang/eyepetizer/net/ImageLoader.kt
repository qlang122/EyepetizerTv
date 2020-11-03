package com.qlang.eyepetizer.net

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.config.trycatch
import java.lang.Exception

/**
 * @author Created by qlang on 2017/7/19.
 */
object ImageLoader {

    interface BitmapListener {
        fun onCall(bitmap: Bitmap?)
    }

    @JvmStatic
    fun <T : ImageView> loadImg(context: Context, id: String, bytes: ByteArray, imageView: T?) {
        if (null == imageView) return
        trycatch {
            Glide.with(context).load(bytes, id)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(imageView)
        }
    }

    @JvmStatic
    fun <T : ImageView> loadImg(context: Context, url: String, imageView: T?) {
        loadImg(context, url, imageView, R.drawable.ic_default_img, R.drawable.ic_default_img)
    }

    @JvmStatic
    fun <T : ImageView> loadBigImg(context: Context, url: String, imageView: T?) {
        imageView ?: return

        trycatch {
            Glide.with(context).load(url)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_default_img)
                    .error(R.drawable.ic_default_img)
                    .into(imageView)
        }
    }

    @JvmStatic
    fun <T : ImageView> loadImg(context: Context, url: String, imageView: T?,
                                @DrawableRes defaultImageResId: Int, @DrawableRes errImageResId: Int) {
        imageView ?: return
        trycatch {
            Glide.with(context).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(defaultImageResId)
                    .error(errImageResId)
//                    .dontAnimate()
                    .crossFade().into(imageView)
        }
    }

    @JvmStatic
    fun loadImg(context: Context, url: String, @DrawableRes defaultImageResId: Int,
                @DrawableRes errImageResId: Int, listener: (Bitmap?) -> Unit) {
        trycatch {
            Glide.with(context).load(url)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(defaultImageResId)
                    .error(errImageResId)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            listener(resource)
                        }

                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            listener(BitmapFactory.decodeResource(context.resources, errImageResId))
                        }
                    })
        }
    }

    @JvmStatic
    fun loadImg(context: Context, url: String, listener: (Bitmap?) -> Unit, error: () -> Unit?) {
        trycatch {
            Glide.with(context).load(url)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            listener(resource)
                        }

                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            error()
                        }
                    })
        }
    }

    @JvmStatic
    fun loadImg(context: Context, url: String, listener: BitmapListener?) {
        trycatch {
            Glide.with(context).load(url)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            listener?.onCall(resource)
                        }
                    })
        }
    }
}

fun ImageView.loadImg(url: String, defImgId: Int = R.drawable.ic_default_img, errImgId: Int = R.drawable.ic_default_img) = ImageLoader.loadImg(this.context, url, this, defImgId, errImgId)

fun ImageView.loadImg(id: String, bytes: ByteArray) = ImageLoader.loadImg(this.context, id, bytes, this)

fun ImageView.loadImg(block: () -> Bitmap?) = this.setImageBitmap(block())

fun ImageView.loadImg(url: String, block: (Bitmap?) -> Unit, @DrawableRes defaultImageResId: Int = R.drawable.ic_default_img,
                      errImageResId: Int = R.drawable.ic_default_img) {
    ImageLoader.loadImg(this.context, url, defaultImageResId, errImageResId) { block(it) }
}
