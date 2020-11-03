package com.qlang.eyepetizer.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log

import com.libs.utils.NetworkUtils
import com.qlang.eyepetizer.config.UserConfig
import com.qlang.eyepetizer.ktx.execAsync
import com.qlang.eyepetizer.net.model.DeviceNet

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap

/**
 * @author Created by RDKC on 2017/6/18.
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    // 系统默认的UncaughtException处理类
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    // 程序的Context对象
    private var mContext: Context? = null
    // 用来存储设备信息和异常信息
    private val infos = HashMap<String, String>()

    // 用于格式化日期,作为日志文件名的一部分
    private val nameString = "exception-log"

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        mContext = context
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler!!.uncaughtException(thread, ex)
        } else {
            ex.printStackTrace()
            Log.e(TAG, "程序异常---> ", ex)
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        ex.printStackTrace()

        // 收集设备参数信息
        collectDeviceInfo(mContext)
        // 保存日志文件
        object : Thread() {
            override fun run() {
                super.run()
                saveCrashInfo2File(ex)
            }
        }.start()
        return true
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    fun collectDeviceInfo(ctx: Context?) {
        try {
            val pm = ctx!!.packageManager
            val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                infos["versionName"] = versionName
                infos["versionCode"] = versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "an error occured when collect package info", e)
        }

        val fields = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                infos[field.name] = field.get(null)!!.toString()
                Log.d(TAG, field.name + " : " + field.get(null))
            } catch (e: Exception) {
                Log.e(TAG, "an error occured when collect crash info", e)
            }

        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private fun saveCrashInfo2File(ex: Throwable?): String? {

        val sb = StringBuffer()
        sb.append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date()))
        sb.append("\r\n")
        for ((key, value) in infos) {
            sb.append("$key = $value\r\n")
        }

        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex!!.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append(result)

        try {
            //            String time = formatter.format(new Date());
            val fileName = "$nameString.log"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val path = Environment.getExternalStorageDirectory().absolutePath
                val dir = File(path + File.separator + "nextstage")
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val file = File(dir.toString() + File.separator + fileName)
                if (file.exists() && file.isFile && file.length() > 20 * 1024 * 1024) file.delete()
                if (!file.exists()) {
                    file.createNewFile()
                }
                val fos = FileOutputStream(file, true)
                fos.write(sb.toString().toByteArray())
                fos.write("\r\n\r\n".toByteArray())//空行
                fos.close()

                saveToServer(file)
            }
            return fileName
        } catch (e: Exception) {
            Log.e(TAG, "an error occured while writing file...", e)
        }

        return null
    }

    @Throws(IOException::class)
    private fun saveToServer(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        val inputStream = FileInputStream(file)
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        inputStream.close()

        val msg = String(bytes, Charset.forName("UTF-8"))

        val map = HashMap<String, Any>()

        map["sn"] = UserConfig.deviceSn
        map["ip"] = "${NetworkUtils.getLocalIpAddress(mContext!!)}"
        map["code"] = 9
        map["message"] = msg

        execAsync({
            DeviceNet().uploadErrLog(map)
        }, {
            if (0 == it.value?.code) {
                //清空文件
                try {
                    val fos = FileOutputStream(file, false)
                    fos.write("\r\n\r\n".toByteArray())
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }

    companion object {
        val TAG = "CrashHandler"
        val instance = CrashHandler()
    }
}