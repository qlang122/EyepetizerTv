package com.qlang.eyepetizer.ui.adapter

import android.content.Context
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.base.BaseAdapter
import com.qlang.eyepetizer.base.ViewHolder
import com.qlang.eyepetizer.bean.HomeRecommendInfo
import com.qlang.eyepetizer.net.loadImg

class BannerAdapter(context: Context, val list: List<HomeRecommendInfo>) : BaseAdapter<HomeRecommendInfo>(context) {
    override fun getLayoutResource() = R.layout.item_banner_item

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = holder.adapterPosition.let { list[it] }

        holder.run {
            val conver = data.data?.image ?: data.data?.content?.data?.cover?.feed
            ?: data.data?.cover?.feed
            conver?.let { findView(R.id.iv_picture, imageView)?.loadImg(it) }

            findView(R.id.tv_title, textView)?.text = data.data?.title
                    ?: data.data?.content?.data?.title ?: data.data?.header?.title ?: ""

            findView(R.id.ly_root, view)?.setOnClickListener {
                listener?.invoke(position, it, data)
            }
        }
    }
}