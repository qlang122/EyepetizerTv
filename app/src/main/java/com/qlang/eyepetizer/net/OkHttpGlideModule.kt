package com.qlang.eyepetizer.net

import android.content.Context

import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.GlideModule

import java.io.InputStream
import okhttp3.OkHttpClient

/**
 * @author Created by qlang on 2018/12/3.
 */
class OkHttpGlideModule : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {}

    override fun registerComponents(context: Context, glide: Glide) {
        val socketFactory = SslContextFactory.getEmptySslSocket(context)?.socketFactory
        val mHttpClient = OkHttpClient().newBuilder()
                .apply { socketFactory?.let { sslSocketFactory(it) } }
                .hostnameVerifier { hostname, session -> true }
                .build()
        glide.register(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(mHttpClient))
    }
}
