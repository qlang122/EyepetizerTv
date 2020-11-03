package com.qlang.eyepetizer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qlang.eyepetizer.R
import com.qlang.eyepetizer.base.BaseAdapter
import com.qlang.eyepetizer.base.ViewHolder
import com.qlang.eyepetizer.bean.*
import com.qlang.eyepetizer.net.loadImg

class HomeListAdapter(context: Context, val list: List<BaseInfo>) : BaseAdapter<BaseInfo>(context) {
    override fun getLayoutResource(): Int = R.layout.item_empty

    override fun getItemViewType(position: Int): Int {
        return ItemTypeHelper.getItemViewType(list[position])
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ItemType.SQUARE_CARD_COLLECTION,
            ItemType.HORIZONTAL_SCROLL_CARD -> HorizontalBannerViewHolder(LayoutInflater.from(context).inflate(
                    R.layout.item_horizontal_scroll_card, parent, false))
            ItemType.FOLLOW_CARD,
            ItemType.VIDEO_SMALL_CARD -> FollowCardViewHolder(LayoutInflater.from(context).inflate(
                    R.layout.item_follow_card_type, parent, false))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.plus(0L)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = holder.adapterPosition.let { list[it] }

        holder.run {
            itemView.tag = position
            when (this) {
                is HorizontalBannerViewHolder -> {
                    findView(R.id.rv_list, RecyclerView(context))?.run {
                        isFocusable = false
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        val itemList = when (data) {
                            is HomeRecommendInfo -> data.data?.itemList ?: emptyList()
                            else -> emptyList()
                        }
                        adapter = BannerAdapter(context, itemList).apply {
                            this@HomeListAdapter.listener?.let { setOnItemClickListener(it) }
                        }
//                        viewBorder?.attachTo(this)
                    }
                }
                is FollowCardViewHolder -> bindFollowCardHolder(data, holder)
            }
            ""
        }
    }

    private fun bindFollowCardHolder(data: BaseInfo, holder: ViewHolder) {
        holder.run {
            var cover: String? = ""
            var library: String? = ""
            var userType: String? = ""
            var time: Int = 0
            var head: String? = ""
            var title: String? = ""
            var desc: String? = ""
            when (data) {
                is HomeRecommendInfo -> {
                    cover = data.data?.cover?.feed.let {
                        if (it.isNullOrEmpty()) data.data?.content?.data?.cover?.feed else it
                    }
                    library = data.data?.content?.data?.library
                    userType = data.data?.content?.data?.owner?.userType
                    time = data.data?.duration ?: data.data?.content?.data?.duration ?: 0
                    head = data.data?.author?.icon.let {
                        if (it.isNullOrEmpty()) data.data?.content?.data?.author?.icon.let {
                            if (it.isNullOrEmpty()) data.data?.header?.icon else it
                        } else it
                    }
                    title = data.data?.title.let {
                        if (it.isNullOrEmpty()) data.data?.content?.data?.title.let {
                            if (it.isNullOrEmpty()) data.data?.header?.title else it
                        } else it
                    }
                    desc = data.data?.description.let {
                        if (it.isNullOrEmpty()) data.data?.content?.data?.description.let {
                            if (it.isNullOrEmpty()) data.data?.header?.description else it
                        } else it
                    }
                }
                is DiscoverInfo -> {
                    cover = data.data?.cover?.feed.let {
                        if (it.isNullOrEmpty()) data.data?.content?.data?.cover?.feed else it
                    }
                    library = data.data?.content?.data?.library
                    time = data.data?.duration ?: data.data?.content?.data?.duration ?: 0
                    title = data.data?.title.let {
                        if (it.isNullOrEmpty()) data.data?.content?.data?.title.let {
                            if (it.isNullOrEmpty()) data.data?.header?.title else it
                        } else it
                    }
                }
                is DailyInfo -> {
                    cover = data.data?.content?.data?.cover?.feed
                    library = data.data?.content?.data?.library
                    time = data.data?.content?.data?.duration ?: 0
                    title = data.data?.content?.data?.title.let {
                        if (it.isNullOrEmpty()) data.data?.header?.title else it
                    }
                }
                is FollowInfo -> {
                    cover = data.data?.content?.data?.cover?.feed
                    library = data.data?.content?.data?.library
                    time = data.data?.content?.data?.duration ?: 0
                    title = data.data?.content?.data?.title.let {
                        if (it.isNullOrEmpty()) data.data?.header?.title else it
                    }
                }
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
                    focusListener?.invoke(position, v, hasFocus, data)
                }
            }
        }
    }
}