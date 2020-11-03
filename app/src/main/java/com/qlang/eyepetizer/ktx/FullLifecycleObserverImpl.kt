package com.qlang.eyepetizer.ktx

import androidx.lifecycle.LifecycleOwner

abstract class FullLifecycleObserverImpl : FullLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
    }

    override fun onStart(owner: LifecycleOwner) {
    }

    override fun onResume(owner: LifecycleOwner) {
    }

    override fun onPause(owner: LifecycleOwner) {
    }

    override fun onStop(owner: LifecycleOwner) {
    }

    override fun onDestroy(owner: LifecycleOwner) {
    }
}