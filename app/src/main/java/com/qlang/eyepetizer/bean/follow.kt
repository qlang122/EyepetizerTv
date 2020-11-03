package com.qlang.eyepetizer.bean

/// 社区-关注
class FollowInfo(
        val data: FollowBody?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) : BaseInfo() {

}

class FollowBody(
        val adTrack: List<Any>?,
        val content: Content?,
        val dataType: String?,
        val header: Header?,
        val type: String?,
        val itemList: List<HomeRecommendInfo>?) : BaseInfo() {

}

