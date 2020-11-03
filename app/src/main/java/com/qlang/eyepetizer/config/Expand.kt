package com.qlang.eyepetizer.config

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import com.libs.utils.MD5Utils
import com.libs.utils.Utils

/**
 * @author Created by qlang on 2019/3/13.
 */
fun Context.appSN() = MD5Utils.md5("${Utils.getDeviceSN()}${Utils.getAndroidId(this)}${UserConfig.deviceSn}")

inline fun <reified T : Activity> Context.startActivity() {
    startActivity(Intent(this, T::class.java))
}

fun Context.startService(vararg clazzs: Class<*>) {
    clazzs.forEach { clazz ->
        trycatch { startService(Intent(this, clazz)) }
    }
}

fun Context.stopService(vararg clazzs: Class<*>) {
    clazzs.forEach { clazz ->
        if (Utils.isServerRunning(this, clazz)) {
            trycatch { stopService(Intent(this, clazz)) }
        }
    }
}

fun Any?.isNull(): Boolean = null == this

inline fun <T, R> T.trycatch(block: T.() -> R?): R? {
    return try {
        block()
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

inline fun <reified T : Any> Gson.fromJson(json: String): T? = trycatch { this.fromJson(json, T::class.java) }

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

abstract class TextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

fun EditText.onTextChanged(action: (CharSequence?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action(s)
        }
    })
}