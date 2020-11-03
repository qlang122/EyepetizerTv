package com.qlang.eyepetizer.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.qlang.eyepetizer.base.BaseFragment

abstract class BaseVMFragment<VM : BaseViewModel>(val dataBinding: Boolean = false) : BaseFragment() {
    protected var binding: ViewDataBinding? = null
    protected lateinit var viewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (dataBinding) {
            binding = DataBindingUtil.inflate(inflater, getLayoutResources(), container, false)
            rootView = binding?.root
            rootView
        } else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = initVM()
        observe()
        binding?.setLifecycleOwner(this)
        super.onViewCreated(view, savedInstanceState)
    }

    abstract fun bindVM(): Class<VM>
    abstract fun observe()

    private fun initVM(): VM = ViewModelProvider(this).get(bindVM())
}