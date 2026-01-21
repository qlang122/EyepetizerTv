package com.qlang.eyepetizer.ui.adapter

import android.content.Context
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.base.BaseAdapter
import com.qlang.eyepetizer.base.ViewHolder
import com.qlang.eyepetizer.bean.TabTitleInfo

class TabTitleAdapter(context: Context, val list: List<TabTitleInfo>) :
    BaseAdapter<TabTitleInfo>(context) {
    private var currentIndex = -1

    override fun getLayoutResource() = R.layout.item_tab_title

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.run {
            val cb = findView(R.id.cb_txt, checkBox)?.also { it.text = data.name }
            val lyRoot = findView(R.id.ly_root, view)?.also {
                it.setOnClickListener {
                    it.requestFocus()
                    listener?.invoke(position, it, data)
                }
                it.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) currentIndex = position
                    focusListener?.invoke(position, v, hasFocus, data)
                }
                it.viewTreeObserver.addOnGlobalLayoutListener {
                    cb?.isChecked = currentIndex == position
                }
            }
            findView(R.id.v_click_mask, view)?.setOnClickListener {
                lyRoot?.requestFocus()
                currentIndex = position
                listener?.invoke(position, it, data)
            }
        }
    }
}