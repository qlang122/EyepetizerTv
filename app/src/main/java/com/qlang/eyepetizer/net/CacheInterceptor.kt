package com.qlang.eyepetizer.net

import android.content.Context
import com.libs.utils.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.CacheControl

/**
 * @author Created by qlang on 2017/7/14.
 */
class CacheInterceptor(var context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (NetworkUtils.isNetConneted(context)) {
            val response = chain.proceed(request)
            val maxAge = 60
            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build()
        } else {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            val response = chain.proceed(request)
            val maxStale = 60 * 60 * 24 * 3
            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                .build()
        }
    }
}