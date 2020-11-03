package com.qlang.eyepetizer.net

import android.content.Context

import java.security.SecureRandom
import java.security.cert.X509Certificate

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author Created by qlang on 2018/2/7.
 */

object SslContextFactory {
    @JvmStatic
    fun getEmptySslSocket(context: Context): SSLContext? {
        val trusts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

        })
        return SSLContext.getInstance("TLS").apply { init(null, trusts, SecureRandom()) }
    }
}
