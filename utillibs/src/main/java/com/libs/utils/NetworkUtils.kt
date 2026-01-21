package com.libs.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.util.Log
import java.net.*

/**
 * @author Created by qlang on 2017/7/14.
 */
object NetworkUtils {

    @JvmStatic
    fun isNetConneted(context: Context): Boolean {
        val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectManager?.activeNetworkInfo
        return if (networkInfo == null) {
            false
        } else {
            networkInfo.isAvailable && networkInfo.isConnected
        }
    }

    @JvmStatic
    fun isNetworkConnected(context: Context, typeMoblie: Int): Boolean {
        if (!isNetConneted(context)) {
            return false
        }
        val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectManager?.getNetworkInfo(typeMoblie)
        return if (networkInfo == null) {
            false
        } else {
            networkInfo.isConnected && networkInfo.isAvailable
        }
    }

    @JvmStatic
    fun isPhoneNetConnected(context: Context): Boolean {
        return isNetworkConnected(context, ConnectivityManager.TYPE_MOBILE)
    }

    @JvmStatic
    fun isWifiNetConnected(context: Context): Boolean {
        return isNetworkConnected(context, ConnectivityManager.TYPE_WIFI)
    }

    @JvmStatic
    fun getLocalIpAddress(context: Context): String? {
        val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo
        if (null != info?.isConnected && info.isConnected) {
            when {
                info.type == ConnectivityManager.TYPE_MOBILE -> //当前使用2G/3G/4G网络
                    try {
                        val en = NetworkInterface.getNetworkInterfaces()
                        while (en.hasMoreElements()) {
                            val intf = en.nextElement()
                            val enumIpAddr = intf.inetAddresses
                            while (enumIpAddr.hasMoreElements()) {
                                val inetAddress = enumIpAddr.nextElement()
                                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                    return inetAddress.getHostAddress()
                                }
                            }
                        }
                    } catch (e: SocketException) {
                        e.printStackTrace()
                    }
                info.type == ConnectivityManager.TYPE_WIFI -> {//当前使用无线网络
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                    val wifiInfo = wifiManager?.connectionInfo
                    return intIP2StringIP(wifiInfo?.ipAddress ?: 0)
                }
                else -> return getHostIP()
            }
        } else {
            //当前无网络连接
        }
        return null
    }

    private fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }

    @JvmStatic
    fun getHostIP(): String? {
        var hostIp: String? = null
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            var ia: InetAddress? = null
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement() as NetworkInterface
                val ias = ni.inetAddresses
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement()
                    if (ia is Inet6Address) {
                        continue// skip ipv6
                    }
                    val ip = ia?.hostAddress
                    if ("127.0.0.1" != ip) {
                        hostIp = ia.hostAddress
                        break
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return hostIp
    }

    @JvmStatic
    fun isWifi(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val info = manager?.activeNetworkInfo
        if (manager?.backgroundDataSetting == false) {
            return false
        }
        return ConnectivityManager.TYPE_WIFI == info?.type && info?.isConnectedOrConnecting == true
    }

    @SuppressLint("MissingPermission")
    @JvmStatic
    fun isWifiEnabled(context: Context): Boolean {
        val mgrConn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val mgrTel = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val buf = StringBuffer().apply {
            mgrTel?.let {
                try {
                    append("simSerialNumber->${it.simSerialNumber}\n")
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        Log.e("QL", "--------$buf")
        return mgrConn?.activeNetworkInfo?.state == NetworkInfo.State.CONNECTED ||
                mgrTel?.networkType == TelephonyManager.NETWORK_TYPE_UMTS
    }

    @JvmStatic
    fun isMobileAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val info = manager?.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return info?.isAvailable == true
    }

    fun isMobile(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val info = manager?.activeNetworkInfo
        return when (info?.type) {
            ConnectivityManager.TYPE_MOBILE,
            ConnectivityManager.TYPE_MOBILE_MMS,
            ConnectivityManager.TYPE_MOBILE_SUPL,
            ConnectivityManager.TYPE_MOBILE_DUN,
            ConnectivityManager.TYPE_MOBILE_HIPRI -> true
            else -> false
        }
    }

    /**
     * 获取扫描到的wifi列表
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getAroundWifi(context: Context): List<ScanResult>? {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        wifi?.startScan()
        return wifi?.scanResults
    }

    /**
     * 获取子网掩码
     */
    @JvmStatic
    fun getNetworkMask(context: Context): String? {
        val mWifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        var di: DhcpInfo? = mWifiManager?.dhcpInfo
        val ip = di?.netmask
        return ip?.let { intIP2StringIP(it) }
    }

    /**
     * 获取网关
     */
    @JvmStatic
    fun getGatway(context: Context): String? {
        val mWifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val di: DhcpInfo? = mWifiManager?.dhcpInfo
        val ip = di?.gateway
        return ip?.let { intIP2StringIP(it) }
    }

    @JvmStatic
    fun getWifiMac(context: Context): String? {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        return wifi?.connectionInfo?.macAddress
    }

    @JvmStatic
    fun getRouterWifiMac(context: Context): String? {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        return wifi?.connectionInfo?.bssid
    }

    @JvmStatic
    fun getWifiName(context: Context): String? {
        val mWifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        return mWifiManager?.connectionInfo?.ssid
    }


}