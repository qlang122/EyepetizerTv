package com.qlang.eyepetizer.ui.adapter

import android.view.View
import com.qlang.eyepetizer.base.ViewHolder

class HorizontalBannerViewHolder(content: View) : ViewHolder(content) {
}

class FollowCardViewHolder(view: View) : ViewHolder(view) {
}

object LibraryType {
    const val TYPE_DEFAULT = "DEFAULT"
    const val TYPE_NONE = "NONE"
    const val TYPE_DAILY = "DAILY"
}
object UserType {
    const val TYPE_NORMAL = "NORMAL"
    const val TYPE_STAR = "STAR"
}