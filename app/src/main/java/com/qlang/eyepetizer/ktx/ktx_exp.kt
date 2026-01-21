@file:JvmMultifileClass
@file:JvmName("ExpandKtx")

package com.qlang.eyepetizer.ktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

/**
 * 使用协程进行异步任务，IO线程执行，UI线程返回 ，或者可以使用[disp]指定调度线程；
 * 可通过[job]自行任务管理，或使用[owner]自动监听生命周期（重要：使用[owner]时需在主线程调用）。
 *
 * @param onFinish 执行块执行结束或主动[MyContextScope.post]的监听
 * @param owner 生命周期监听，如果自行通过[job]取消，可不传此值
 * @param job 任务
 * @param disp 调度者
 * @param onCancel 被取消回调
 * @param block 执行块
 */
fun <T, R> T.execAsync(
    onFinish: ((R?) -> Unit)? = null,
    owner: LifecycleOwner? = null,
    job: Job? = null,
    disp: CoroutineDispatcher = Dispatchers.Main,
    onCancel: (() -> Unit)? = null,
    onFinally: (() -> Unit)? = null,
    block: suspend T.(MyContextScope) -> R?
): T {
    var isCanceled = false
    val _job = job ?: Job()

    owner?.lifecycle?.addObserver {
        when (it) {
            Lifecycle.Event.ON_DESTROY -> {
                _job.cancel()
                if (!isCanceled) {
                    isCanceled = true
                    onCancel?.invoke()
                }
            }

            else -> {}
        }
    }

    fun makeBack(value: R?) {
        val back = onFinish ?: return
        MyCoroutineScope<R>(_job).launch(disp) { back(value) }
    }

    MyCoroutineScope<R>(_job) { makeBack(it) }.run {
        launch(Dispatchers.IO) {
            try {
                val result = block(this@run)
                onFinish?.let { makeBack(result) }
            } catch (e: CancellationException) {
                onCancel?.takeIf { !isCanceled }?.let {
                    isCanceled = true
                    MainScope().launch { onCancel.invoke() }
                }
            } finally {
                onFinally?.let {
                    MainScope().launch { onFinally.invoke() }
                }
            }
        }
    }
    return this
}

fun Lifecycle.addObserver(block: LifecycleOwner.(event: Lifecycle.Event) -> Unit) {
    this.addObserver(LifecycleOnChanged(block))
}

@Suppress("FunctionName")
private fun LifecycleOnChanged(
    block: LifecycleOwner.(event: Lifecycle.Event) -> Unit,
): LifecycleObserver = LifecycleEventObserver { source, event -> block(source, event) }

@Suppress("FunctionName")
private fun <R> MyCoroutineScope(
    context: CoroutineContext,
    onNext: (CoroutineScope.(R?) -> Unit) = {}
): MyContextScope = MyContextScope(if (context[Job] != null) context else context + Job()) {
    onNext(it as? R?)
}

class MyContextScope(
    context: CoroutineContext,
    private val onNext: (CoroutineScope.(Any?) -> Unit)? = null,
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = context
    override fun toString(): String = "CoroutineScope(coroutineContext=$coroutineContext)"

    /**
     * 模拟触发完成，会执行[execAsync]的完成[onFinish]回调
     */
    fun <R> post(value: R?) {
        onNext?.invoke(this, value)
    }
}

fun newJob() = Job()

private val _mutex = Mutex()
suspend fun <T, R> T.suspendLock(
    mutex: Mutex = _mutex,
    block: T.() -> R
): R {
    return mutex.withLock { block(this) }
}