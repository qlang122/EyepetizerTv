package com.qlang.eyepetizer.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.view.MotionEvent
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(), View.OnTouchListener, View.OnClickListener {
    private var isFirst: Boolean = false
    protected open var rootView: View? = null
    private var isFragmentVisiable: Boolean = false
    protected open var isActing = false

    private val STATE_SAVE_OR_HIDDEN = "STATE_SAVE_OR_HIDDEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as? BaseActivity)?.addFragmentListener(this)
        super.onCreate(savedInstanceState)
        //处理内存重启导致的多个Fragment重叠问题
        if (savedInstanceState != null) {
            val isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_OR_HIDDEN)

            val ft = fragmentManager?.beginTransaction()
            if (isSupportHidden) ft?.hide(this)
            else ft?.show(this)

            ft?.commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = rootView ?: inflater.inflate(getLayoutResources(), container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(rootView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_OR_HIDDEN, isHidden)
        super.onSaveInstanceState(outState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            isFragmentVisiable = true
        }
        rootView ?: return

        //可见，并且没有加载过
        if (!isFirst && isFragmentVisiable) {
            onFragmentVisiableChange(true)
            return
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisiable) {
            onFragmentVisiableChange(false)
            isFragmentVisiable = false
        }
    }

    open protected fun onFragmentVisiableChange(b: Boolean) {

    }

    abstract fun getLayoutResources(): Int

    abstract fun initView(rootView: View?)

    open fun onUserConfigChanged() {

    }

    open fun userConfigChanged() {
        (activity as? BaseActivity)?.userConfigChanged()
    }

    fun showToast(str: String, duration: Int = Toast.LENGTH_SHORT) = (activity as? BaseActivity)?.showToast(str, duration)

    fun showToast(str: Int, duration: Int = Toast.LENGTH_SHORT) = (activity as? BaseActivity)?.showToast(str, duration)

    fun showProgress(msg: String, cancleable: Boolean = true, onTouchCancleable: Boolean = false) = (activity as? BaseActivity)?.showProgress(msg, cancleable, onTouchCancleable)

    fun hideProgress() {
        (activity as? BaseActivity)?.hideProgress()
        hideNavigationBar()
    }

    override fun onResume() {
        super.onResume()
        isActing = true
    }

    override fun onPause() {
        super.onPause()
        isActing = true
    }

    override fun onStop() {
        isActing = false
        super.onStop()
    }

    override fun onDestroy() {
        isActing = false
        (activity as? BaseActivity)?.removeFragmentListener(this)
        super.onDestroy()
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    //解决点击穿透
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {

    }

    protected fun <T : View> setOnClickListener(vararg views: T?) {
        views.forEach { it?.setOnClickListener(this@BaseFragment) }
    }

    protected fun hideNavigationBar() {
        (activity as? BaseActivity)?.hideNavigationBar()
    }
}
