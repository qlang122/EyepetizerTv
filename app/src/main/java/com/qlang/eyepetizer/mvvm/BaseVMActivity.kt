package com.qlang.eyepetizer.mvvm

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.qlang.eyepetizer.base.BaseActivity

abstract class BaseVMActivity<VM : BaseViewModel>(val dataBinding: Boolean = true) : BaseActivity() {
    protected var binding: ViewDataBinding? = null
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = initVM()
        observe()
        if (dataBinding) {
            binding = DataBindingUtil.setContentView(this, getContentLayoutId())
            binding?.setLifecycleOwner(this)
        } else setContentView(getContentLayoutId())
    }

    override fun setContentView(): Boolean {
        return false
    }

    abstract fun bindVM(): Class<VM>
    abstract fun observe()

    private fun initVM(): VM = ViewModelProvider(this).get(bindVM())

}