package com.qlang.eyepetizer.bean

/// 首页-日报
class DailyInfo(
        val data: DailyBody?,
        val type: String?,
        val tag: Any?,
        val id: Int?,
        val adIndex: Int?) : BaseInfo() {

}

class DailyBody(
        val actionUrl: String?,
        val adTrack: Any?,
        val backgroundImage: String?,
        val type: String?,
        val content: Content?,
        val dataType: String?,
        val follow: AuthorFollow?,
        val header: Header?,
        val id: Int?,
        val rightText: String?,
        val subTitle: Any?,
        val text: String?,
        val titleList: List<String>?,
        val image: String?,
        val label: Label?) : BaseInfo() {

}
