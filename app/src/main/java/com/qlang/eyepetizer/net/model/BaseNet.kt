package com.qlang.eyepetizer.net.model

import android.content.Context
import android.net.ParseException
import android.util.Log
import com.alibaba.fastjson.JSON
import com.google.gson.JsonParseException
import com.qlang.eyepetizer.BuildConfig
import com.qlang.eyepetizer.net.CacheInterceptor
import com.qlang.eyepetizer.net.SslContextFactory
import com.qlang.eyepetizer.net.api.IApiService
import com.qlang.eyepetizer.net.api.IHttp

import java.io.File
import java.util.concurrent.TimeUnit

import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.*
import javax.net.ssl.SSLHandshakeException

typealias OnSuccess<T> = (data: T?) -> Unit

typealias OnFaild = (msg: String, throwable: Throwable?) -> Unit

abstract class BaseNet(private val context: Context) {
    private val UNAUTHORIZED = 401
    private val FORBIDDEN = 403
    private val NOT_FOUND = 404
    private val REQUEST_TIMEOUT = 408
    private val INTERNAL_SERVER_ERROR = 500
    private val BAD_GATEWAY = 502
    private val SERVICE_UNAVAILABLE = 503
    private val GATEWAY_TIMEOUT = 504

    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null
    private var cache: Cache? = null
    private val DEFAULT_TIMEOUT: Long = 10

    private val serverUrl get() = IHttp.BASE_URL

    protected val apiServer by lazy { buildService(IApiService::class.java) }

    init {
        try {
            cache = cache ?: Cache(File(context.cacheDir, "app_cache"), (10 * 1024 * 1024).toLong())
        } catch (e: Exception) {
            Log.e("OKHttp", "Could not create http cache", e)
        }
        init()
    }

    private fun init() {
//        val sslSocketFactory = SslContextFactory.getSslSocket(context)?.socketFactory
        val sslSocketFactory = SslContextFactory.getEmptySslSocket(context)?.socketFactory

        okHttpClient = okHttpClient ?: OkHttpClient.Builder()
//                .apply { sslSocketFactory?.let { sslSocketFactory(it) } }
                .hostnameVerifier { hostname, session -> true }
                .cache(cache)
                .addInterceptor { chain ->
                    val builder = chain.request().newBuilder().apply {
                        header("User-Agent", System.getProperty("http.agent") ?: "unknown")
                        header("model", "Android")
                    }
                    chain.proceed(builder.build())
                }
                .addNetworkInterceptor(CacheInterceptor(context))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
                .apply {
                    if (BuildConfig.DEBUG) {
                        addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//打印
//                        proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.1.24", 8888)))
                    }
                }.build()

        retrofit = retrofit ?: Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient!!)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun <T> buildService(service: Class<T>?): T {
        if (service == null) {
            throw RuntimeException("Api service is null!")
        }
        if (null == retrofit) init()
        return retrofit!!.create(service)
    }

    fun <T> buildService(baseUrl: String, service: Class<T>): T {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient!!)
                .build()
        return retrofit.create(service)
    }

//    fun <T, R> T.execute(block: T.() -> Observable<R>): Observable<R> {
//        return block().subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun <R> Observable<R>.subscribe(onSuccess: (data: R?) -> Unit, onError: (msg: String, throwable: Throwable?) -> Unit) {
//        if (NetworkUtils.isNetConneted(context))
//            this.subscribe({ onSuccess(it) }, { onError(handleException(it), it) })
//        else onError(handleException(ConnectException()), ConnectException())
//    }

    suspend fun <T, R> T.call(block: suspend T.() -> R?): R? {
        return block.invoke(this)
    }

    suspend fun <T, R> T.safeCall(block: suspend T.() -> R?): Result<R> {
        return try {
            Result.success(block(this))
        } catch (e: Throwable) {
            val msg = handleException(e)
            Result.failure(e, msg)
        }
    }

    fun toGsonRequestBody(params: Any): RequestBody {
        val jsonString = JSON.toJSONString(params)
        return RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonString)
    }

    fun toGsonRequestBody(params: JSONObject): RequestBody {
        return RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), params.toString())
    }

    private fun handleException(e: Throwable): String {
        e.printStackTrace()
        return when (e) {
            is HttpException -> { //HTTP错误
                when (e.code()) {
                    REQUEST_TIMEOUT -> "请求超时"
                    GATEWAY_TIMEOUT -> "网关超时"
                    INTERNAL_SERVER_ERROR -> "服务器内部错误"
                    NOT_FOUND -> "请求不存在"
                    UNAUTHORIZED -> "认证出错"
                    BAD_GATEWAY -> "服务不可达"
                    FORBIDDEN -> "拒绝访问"
                    SERVICE_UNAVAILABLE -> "网络错误"
                    else -> "网络错误"
                }
            }
            is JsonParseException, is JSONException, is ParseException -> "数据解析错误"
            is ConnectException -> "网络连接错误"
            is UnknownHostException -> "连接服务失败"
            is SocketTimeoutException -> "连接超时"
            is ConnectTimeoutException -> "连接超时"
            is SSLHandshakeException -> "证书错误"
            else -> "未知错误"
        }
    }
}
