package com.qlang.eyepetizer.ktx

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * 使用协程进行异步任务，IO线程执行，UI线程返回 ，或者可以使用[disp]指定调度线程；
 * 可通过[job]自行任务管理，或使用[owner]自动监听生命周期。
 *
 * @param block 执行块
 * @param rtn 返回监听
 * @param disp 调度者
 * @param job 任务
 * @param owner 生命周期监听，如果自行通过[job]取消，可不传此值
 */
fun <T, R> T.execAsync(block: suspend T.() -> R, rtn: ((R) -> Unit)? = null,
                       owner: LifecycleOwner? = null, job: Job? = null,
                       disp: CoroutineDispatcher = Dispatchers.Main): T {
    val _job by lazy { job ?: Job() }
    owner?.lifecycle?.addObserver(FullLifecycleObserverAdapter(object : FullLifecycleObserverImpl() {
        override fun onStop(owner: LifecycleOwner) {
            _job.cancel()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            _job.cancel()
        }
    }))
    val result = CoroutineScope(_job).async(Dispatchers.IO) { block() }
    rtn?.let { CoroutineScope(_job).launch(disp) { it.invoke(result.await()) } }
    return this
}