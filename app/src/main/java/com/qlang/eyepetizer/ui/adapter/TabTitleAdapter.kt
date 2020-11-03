package com.qlang.eyepetizer.ui.adapter

import android.content.Context
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.base.BaseAdapter
import com.qlang.eyepetizer.base.ViewHolder
import com.qlang.eyepetizer.bean.TabTitleInfo

class TabTitleAdapter(context: Context, val list: List<TabTitleInfo>) : BaseAdapter<TabTitleInfo>(context) {
    private var currentIndex = -1

    override fun getLayoutResource() = R.layout.item_tab_title

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = holder.adapterPosition.let { list[it] }

        holder.run {
            val cb = findView(R.id.cb_txt, checkBox)?.apply { text = data.name }
            findView(R.id.ly_root, view)?.apply {
                setOnClickListener {
                    listener?.invoke(position, it, data)
                }
                setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) currentIndex = position
                    focusListener?.invoke(position, v, hasFocus, data)
                }
                viewTreeObserver.addOnGlobalLayoutListener {
                    cb?.isChecked = currentIndex == position
                }
            }
        }
    }
}