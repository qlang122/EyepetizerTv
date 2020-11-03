package com.qlang.eyepetizer.base

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.*
import androidx.multidex.MultiDex
import com.qlang.eyepetizer.config.AppConfig
import com.qlang.eyepetizer.config.UserConfig
import com.qlang.eyepetizer.config.stopService
import com.qlang.eyepetizer.config.trycatch
import com.qlang.eyepetizer.service.CrashHandler
import java.util.*
import kotlin.collections.HashMap

/**
 * @author Created by qlang on 2019/3/13.
 */
class MyApp : Application() {
    private val TAG = "MyApp"
    private var activityStack: Stack<Activity> = Stack()
    private var serviceStack: Stack<Class<Service>> = Stack()

    private val configReceiver = ConfigChangeReceiver()

    private inner class ConfigChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AppConfig.ACT_APP_CONFIG_CHANGE_ACTION) {
                UserConfig.updateProperties()
                val params: HashMap<String, Any>? = intent.getSerializableExtra("params") as? HashMap<String, Any>?
                params?.forEach {
                    UserConfig.putValue(it.key, it.value)
                }
                activityStack.forEach { (it as? BaseActivity?)?.onConfigChange() }
            }
        }
    }

    companion object {
        lateinit var instance: MyApp

    }

    override fun onCreate() {
        instance = this
        super.onCreate()

        trycatch { registerReceiver(configReceiver, IntentFilter(AppConfig.ACT_APP_CONFIG_CHANGE_ACTION)) }

        CrashHandler.instance.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    fun finishActivity(activity: Activity?) {
        activity?.let { it.finish();activityStack.remove(it) }
    }

    fun finishAllActivity() {
        activityStack.let { it.forEach { it.finish() };it.clear() }
    }

    fun addService(cls: Class<Service>) {
        serviceStack.add(cls)
    }

    fun finishService(cls: Class<Service>, remove: Boolean = false) {
        stopService(cls)
        if (remove) serviceStack.remove(cls)
    }

    fun finishAllService() {
        serviceStack.let { it.forEach { finishService(it) };it.clear() }
    }

    fun finish() {

        trycatch {
            unregisterReceiver(configReceiver)
        }

//        stopService(FaceService::class.java)
    }
}