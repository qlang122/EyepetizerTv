package com.libs.utils

import android.Manifest
import android.annotation.SuppressLint
import android.view.WindowManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Environment
import java.io.File
import android.app.Activity
import android.app.ActivityManager
import android.content.res.Configuration
import android.text.TextUtils
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.provider.Settings
import android.telephony.TelephonyManager
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.DataOutputStream
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import java.util.regex.Pattern

/**
 * @author Created by qlang on 2017/12/12.
 */
object Utils {
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)

    /**
     * 取二维数组{@code A[n][2]}中与提供数组B[2]中值的近似值
     * @param inArr
     * @param suitable
     * @return
     */
    @JvmStatic
    fun getSuitableSize(inArr: Array<IntArray>, suitable: IntArray): IntArray {
        val array = Array(inArr.size) { IntArray(3) }
        for (i in inArr.indices) {
            for (j in 0..2) {
                if (j == 0) {
                    array[i][j] = inArr[i][j] * inArr[i][j + 1]
                } else {
                    array[i][j] = inArr[i][j - 1]
                }
            }
        }
        val tempArr = Array(inArr.size) { IntArray(3) }

        for (j in array.indices) {
            if (array[j][0] <= suitable[0] * suitable[1]) {
                tempArr[j][0] = array[j][0]
                tempArr[j][1] = array[j][1]
                tempArr[j][2] = array[j][2]
            }
        }

        val len = tempArr.size
        for (i in 0 until len - 1) {
            for (j in 0 until len - i - 1) {
                if (tempArr[j][0] > tempArr[j + 1][0]) {
                    val temp = tempArr[j][0]
                    val tV = intArrayOf(tempArr[j][1], tempArr[j][2])
                    tempArr[j][0] = tempArr[j + 1][0]
                    tempArr[j][1] = tempArr[j + 1][1]
                    tempArr[j][2] = tempArr[j + 1][2]
                    tempArr[j + 1][0] = temp
                    tempArr[j + 1][1] = tV[0]
                    tempArr[j + 1][2] = tV[1]
                }
            }
        }
//        for (j in tempArr.size - 1 downTo 1) {
//            Log.e("QL", tempArr[j][0].toString() + "|" + tempArr[j][1] + "|" + tempArr[j][2])
//        }
        return intArrayOf(tempArr[tempArr.size - 1][1], tempArr[tempArr.size - 1][2])
    }

    @JvmStatic
    fun isScreenLandscape(context: Context): Boolean {
        return context.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * 获取屏幕宽度
     */
    @JvmStatic
    fun getScreenWidth(context: Context?): Int {
        context ?: return 0

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val point = Point()
        wm?.defaultDisplay?.getRealSize(point)
        return point.x
    }

    /**
     * 获取屏幕高度
     */
    @JvmStatic
    fun getScreenHeight(context: Context?): Int {
        context ?: return 0

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val point = Point()
        wm?.defaultDisplay?.getRealSize(point)
        return point.y
    }

    /**
     * 获取屏幕宽度
     */
    @JvmStatic
    fun getWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        return wm?.defaultDisplay?.width ?: 0
    }

    /**
     * 获取屏幕高度
     */
    @JvmStatic
    fun getHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        return wm?.defaultDisplay?.height ?: 0
    }

    /**
     * 创建指定的文件路径
     */
    @JvmStatic
    fun createFolder(path: String) {
        var file: File? = null
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            file = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + path)
            if (!file.exists()) {
                file.mkdirs()
            }
        }
    }

    /**
     * 判断摄像头权限
     */
    @JvmStatic
    fun checkCameraPermission(mContext: Context): Boolean {
        val permisson = "android.permission.CAMERA"
        val res = ContextCompat.checkSelfPermission(mContext, permisson)
        return res == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permissionWRITE = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionWRITE != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 0)
        }
    }

    @JvmStatic
    fun verifyCameraPermissions(activity: Activity) {
        // Check if we have write permission
        val permissionCamera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CAMERA, 0)
        }
    }

    /**
     * 判断SD卡是否存在

     * @return
     */
    @JvmStatic
    fun isExistsSDCard(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 判断当前Activity是否在栈顶运行
     * @param context
     * *
     * @param cls
     * *
     * @return
     */
    @SuppressLint("NewApi")
    @JvmStatic
    fun isActivityRunning(context: Context?, cls: Class<*>): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val list = manager?.getRunningTasks(Integer.MAX_VALUE)
        return list?.any { it.topActivity?.className == cls.name } == true
    }

    @SuppressLint("NewApi")
    @JvmStatic
    fun isActivityRunning(context: Context?, clsName: String): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val list = manager?.getRunningTasks(Integer.MAX_VALUE)
        return list?.any { it.topActivity?.className == clsName } == true
    }

    /**
     * 判断当前Server是否在运行
     * @param context
     * *
     * @param cls
     * *
     * @return
     */
    @JvmStatic
    fun isServerRunning(context: Context?, cls: Class<*>): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val list = manager?.getRunningServices(Integer.MAX_VALUE)
        return list?.any { it.service.className == cls.name } == true
    }

    /**
     * 判断当前Server是否在运行
     * @param context
     * *
     * @param cls
     * *
     * @return
     */
    @JvmStatic
    fun isServerRunning(context: Context?, clsName: String): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val list = manager?.getRunningServices(Integer.MAX_VALUE)
        return list?.any { it.service.className == clsName } == true
    }

    @JvmStatic
    fun encode(key: String, data: String): String? {
        return encode(key, data.toByteArray())
    }

    /**
     * 加密
     *
     * @param data       待加密字符串
     * @param encryptKey 加密私钥，长度不能够小于8位
     *
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    @JvmStatic
    fun encode(encryptKey: String, data: ByteArray): String? {
        var str = ""
        try {
            val iv = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
            val zeroIv = IvParameterSpec(iv)
            val key = SecretKeySpec(encryptKey.toByteArray(), "DES")
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv)
            val encryptedData = cipher.doFinal(data)
            str = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }

    /**
     * 获取编码后的值
     *
     * @param key
     * @param data
     *
     * @return
     */
    @JvmStatic
    fun decodeValue(key: String, data: String): String? {
        val datas: ByteArray
        var value: String? = null
        if (TextUtils.isEmpty(data)) return ""

        try {
            datas = decode(key, data)
            value = String(datas)
        } catch (e: Exception) {
            value = ""
        }
        return value
    }

    /**
     * 解密
     *
     * @param decryptKey    待解密字符串
     * @param decryptString 解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     *
     * @throws Exception 异常
     */
    @JvmStatic
    fun decode(decryptKey: String, decryptString: String): ByteArray {
        var result = ByteArray(0)
        try {
            val iv = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
            val byteMi = Base64.decode(decryptString, Base64.DEFAULT)
            val zeroIv = IvParameterSpec(iv)
            val key = SecretKeySpec(decryptKey.toByteArray(), "DES")
            val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv)
            result = cipher.doFinal(byteMi)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * dp转换成px

     * @param context
     * *
     * @param dpValue
     * *
     * @return
     */
    @JvmStatic
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px转换成dp

     * @param context
     * *
     * @param pxValue
     * *
     * @return
     */
    @JvmStatic
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    @JvmStatic
    @SuppressLint("MissingPermission")
    fun getIMEI(context: Context): String {
        return try {
            (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.deviceId
                    ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
    }

    @JvmStatic
    fun getDeviceSN(): String {
        return android.os.Build.SERIAL
    }

    /**
     * 获取app版本号
     */
    @JvmStatic
    fun getAppVersionCode(context: Context): Int {
        var back = 0
        try {
            back = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return back
        }
    }

    /**
     * 获取app版本名称
     */
    @JvmStatic
    fun getAppVersionName(context: Context): String {
        var back = ""
        try {
            back = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return back
        }
    }

    /**
     * 获取当前手机系统版本号
     */
    @JvmStatic
    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取手机型号
     */
    @JvmStatic
    fun getSystemModel(): String {
        return Build.MODEL
    }

    /**
     * 正则表达式判断
     */
    @JvmStatic
    fun isMatcherStr(str: String, compileStr: String): Boolean {
        val p = Pattern.compile(compileStr)
        val m = p.matcher(str.trim { it <= ' ' })
        return m.matches()
    }

    /**
     * 隐藏软件盘
     */
    @JvmStatic
    fun hideKeyBoard(context: Context, view: View) {
        val imm: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @JvmStatic
    fun execCmd(command: String): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes(command + "\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                os?.close()
                process?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }
}