package com.qlang.eyepetizer.base

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import android.widget.Toast
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.libs.dialog.CustomViewDialog
import com.qlang.eyepetizer.config.trycatch
import com.qlang.eyepetizer.R
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 * @author Created by qlang on 2017/7/24.
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {
    private var toast: Toast? = null
    private var dialog: Dialog? = null

    lateinit var context: BaseActivity

    private val fragments = arrayListOf<BaseFragment>()
    protected var isActing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        MyApp.instance.addActivity(this)
        super.onCreate(savedInstanceState)
        isActing = true
        context = this
        if (setContentView()) setContentView(getContentLayoutId())
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initToolBar()
        initView()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        initToolBar()
        initView()
    }

    /**
     * Override this function and return [true], default to set current contentView.
     * or return [false] for autonomous control.
     */
    open fun setContentView(): Boolean {
        return true
    }

    abstract fun getContentLayoutId(): Int

    open fun initView() {}

    open fun onConfigChange() {}

    override fun onResume() {
        super.onResume()
        isActing = true
    }

    override fun onPause() {
        super.onPause()
        isActing = false
    }

    override fun onStop() {
        isActing = false
        super.onStop()
    }

    override fun onDestroy() {
        MyApp.instance.finishActivity(this)
        isActing = false
        super.onDestroy()
    }

    private fun initToolBar() {
        btn_toolBar_left?.setOnClickListener { onToolBarLeftItemClick(it) }
        btn_toolBar_right?.setOnClickListener { onToolBarRightItemClick(it) }
        btn_toolBar_right2?.setOnClickListener { onToolBarRightItem2Click(it) }
    }

    open fun onToolBarLeftItemClick(view: View) {}
    open fun onToolBarRightItemClick(view: View) {}
    open fun onToolBarRightItem2Click(view: View) {}

    fun setToolBarBgColor(@ColorRes resId: Int) {
        toolbar?.setBackgroundColor(resources.getColor(resId))
    }

    fun setToolBarBgColor(colorStr: String) {
        toolbar?.setBackgroundColor(Color.parseColor(colorStr))
    }

    protected fun hideToolBar() {
        toolbar?.visibility = View.GONE
    }

    protected fun showToolBar() {
        toolbar?.visibility = View.VISIBLE
    }

    protected fun showToolBarItem() {
        btn_toolBar_left?.visibility = View.VISIBLE
        btn_toolBar_right?.visibility = View.VISIBLE
        btn_toolBar_right2?.visibility = View.VISIBLE
    }

    protected fun hideToolBarItem() {
        btn_toolBar_left?.visibility = View.GONE
        btn_toolBar_right?.visibility = View.GONE
        btn_toolBar_right2?.visibility = View.GONE
    }

    protected fun showToolBarLeftItem() {
        btn_toolBar_left?.visibility = View.VISIBLE
    }

    protected fun hideToolBarLeftItem() {
        btn_toolBar_left?.visibility = View.GONE
    }

    protected fun showToolBarRightItem() {
        btn_toolBar_right?.visibility = View.VISIBLE
    }

    protected fun hideToolBarRightItem() {
        btn_toolBar_right?.visibility = View.GONE
    }

    protected fun showToolBarRight2Item() {
        btn_toolBar_right2?.visibility = View.VISIBLE
    }

    protected fun hideToolBarRight2Item() {
        btn_toolBar_right2?.visibility = View.GONE
    }

    protected fun setToolBarTitle(text: String) {
        tv_toolBar_title?.text = text
    }

    protected fun setToolBarTitle(@StringRes resId: Int) {
        tv_toolBar_title?.setText(resId)
    }

    protected fun setToolBarLeftItem(@DrawableRes resId: Int = -1, @StringRes strId: Int = -1, str: String? = null) {
        showToolBarLeftItem()
        val drawable = if (resId > 0) {
            resources.getDrawable(resId)?.also { it.setBounds(0, 0, it.minimumWidth, it.minimumHeight) }
        } else null
        btn_toolBar_left?.setCompoundDrawables(drawable, null, null, null)
        str?.let { btn_toolBar_left?.text = it }
        if (strId > 0) btn_toolBar_left?.text = resources.getString(strId)
    }

    protected fun setToolBarRightItem(@DrawableRes resId: Int = -1, @StringRes strId: Int = -1, str: String? = null) {
        showToolBarRightItem()
        val drawable = if (resId > 0) {
            resources.getDrawable(resId)?.also { it.setBounds(0, 0, it.minimumWidth, it.minimumHeight) }
        } else null
        btn_toolBar_right?.setCompoundDrawables(drawable, null, null, null)
        str?.let { btn_toolBar_right?.text = it }
        if (strId > 0) btn_toolBar_right?.text = resources.getString(strId)
    }

    protected fun setToolBarRight2Item(@DrawableRes resId: Int = -1, @StringRes strId: Int = -1, str: String? = null) {
        showToolBarRight2Item()
        val drawable = if (resId > 0) {
            resources.getDrawable(resId)?.also { it.setBounds(0, 0, it.minimumWidth, it.minimumHeight) }
        } else null
        btn_toolBar_right2?.setCompoundDrawables(drawable, null, null, null)
        str?.let { btn_toolBar_right2?.text = it }
        if (strId > 0) btn_toolBar_right2?.text = resources.getString(strId)
    }

    fun showToast(str: String, duration: Int = Toast.LENGTH_SHORT) {
        toast = toast ?: Toast.makeText(this, str, duration)
        toast?.setGravity(Gravity.BOTTOM, 0, 50)
        toast?.view = getToastView(str)

        toast?.show()
    }

    fun showToast(@StringRes str: Int, duration: Int = Toast.LENGTH_SHORT) {
        showToast(resources.getString(str), duration)
    }

    private fun getToastView(msg: String): View? {
        val view = layoutInflater?.inflate(R.layout.layout_toast, null)
        view?.findViewById<TextView>(R.id.tv_toast)?.text = msg
        return view
    }

    fun showToastView(@LayoutRes layout: Int) {
        toast = toast ?: Toast.makeText(this, "", Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.view = layoutInflater?.inflate(layout, null)

        toast?.show()
    }

    fun showProgress(msg: String, cancelable: Boolean = true, touchOutsideCancelable: Boolean = false) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading_view, null)
        val tipText = view.findViewById<TextView>(R.id.tipTextView)
        tipText.text = msg

        dialog = CustomViewDialog.newInstance(context, view, cancelable, touchOutsideCancelable) { _, _ -> }

        trycatch { dialog?.show() }
    }

    fun hideProgress() = trycatch { dialog?.cancel() }

    protected fun setOnClickListener(vararg v: View?) {
        v.forEach { it?.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {

    }

    fun addFragmentListener(frag: BaseFragment) {
        fragments.add(frag)
    }

    fun removeFragmentListener(frag: BaseFragment) {
        fragments.filter { frag == it }.forEach { fragments.remove(it) }
    }

    open fun onUserConfigChanged() {
    }

    open fun userConfigChanged() {
        fragments.forEach { it.onUserConfigChanged() }
        onUserConfigChanged()
    }

    fun setFullScreen() {
        val lp = window.attributes
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = lp
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    fun hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= 16) {
            val decorView = window.decorView
            var uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_IMMERSIVE
            }
            decorView.systemUiVisibility = uiOptions
        }
    }

    fun showNavigationBar() {
        if (Build.VERSION.SDK_INT >= 16) {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
            decorView.systemUiVisibility = uiOptions
        }
    }

    fun quitFullScreen() {
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}