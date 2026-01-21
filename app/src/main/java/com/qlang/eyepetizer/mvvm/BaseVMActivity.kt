package com.qlang.eyepetizer.mvvm

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.qlang.eyepetizer.base.BaseActivity

abstract class BaseVMActivity<DB : ViewDataBinding, VM : BaseViewModel>(
    val useBinding: Boolean = true
) : BaseActivity() {
    protected var binding: DB? = null
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = initVM()
        observe()
        if (useBinding) {
            binding = DataBindingUtil.setContentView(this, getContentLayoutId())
            binding?.setLifecycleOwner(this)
        } else if (setContentView()) {
            setContentView(getContentLayoutId())
        }
        initView()
    }

    override fun setContentView(): Boolean {
        return false
    }

    abstract fun bindVM(): Class<VM>
    abstract fun observe()

    open fun initView() {}

    private fun initVM(): VM = ViewModelProvider(this).get(bindVM())

}