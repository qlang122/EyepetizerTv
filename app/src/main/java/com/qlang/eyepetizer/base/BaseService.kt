package com.qlang.eyepetizer.base

import android.app.Service

/**
 * @author Created by qlang on 2019/3/13.
 */
abstract class BaseService : Service() {
    override fun onCreate() {
        MyApp.instance.addService(javaClass)
        super.onCreate()
    }

    override fun onDestroy() {
        MyApp.instance.finishService(javaClass, true)
        super.onDestroy()
    }
}