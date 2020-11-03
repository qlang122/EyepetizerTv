package com.qlang.eyepetizer.ui.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.qlang.eyepetizer.base.BaseFragment


/**
 * @author Created by qlang on 2018/6/17.
 */
class MyFragmentPageAdapter(activity: FragmentActivity, val list: ArrayList<BaseFragment>) : FragmentStateAdapter(activity) {
    override fun getItemCount() = list.size

    override fun createFragment(position: Int) = list[position]
}