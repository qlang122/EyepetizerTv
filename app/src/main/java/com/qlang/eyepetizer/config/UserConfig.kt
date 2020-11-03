package com.qlang.eyepetizer.config

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.libs.utils.ByteUtils

import com.qlang.eyepetizer.base.MyApp
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

/**
 * @author Created by Qlang on 2017/5/26.
 */

object UserConfig {
    private val TAG = "UserConfig"

    private val FIRST_ENTER = "firstEnter"

    private val REGISTER_STATE = "registerState"

    private val SERVER_URL = "server_url"
    private val DEVICE_NAME = "device_name"
    private val VOICE_ENABLE = "voice_anable"

    private val DEVICE_SN = "d_sn"
    private val SECRET_KEY = "secret_key"

    private val USER_NAME = "user_name"
    private val USER_PWD = "user_pwd"
    private val USER_NICK = "nick_name"

    private val LAST_DEVICE_STATE = "last_device_state"

    private var mShared: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null

    private val dir = File("${Environment.getExternalStorageDirectory().absolutePath}/qlang/${MyApp.instance.packageName}").apply {
        if (!exists()) mkdirs()
    }
    private val configFile = File("${dir.absolutePath}/config").apply { if (!exists()) createNewFile() }
    private val properties by lazy { Properties().apply { load(FileInputStream(configFile)) } }

    private fun init() {
        mShared = mShared
                ?: MyApp.instance.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
        mEditor = mEditor ?: mShared?.edit()
    }

    fun getShared(): SharedPreferences? {
        mShared ?: init()
        return mShared
    }

    fun updateProperties() {
        mShared = MyApp.instance.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
        mEditor = mShared?.edit()
        properties.apply { load(FileInputStream(configFile)) }
    }

    @JvmStatic
    fun <T : Any> getExternalValue(key: String, tAndDefVal: T): T {
        var rtn: T? = tAndDefVal
        rtn = when (tAndDefVal) {
            is String -> (properties.getProperty(key) ?: tAndDefVal) as? T?
            is Int -> (properties.getProperty(key)?.toInt() ?: tAndDefVal) as? T?
            is Float -> (properties.getProperty(key)?.toFloat() ?: tAndDefVal) as? T?
            is Long -> (properties.getProperty(key)?.toLong() ?: tAndDefVal) as? T?
            is Boolean -> (properties.getProperty(key)?.toBoolean() ?: tAndDefVal) as? T?
            else -> (properties.getProperty(key) ?: tAndDefVal) as? T?
        }
        return rtn ?: tAndDefVal
    }

    @JvmStatic
    fun setExternalValue(key: String, `val`: Any) {
        try {
            properties.load(FileInputStream(configFile))
            when (`val`) {
                is String -> properties.setProperty(key, `val`)
                is Int -> properties.setProperty(key, "${`val`}")
                is Boolean -> properties.setProperty(key, `val`.toString())
                is Float -> properties.setProperty(key, "${`val`}")
                is Long -> properties.setProperty(key, "${`val`}")
                else -> properties.setProperty(key, "${`val`}")
            }
            val fos = FileOutputStream(configFile)
            properties.store(fos, null)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    var isFirstEnter: Boolean
        get() = getValue(FIRST_ENTER, 1) == 1
        set(`val`) = putValue(FIRST_ENTER, if (`val`) 1 else 0)

    @JvmStatic
    var deviceSn: String
        get() = getValue(DEVICE_SN, "")
        set(`val`) = putValue(DEVICE_SN, `val`)

    @JvmStatic
    var token: String
        get() = getValue("token", "")
        set(`val`) = putValue("token", `val`)

    @JvmStatic
    var deviceName: String
        get() {
            var value = getValue(DEVICE_NAME, "")
            if (value.isNullOrEmpty()) {
                val hexStr = getExternalValue(DEVICE_NAME, "")
                val b = ByteUtils.hexString2bytes(hexStr)
                value = b?.let { String(it) } ?: ""
            }
            return value
        }
        set(value) {
            putValue(DEVICE_NAME, value)
            val b = value.toByteArray()
            val str = ByteUtils.bytes2HexString(b) ?: ""
            setExternalValue(DEVICE_NAME, str)
        }

    @JvmStatic
    var isVoiceEnable: Boolean
        get() = getValue(VOICE_ENABLE, 1) == 1
        set(`val`) = putValue(VOICE_ENABLE, if (`val`) 1 else 0)

    @JvmStatic
    var userName: String
        set(`val`) = putValue(USER_NAME, `val`)
        get() = getValue(USER_NAME, "")

    @JvmStatic
    var userPwd: String
        set(`val`) = putValue(USER_PWD, `val`)
        get() = getValue(USER_PWD, "")

    @JvmStatic
    var nickName: String
        set(`val`) = putValue(USER_NICK, `val`)
        get() = getValue(USER_NICK, "")

    @JvmStatic
    fun putValue(key: String, `val`: Any) {
        try {
            mEditor ?: init()
            mEditor?.run {
                when (`val`) {
                    is String -> putString(key, `val`.toString())
                    is Int -> putInt(key, `val`)
                    is Boolean -> putBoolean(key, `val`)
                    is Float -> putFloat(key, `val`)
                    is Long -> putLong(key, `val`)
                    else -> putString(key, `val`.toString())
                }
                val b = commit()
//                Log.e("QL", "------putValue----->k:$key  v:${`val`} rtn:$b")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun <T : Any> getValue(key: String, tAndDefVal: T): T {
        try {
            mEditor ?: init()
            var rtn: T? = null
            mShared?.run {
                rtn = when (tAndDefVal) {
                    is String -> getString(key, tAndDefVal as? String? ?: "") as T?
                    is Int -> getInt(key, tAndDefVal as Int? ?: 0) as T?
                    is Float -> getFloat(key, tAndDefVal as Float? ?: 0f) as T?
                    is Long -> getLong(key, tAndDefVal as Long? ?: 0L) as T?
                    is Boolean -> getBoolean(key, tAndDefVal as Boolean? ?: false) as T?
                    else -> getString(key, tAndDefVal as String? ?: "") as T?
                }
            }
            return rtn ?: tAndDefVal
        } catch (e: Exception) {
            e.printStackTrace()
            return tAndDefVal
        }
    }
}
