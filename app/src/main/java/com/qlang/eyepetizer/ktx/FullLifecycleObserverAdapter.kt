package com.qlang.eyepetizer.ktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class FullLifecycleObserverAdapter : LifecycleEventObserver {
    private var mFullLifecycleObserver: FullLifecycleObserver? = null
    private var mLifecycleEventObserver: LifecycleEventObserver? = null

    constructor(fullLifecycleObserver: FullLifecycleObserver?,
                lifecycleEventObserver: LifecycleEventObserver? = null) {
        mFullLifecycleObserver = fullLifecycleObserver
        mLifecycleEventObserver = lifecycleEventObserver
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> mFullLifecycleObserver?.onCreate(source)
            Lifecycle.Event.ON_START -> mFullLifecycleObserver?.onStart(source)
            Lifecycle.Event.ON_RESUME -> mFullLifecycleObserver?.onResume(source)
            Lifecycle.Event.ON_PAUSE -> mFullLifecycleObserver?.onPause(source)
            Lifecycle.Event.ON_STOP -> mFullLifecycleObserver?.onStop(source)
            Lifecycle.Event.ON_DESTROY -> mFullLifecycleObserver?.onDestroy(source)
            Lifecycle.Event.ON_ANY -> {
            }
        }
        mLifecycleEventObserver?.onStateChanged(source, event)
    }
}