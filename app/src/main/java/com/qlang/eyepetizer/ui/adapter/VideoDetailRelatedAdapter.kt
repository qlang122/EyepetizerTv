package com.qlang.eyepetizer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.base.BaseAdapter
import com.qlang.eyepetizer.base.ViewHolder
import com.qlang.eyepetizer.bean.*
import com.qlang.eyepetizer.net.loadImg
import com.xiaweizi.marquee.MarqueeTextView

class VideoDetailRelatedAdapter(context: Context, val list: List<VideoRelated>) : BaseAdapter<VideoRelated>(context) {
    private val marqueeTextView = MarqueeTextView(context)

    override fun getLayoutResource() = R.layout.item_empty

    override fun getItemViewType(position: Int): Int {
        return ItemTypeHelper.getItemViewType(list[position])
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ItemType.FOLLOW_CARD,
            ItemType.VIDEO_SMALL_CARD -> FollowCardViewHolder(LayoutInflater.from(context).inflate(
                    R.layout.item_follow_card_type_small, parent, false))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = holder.adapterPosition.let { list[it] }

        holder.run {
            itemView.tag = position
            when (this) {
                is FollowCardViewHolder -> bindFollowCardHolder(data, holder)
            }
        }
    }

    private fun bindFollowCardHolder(data: VideoRelated, holder: ViewHolder) {
        holder.run {
            var cover: String? = data.data?.cover?.feed
            var library: String? = data.data?.library
            var userType: String? = ""
            var time: Int = data.data?.duration ?: 0
            var head: String? = ""
            var title: String? = ""
            var desc: String? = ""

            head = data.data?.author?.icon.let {
                if (it.isNullOrEmpty()) data.data?.header?.icon else it
            }
            title = data.data?.title.let {
                if (it.isNullOrEmpty()) data.data?.header?.title else it
            }
            desc = data.data?.description.let {
                if (it.isNullOrEmpty()) data.data?.header?.description else it
            }

            cover?.let { findView(R.id.iv_cover, imageView)?.loadImg(it) }
            findView(R.id.iv_library, view)?.visibility = if (LibraryType.TYPE_DAILY == library) View.VISIBLE else View.GONE
            findView(R.id.iv_avatarStar, view)?.visibility = if (UserType.TYPE_STAR == userType) View.VISIBLE else View.GONE
            findView(R.id.tv_videoDuration, textView)?.text = ItemTypeHelper.convertVideoTime(time)
            head?.let { url ->
                findView(R.id.iv_avatar, imageView)?.let { img -> img.loadImg(url, { img.setImageBitmap(it) }) }
            }
            findView(R.id.tv_title, textView)?.text = title ?: ""
            findView(R.id.tv_description, textView)?.text = desc ?: ""

            findView(R.id.ly_root, view)?.apply {
                setOnClickListener {
                    listener?.invoke(position, it, data)
                }
                setOnFocusChangeListener { v, hasFocus ->
                    findView(R.id.tv_title, marqueeTextView)?.apply {
                        if (hasFocus) startScroll() else stopScroll()
                    }
                    focusListener?.invoke(position, v, hasFocus, data)
                }
            }
        }
    }
}